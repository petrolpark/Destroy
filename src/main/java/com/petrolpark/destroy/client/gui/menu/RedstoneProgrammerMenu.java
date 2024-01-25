package com.petrolpark.destroy.client.gui.menu;

import org.jetbrains.annotations.NotNull;

import com.petrolpark.destroy.util.RedstoneProgram;
import com.petrolpark.destroy.util.RedstoneProgram.Channel;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class RedstoneProgrammerMenu extends GhostItemMenu<RedstoneProgram> {

    protected RedstoneProgrammerMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf buf) {
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
        return program;
    };

    @Override
    protected ItemStackHandler createGhostInventory() {
        return new ItemStackHandler(contentHolder.getChannels().size() * 2);
    };

    @Override
    protected boolean allowRepeats() {
        return true;
    };

    @Override
    protected void addSlots() {
        refreshSlots(0, 100, 19); //TODO determine actual max and channel spacing
    };

    public void refreshSlots(int offset, int max, int channelSpacing) {
        ghostInventory = createGhostInventory();
        int position = -offset;
        for (int channel = 0; channel < contentHolder.getChannels().size(); channel++) {
            position += channelSpacing;
            if (position < 0 || position > max) continue;
            addSlot(new FrequencySlotItemHandler(channel * 2, 0, position));
            addSlot(new FrequencySlotItemHandler(channel * 2 + 1, 18, position));
        };
    };

    public class FrequencySlotItemHandler extends SlotItemHandler {

        public FrequencySlotItemHandler(int index, int xPosition, int yPosition) {
            super(ghostInventory, index, xPosition, yPosition);
        };

        @Override
        public void set(@NotNull ItemStack stack) {
            if (stack != null) {
                Channel channel = contentHolder.getChannels().get(getSlotIndex() / 2);
                channel.networkKey.set(getSlotIndex() % 2 == 0, Frequency.of(stack));
                if (channel.networkKey.both(f -> f.getStack().isEmpty())) contentHolder.remove(channel);
            };
            super.set(stack);
        };

    };

    @Override
    protected void saveData(RedstoneProgram contentHolder) {

    };

    public static class DummyRedstoneProgram extends RedstoneProgram {

        @Override
        public boolean hasPower() {
            return false;
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

    };
    
};
