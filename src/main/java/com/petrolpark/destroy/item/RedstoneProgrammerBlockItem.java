package com.petrolpark.destroy.item;

import java.util.Optional;
import java.util.UUID;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.RedstoneProgrammerBlock;
import com.petrolpark.destroy.util.RedstoneProgram;
import com.petrolpark.destroy.util.RedstoneProgrammerItemHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class RedstoneProgrammerBlockItem extends BlockItem {

    public RedstoneProgrammerBlockItem(RedstoneProgrammerBlock block, Properties properties) {
        super(block, properties);
        properties.stacksTo(1);
    };

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (entity instanceof LivingEntity player) {
            getProgram(stack, level, player).ifPresent(program -> {
                program.load(); // This is a set so we're safe to repeatedly load
                program.tick();
                setProgram(stack, program);
            });
        };
    };

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack from, ItemStack to, boolean slotChanged) {
        return !(from.getItem() instanceof RedstoneProgrammerBlockItem && to.getItem() instanceof RedstoneProgrammerBlockItem);
    };

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack from, ItemStack to) {
        return !(from.getItem() instanceof RedstoneProgrammerBlockItem && to.getItem() instanceof RedstoneProgrammerBlockItem);
    };

    public static void setProgram(ItemStack stack, RedstoneProgram program) {
        stack.getOrCreateTag().put("Program", program.write());
    };

    public static ItemStack withProgram(RedstoneProgram program) {
        ItemStack stack = DestroyBlocks.REDSTONE_PROGRAMMER.asStack();
        setProgram(stack, program);
        return stack;
    };

    /**
     * Get the Program associated with this Redstone Programmer.
     * @param item The tag of this may be changed
     * @param level
     * @param player
     * @return An Optional which should almost always contain a Redstone Program
     */
    public static Optional<RedstoneProgram> getProgram(ItemStack item, LevelAccessor level, LivingEntity player) {
        if (!(item.getItem() instanceof RedstoneProgrammerBlockItem) || player == null) return Optional.empty();
        CompoundTag tag = item.getOrCreateTag();
        UUID uuid = null;
        if (tag.contains("UUID")) {
            uuid = tag.getUUID("UUID");
        } else {
            uuid = UUID.randomUUID();
            tag.putUUID("UUID", uuid);
        };
        ItemStackRedstoneProgram program = RedstoneProgrammerItemHandler.programs.get(level).computeIfAbsent(uuid, u -> {
            if (!tag.contains("Program")) return new ItemStackRedstoneProgram(player);
            return RedstoneProgram.read(() -> new ItemStackRedstoneProgram(player), item.getOrCreateTag().getCompound("Program"));
        });
        return Optional.of(program);
    };

    public static class ItemStackRedstoneProgram extends RedstoneProgram {

        public int ttl;
        protected final LivingEntity player;

        public ItemStackRedstoneProgram(LivingEntity player) {
            super();
            this.player = player;
            ttl = RedstoneProgrammerItemHandler.TIMEOUT;
        };

        @Override
        public void tick() {
            ttl = RedstoneProgrammerItemHandler.TIMEOUT; // This tick is only called for programmers in a Player's inventory, so if the Item is no longer in an inventory, it will die
            super.tick();
        };

        @Override
        public boolean hasPower() {
            return false;
        };

        @Override
        public BlockPos getBlockPos() {
            return player.getOnPos();
        };

        @Override
        public boolean shouldTransmit() {
            return ttl > 0;
        };

        @Override
        public LevelAccessor getWorld() {
            return player.level();
        };

    };
    
};
