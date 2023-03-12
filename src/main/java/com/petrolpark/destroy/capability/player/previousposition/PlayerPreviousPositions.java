package com.petrolpark.destroy.capability.player.previousposition;

import java.util.List;
import java.util.Queue;

import com.google.common.collect.EvictingQueue;
import com.petrolpark.destroy.config.DestroyAllConfigs;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class PlayerPreviousPositions {

    private static int QUEUE_SIZE = 20; // Default is 20
    private static final int TICKS_PER_SECOND = 20;

    private Queue<BlockPos> previousPositions = EvictingQueue.create(QUEUE_SIZE);
    private int tickCounter = 0;

    public BlockPos getOldestPosition() {
        return previousPositions.peek();
    };

    public void recordPosition(BlockPos pos) {
        previousPositions.add(pos);
    };

    public void incrementTickCounter() {
        tickCounter++;
        if (tickCounter >= TICKS_PER_SECOND) {
            tickCounter = 0;
        };
    };

    public boolean hasBeenSecond() {
        return tickCounter == 0;
    };

    public void saveNBTData(CompoundTag tag) {
        ListTag positionsTag = new ListTag();
        for (BlockPos pos : previousPositions) {
            positionsTag.add(new IntArrayTag(List.of(pos.getX(), pos.getY(), pos.getZ())));
        };
        tag.put("PreviousPositions", positionsTag);
    };

    public void loadNBTData(CompoundTag tag) {
        previousPositions = EvictingQueue.create(QUEUE_SIZE);
        ListTag positionsTag = tag.getList("PreviousPositions", Tag.TAG_INT_ARRAY);
        for (int i = 0; i < positionsTag.size(); i++) {
            int[] posTag = positionsTag.getIntArray(i);
            previousPositions.add(new BlockPos(posTag[0], posTag[1], posTag[2]));
        };
    };

    public static void updateQueueSize() {
        QUEUE_SIZE = DestroyAllConfigs.COMMON.substances.teleportTime.get();
    };

    public static int getQueueSize() {
        return QUEUE_SIZE;
    };
    
}
