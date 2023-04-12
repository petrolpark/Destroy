package com.petrolpark.destroy.block;


import com.petrolpark.destroy.block.entity.CoolerBlockEntity;
import com.petrolpark.destroy.block.entity.DestroyBlockEntities;
import com.petrolpark.destroy.block.entity.CoolerBlockEntity.ColdnessLevel;
import com.petrolpark.destroy.block.shape.DestroyShapes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CoolerBlock extends Block implements ITE<CoolerBlockEntity> {

    public static final EnumProperty<ColdnessLevel> COLD_LEVEL = EnumProperty.create("breeze", ColdnessLevel.class);
    public static final EnumProperty<HeatLevel> HEAT_LEVEL = EnumProperty.create("blaze", HeatLevel.class); // This is purely for ease of interaction with Basin Recipes - most values do nothing

    public CoolerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(COLD_LEVEL, ColdnessLevel.IDLE).setValue(HEAT_LEVEL, HeatLevel.NONE));
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(COLD_LEVEL, HEAT_LEVEL);
    };

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
        withTileEntityDo(level, pos, be -> be.updateHeatLevel(state.getValue(COLD_LEVEL)));
        
        if (level.isClientSide()) return;
        BlockEntity blockEntity = level.getBlockEntity(pos.above()); // Check for a Basin
        if (!(blockEntity instanceof BasinTileEntity)) return;
        BasinTileEntity basin = (BasinTileEntity) blockEntity;
        basin.notifyChangeOfContents(); // Let the Basin know there's now a Cooler
    };

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockRayTraceResult) {
        return InteractionResult.PASS;
    };

    public static InteractionResultHolder<ItemStack> tryInsert(BlockState state, Level world, BlockPos pos, ItemStack stack, boolean doNotConsume, boolean forceOverflow, boolean simulate) { //this is an override
        return InteractionResultHolder.fail(ItemStack.EMPTY);
    };

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState();
    };

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos pos, CollisionContext context) {
        return DestroyShapes.COOLER;
    };

    @Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (context == CollisionContext.empty()) return AllShapes.HEATER_BLOCK_SPECIAL_COLLISION_SHAPE;
		return getShape(blockState, level, pos, context);
	};

    public static ColdnessLevel getColdnessLevelOf(BlockState blockState) {
        return blockState.getValue(COLD_LEVEL);
    };

    @Override
    public Class<CoolerBlockEntity> getTileEntityClass() {
        return CoolerBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends CoolerBlockEntity> getTileEntityType() {
        return DestroyBlockEntities.COOLER.get();
    };

    @Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	};
    
};
