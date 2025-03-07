package com.petrolpark.destroy.content.oil.seismology;

import java.util.Arrays;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyAdvancementTrigger;
import com.petrolpark.destroy.client.DestroyGuiTextures;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT, modid = Destroy.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class SeismographItem extends MapItem {

    public SeismographItem(Properties properties) {
        super(properties);
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> openScreen(stack, usedHand));
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(usedHand));
    };

	@OnlyIn(value = Dist.CLIENT)
	protected void openScreen(ItemStack item, InteractionHand hand) {
		ScreenOpener.open(new SeismographScreen(item, hand));
	};

    public static Seismograph readSeismograph(ItemStack stack) {
        Seismograph seismograph = new Seismograph();
        if (!(stack.getItem() instanceof SeismographItem)) return seismograph;
        CompoundTag tag = stack.getOrCreateTag().getCompound("Seismograph");
        seismograph.rowsDiscovered = tag.getByte("RowsDiscovered");
        seismograph.columnsDiscovered = tag.getByte("ColumnsDiscovered");
        seismograph.rows = Arrays.copyOf(tag.getByteArray("Rows"), 8);
        seismograph.columns = Arrays.copyOf(tag.getByteArray("Columns"), 8);
        seismograph.marks = Arrays.copyOf(tag.getByteArray("Markings"), 64);
        return seismograph;
    };

    public static void writeSeismograph(ItemStack stack, Seismograph seismograph) {
        CompoundTag tag = new CompoundTag();
        tag.putByte("RowsDiscovered", seismograph.rowsDiscovered);
        tag.putByte("ColumnsDiscovered", seismograph.columnsDiscovered);
        tag.putByteArray("Rows", seismograph.rows);
        tag.putByteArray("Columns", seismograph.columns);
        tag.putByteArray("Markings", seismograph.marks);
        stack.getOrCreateTag().put("Seismograph", tag);
    };

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new SeismographItemRenderer()));
    };

    public static class Seismograph {

        private byte rowsDiscovered;
        private byte columnsDiscovered;
        private byte[] rows;
        private byte[] columns;
        private byte[] marks;

        public Seismograph() {
            rows = new byte[8];
            columns = new byte[8];
            marks = new byte[64];
        };

        public void clear() {
            rowsDiscovered = 0;
            columnsDiscovered = 0;
            rows = new byte[8];
            columns = new byte[8];
            marks = new byte[64];
        };

        public byte[] getRows() {
            return rows;
        };

        public byte[] getColumns() {
            return columns;
        };

        public byte[] getMarks() {
            return marks;
        };

        public Mark getMark(int x, int z) {
            if (x < 0 || x > 7 || z < 0 || z > 7) return Mark.NONE;
            return Mark.values()[marks[x * 8 + z]];
        };

        /**
         * @param x
         * @param z
         * @param mark
         * @return Whether the mark changed
         */
        public boolean mark(int x, int z, Mark mark) {
            if (x < 0 || x > 7 || z < 0 || z > 7) return false;
            boolean changed = marks[x * 8 + z] != (byte)mark.ordinal();
            marks[x * 8 + z] = (byte)mark.ordinal();
            return changed;
        };

        public int[] getColumnDisplayed(int column) {
            return getDisplayed(columns, column);
        };

        public int[] getRowDisplayed(int row) {
            return getDisplayed(rows, row);
        };

        public int[] getDisplayed(byte[] array, int index) {
            if (index < 0 || index > 7) return new int[0];
            int[] numbers = new int[4];
            int numbersAdded = 0;
            int count = 0;
            for (int i = 0; i <= 8; i++) { // Iterate one more time than needed so the last count gets added on
                if (i != 8 && (array[index] & (1 << i)) != 0) { // If there is something here
                    count++;
                } else if (count > 0) {
                    numbers[numbersAdded] = count;
                    numbersAdded++;
                    count = 0;
                };
            };
            return numbers;
        };

        /**
         * Let the Seismograph know we've collected data and filled in this row
         * @param row
         * @return {@code true} if that row had not been filled in already
         */
        public boolean discoverRow(int row, Level level, Player player) {
            if (row < 0 || row > 7) return false;
            byte oldRowsDiscovered = rowsDiscovered;
            rowsDiscovered |= 1 << row;
            triggerFillSeismographAdvancement(level, player);
            return oldRowsDiscovered != rowsDiscovered;
        };

        /**
         * Let the Seismograph know we've collected data and filled in this column
         * @param row
         * @return {@code true} if that column had not been filled in already
         */
        public boolean discoverColumn(int column, Level level, Player player) {
            if (column < 0 || column > 7) return false;
            byte oldColumnsDiscovered = columnsDiscovered;
            columnsDiscovered |= 1 << column;
            triggerSolveSeismographAdvancement(level, player);
            return oldColumnsDiscovered != columnsDiscovered;
        };

        public boolean isRowDiscovered(int row) {
            if (row < 0 || row > 7) return false;
            return (rowsDiscovered & 1 << row) != 0;
        };

        public boolean isColumnDiscovered(int column) {
            if (column < 0 || column > 7) return false;
            return (columnsDiscovered & 1 << column) != 0;
        };

        /**
         * Check if all columns and rows have been discovered and award an advancement if they have.
         * @param level
         * @param player
         * @return {@code true} if all columns and rows have been discovered, whether or not the player already has the advancement
         */
        private boolean triggerFillSeismographAdvancement(Level level, Player player) {
            if (rowsDiscovered == -1 && columnsDiscovered == -1) {
                if (level != null && player != null) DestroyAdvancementTrigger.FILL_SEISMOGRAPH.award(level, player);
                return true;
            };
            return false;
        };

        public void triggerSolveSeismographAdvancement(Level level, Player player) {
            if (triggerFillSeismographAdvancement(level, player)) {
                for (boolean test : Iterate.trueAndFalse) {
                    for (int x = 0; x < 8; x++) {
                        for (int z = 0; z < 8; z++) {
                            boolean shouldBeTicked = (rows[z] & 1 << x) != 0;
                            if (test) {
                                Mark mark = getMark(x, z);
                                if (mark == Mark.NONE) return;
                                if ((mark == Mark.TICK || mark == Mark.GUESSED_TICK) != shouldBeTicked) return;
                            } else {
                                mark(x, z, shouldBeTicked ? Mark.TICK : Mark.CROSS);
                            };
                        };
                    };
                };
                if (level != null && player != null) DestroyAdvancementTrigger.COMPLETE_SEISMOGRAPH.award(level, player);
            };
        };

        public static enum Mark {
            NONE(null),
            TICK(DestroyGuiTextures.SEISMOGRAPH_TICK),
            CROSS(DestroyGuiTextures.SEISMOGRAPH_CROSS),
            GUESSED_TICK(DestroyGuiTextures.SEISMOGRAPH_GUESSED_TICK),
            GUESSED_CROSS(DestroyGuiTextures.SEISMOGRAPH_GUESSED_CROSS);

            @Nullable
            public final DestroyGuiTextures icon;

            private Mark(DestroyGuiTextures icon) {
                this.icon = icon;
            };
        };
    };

    public static int mapChunkCenter(int chunkCoordinate) {
        return Mth.floor((chunkCoordinate + 4d) / 8d) * 8;
    };

    public static int mapChunkLowerCorner(int chunkCoordinate) {
        return mapChunkCenter(chunkCoordinate) - 4;
    };

    /** 
     * Render Seismographs like normal Items rather than Maps.
     * @param event
     */
    @SubscribeEvent
    public static void onRenderItemInFrame(RenderItemInFrameEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof SeismographItem) {
            ItemFrame frame = event.getItemFrameEntity();
            int light = frame.getType() == EntityType.GLOW_ITEM_FRAME ? 15728880 : event.getPackedLight();
            event.getPoseStack().scale(0.5f, 0.5f, 0.5f);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, event.getPoseStack(), event.getMultiBufferSource(), frame.level(), frame.getId());
            event.setCanceled(true);
        };
    };
    
};
