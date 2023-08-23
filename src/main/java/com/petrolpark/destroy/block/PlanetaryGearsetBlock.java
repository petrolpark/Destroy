package com.petrolpark.destroy.block;

import com.petrolpark.destroy.block.entity.DestroyBlockEntityTypes;
import com.petrolpark.destroy.block.shape.DestroyShapes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PlanetaryGearsetBlock extends AbstractShaftBlock implements ICogWheel {

    public PlanetaryGearsetBlock(Properties properties) {
        super(properties);
    };

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return DestroyShapes.PLANETARY_GEARSET.get(state.getValue(RotatedPillarKineticBlock.AXIS));
    };

    @Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.NORMAL;
	};

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(RotatedPillarKineticBlock.AXIS);
    };

    @Override
    public boolean isLargeCog() {
        return true;
    };

    @Override
    public boolean isDedicatedCogWheel() {
        return true;
    };

    @Override
	public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.PLANETARY_GEARSET.get();
    };
    
};
