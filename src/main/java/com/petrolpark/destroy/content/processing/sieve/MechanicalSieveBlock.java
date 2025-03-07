package com.petrolpark.destroy.content.processing.sieve;

import com.petrolpark.compat.create.block.entity.behaviour.AbstractRememberPlacerBehaviour;
import com.petrolpark.destroy.DestroyBlockEntityTypes;
import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.DestroyVoxelShapes;
import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MechanicalSieveBlock extends KineticBlock implements IBE<MechanicalSieveBlockEntity>, TransformableBlock {

    public static final BooleanProperty X = BooleanProperty.create("x");

    public MechanicalSieveBlock(Properties properties) {
        super(properties);
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(X);
    };

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(X, context.getClickedFace().getAxis() == Axis.X);
    };

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        AbstractRememberPlacerBehaviour.setPlacedBy(worldIn, pos, placer);
    };

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return DestroyVoxelShapes.MECHANICAL_SIEVE;
    };

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() instanceof ItemEntity) return DestroyVoxelShapes.MECHANICAL_SIEVE_COLLISION;
        return getShape(pState, pLevel, pPos, context);
    };

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        super.updateEntityAfterFallOn(level, entity);
        BlockPos pos = entity.blockPosition();
		if (!DestroyBlocks.MECHANICAL_SIEVE.has(level.getBlockState(pos)) || !(entity instanceof ItemEntity item) || !entity.isAlive()) return;
        withBlockEntityDo(level, pos, be -> be.beginProcessing(item));
    };

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        return originalState.cycle(X);
    };

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(X) ? Axis.X : Axis.Z;
    };

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        if (face.getAxis() == Axis.Y) return false;
        return (face.getAxis() == Axis.X) == state.getValue(X);
    };

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.FAST;
    };

    @Override
    public Class<MechanicalSieveBlockEntity> getBlockEntityClass() {
        return MechanicalSieveBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends MechanicalSieveBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.MECHANICAL_SIEVE.get();
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        if (transform.rotationAxis == Axis.Y && (transform.rotation == Rotation.CLOCKWISE_90 || transform.rotation == Rotation.COUNTERCLOCKWISE_90)) return state.cycle(X);
        return state;
    };
    
};
