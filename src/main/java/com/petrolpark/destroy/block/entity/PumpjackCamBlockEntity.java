package com.petrolpark.destroy.block.entity;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PumpjackCamBlockEntity extends KineticBlockEntity {

    public BlockPos pumpjackPos;
    private int initialTicks;

    public PumpjackCamBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        initialTicks = 3;
    };

    @Override
    public void tick() {
        super.tick();
        if (initialTicks > 0) {
            initialTicks--;
        };
    };

    @Override
    protected Block getStressConfigKey() {
        return DestroyBlocks.PUMPJACK.get();
    };

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        initialTicks = compound.getInt("Warmup");
    };

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("Warmup", initialTicks);
    };

    public void update(BlockPos sourcePos) {
        pumpjackPos = worldPosition.subtract(sourcePos);  
    };

    public void remove(BlockPos sourcePos) {
        if (!isPowering(sourcePos)) return;
        pumpjackPos = null;
    };
    
    public boolean canPower(BlockPos globalPos) {
        return initialTicks == 0 && (pumpjackPos == null || isPowering(globalPos));
    };

    public boolean isPowering(BlockPos globalPos) {
        BlockPos key = worldPosition.subtract(globalPos);
        return key.equals(pumpjackPos);
    };
};
