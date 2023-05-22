package com.petrolpark.destroy.block;

import javax.annotation.Nullable;

import com.petrolpark.destroy.behaviour.DestroyAdvancementBehaviour;
import com.petrolpark.destroy.block.entity.DestroyBlockEntities;
import com.petrolpark.destroy.block.entity.DynamoBlockEntity;
import com.petrolpark.destroy.block.shape.DestroyShapes;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DynamoBlock extends KineticBlock implements IBE<DynamoBlockEntity>, ICogWheel {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public DynamoBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    };

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    };

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (direction != Direction.UP) return 0;
        return getBlockEntityOptional(level, pos).map(DynamoBlockEntity::getRedstoneSignal).orElse(0);
    };

    @Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == Direction.UP;
	};

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        DestroyAdvancementBehaviour.setPlacedBy(level, pos, placer);
        super.setPlacedBy(level, pos, state, placer, stack);
    };


    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return DestroyShapes.DYNAMO;
    };

    @Override
    public Class<DynamoBlockEntity> getBlockEntityClass() {
        return DynamoBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends DynamoBlockEntity> getBlockEntityType() {
        return DestroyBlockEntities.DYNAMO.get();
    };
    
}
