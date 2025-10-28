package com.petrolpark.destroy.content.redstone.programmer;

import java.util.WeakHashMap;

import net.createmod.catnip.data.Couple;
import org.jetbrains.annotations.NotNull;

import com.petrolpark.destroy.DestroyMessages;
import com.petrolpark.destroy.client.DestroyMenuTypes;
import com.petrolpark.destroy.client.IConditionalGhostSlot;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.content.redstone.programmer.RedstoneProgram.Channel;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

@EventBusSubscriber
public class RedstoneProgrammerMenu extends GhostItemMenu<RedstoneProgram> {

    public static final int SCREEN_ITEM_AREA_X = 3;
    public static final int SCREEN_ITEM_AREA_Y = 31;
    public static final int SCREEN_ITEM_AREA_WIDTH = 73;
    public static final int SCREEN_ITEM_AREA_HEIGHT = 154;
    public static final int SCREEN_DISTANCE_BETWEEN_CHANNELS = 20;

    private int offset = 0;

    public RedstoneProgrammerMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf buf) {
        super(type, id, inv, buf);
    };

    protected RedstoneProgrammerMenu(MenuType<?> type, int id, Inventory inv, RedstoneProgram contentHolder) {
        super(type, id, inv, contentHolder);
    };

    public static RedstoneProgrammerMenu create(int id, Inventory inv, RedstoneProgram program) {
        return new RedstoneProgrammerMenu(DestroyMenuTypes.REDSTONE_PROGRAMMER.get(), id, inv, program);
    };

    @Override
    protected RedstoneProgram createOnClient(FriendlyByteBuf extraData) {
        DummyRedstoneProgram program = new DummyRedstoneProgram();
        program.read(extraData);
        program.powered = extraData.readBoolean();
        return program;
    };

    @Override
    protected ItemStackHandler createGhostInventory() {
        return new ItemStackHandler(maxSlots(contentHolder));
    };

    @Override
    protected boolean allowRepeats() {
        return true;
    };

    @Override
    public boolean canDragTo(Slot slotIn) {
        return true;
    };

    @Override
    protected void addSlots() {
        refreshSlots(0); //TODO determine actual max and channel spacing
    };

    public void refreshSlots() {
        refreshSlots(this.offset);
    };

    public void refreshSlots(int offset) {
        ghostInventory = createGhostInventory();
        this.offset = offset;
        slots.clear();
        lastSlots.clear();
        remoteSlots.clear();
        int i = 0;
        int position = SCREEN_ITEM_AREA_Y - 16 - this.offset;
        for (int channel = 0; channel < Math.min(contentHolder.getChannels().size() + 1, DestroyAllConfigs.SERVER.blocks.redstoneProgrammerMaxChannels.get()); channel++) {
            position += SCREEN_DISTANCE_BETWEEN_CHANNELS;
            Slot slot1 = addSlot(new FrequencySlotItemHandler(i++, SCREEN_ITEM_AREA_X + 32, position, channel, true));
            Slot slot2 = addSlot(new FrequencySlotItemHandler(i++, SCREEN_ITEM_AREA_X + 50, position, channel, false));
            if (channel < contentHolder.getChannels().size()) {
                slot1.set(contentHolder.getChannels().get(channel).networkKey.getFirst().getStack());
                slot2.set(contentHolder.getChannels().get(channel).networkKey.getSecond().getStack());
            };
        };
    };

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        // Do nothing
    };

    public class FrequencySlotItemHandler extends SlotItemHandler implements IConditionalGhostSlot {

        public final int channelIndex;
        public final boolean first;

        public FrequencySlotItemHandler(int index, int xPosition, int yPosition, int channelIndex, boolean first) {
            super(ghostInventory, index, xPosition, yPosition);
            this.channelIndex = channelIndex;
            this.first = first;
        };

        @Override
        public void set(@NotNull ItemStack stack) {
            boolean sync = true;
            if (channelIndex >= contentHolder.getChannels().size()) { // Add a new channel
                if (stack.isEmpty()) {
                    sync = false; 
                } else {
                    Couple<Frequency> networkKey = Couple.create(Frequency.of(ItemStack.EMPTY), Frequency.of(ItemStack.EMPTY));
                    networkKey.set(first, Frequency.of(stack));
                    contentHolder.addBlankChannel(networkKey);
                };
            } else if (!contentHolder.getChannels().isEmpty()) {
                Channel channel = contentHolder.getChannels().get(channelIndex);
                if (channel.networkKey.get(first).getStack().equals(stack)) {
                    sync = false;
                } else {
                    channel.networkKey.set(first, Frequency.of(stack));
                    if (channel.networkKey.both(f -> f.getStack().isEmpty())) contentHolder.remove(channel);                    
                };
            } else {
                sync = false;
            };
            if (sync) {
                sync();
                refreshSlots();
            };
            super.set(stack);
        };

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public boolean isValid() {
            int position = SCREEN_DISTANCE_BETWEEN_CHANNELS * channelIndex - offset;
            return position > -1 && position < SCREEN_ITEM_AREA_HEIGHT - SCREEN_DISTANCE_BETWEEN_CHANNELS;
        };

    };

    public void sync() {
        DestroyMessages.sendToServer(new RedstoneProgramSyncC2SPacket(contentHolder));
    };

    @Override
    protected void saveData(RedstoneProgram contentHolder) {

    };

    public static int maxSlots(RedstoneProgram program) {
        return 2 * Math.min(program.getChannels().size() + 1, DestroyAllConfigs.SERVER.blocks.redstoneProgrammerMaxChannels.get());
    };

    public static class DummyRedstoneProgram extends RedstoneProgram {

        public boolean powered;

        @Override
        public void load() {
            // Do nothing, this should never be on a network
        };

        @Override
        public boolean hasPower() {
            return powered;
        };

        @Override
        public BlockPos getBlockPos() {
            return null;
        };

        @Override
        public boolean shouldTransmit() {
            return false;
        };

        @Override
        public LevelAccessor getWorld() {
            return null;
        };

        @Override
        public void read(FriendlyByteBuf buf) {
            super.read(buf);
        };

    };

    private static WeakHashMap<ServerPlayer, Boolean> programmersPowered = new WeakHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == Phase.END && event.player instanceof ServerPlayer player) {
            if (player.containerMenu instanceof RedstoneProgrammerMenu menu) {
                boolean currentPower = menu.contentHolder.hasPower();
                Boolean oldPower = programmersPowered.get(player);
                if (oldPower == null || currentPower != oldPower) {
                    DestroyMessages.sendToClient(new RedstoneProgrammerPowerChangedS2CPacket(menu.contentHolder.hasPower()), player);
                    programmersPowered.put(player, currentPower);
                };
            } else {
                programmersPowered.remove(player);
            };
        };
    };
    
};
