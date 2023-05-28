package com.petrolpark.destroy.block.entity;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
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
