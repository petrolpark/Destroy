package com.petrolpark.destroy.block;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

public class BreezeBurnerBlock extends BlazeBurnerBlock { //really this should implement ITE<BreezeBurnerBlockEntity>

    public static final EnumProperty<HeatLevel> COLD_LEVEL = EnumProperty.create("breeze", HeatLevel.class);

    public BreezeBurnerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(COLD_LEVEL, HeatLevel.valueOf("FROSTING")));
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(COLD_LEVEL);
    };

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
        if (level.isClientSide()) return;
        BlockEntity blockEntity = level.getBlockEntity(pos.above()); //check for a basin
        if (!(blockEntity instanceof BasinTileEntity)) return;
        BasinTileEntity basin = (BasinTileEntity) blockEntity;
        basin.notifyChangeOfContents(); //let the Basin know there's now a Breeze burner
        super.onPlace(state, level, pos, p_220082_4_, p_220082_5_);
    };

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this)); //this is copied from Block source code (as calling the super method would add a Blaze Burner)
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
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return AllShapes.HEATER_BLOCK_SHAPE;
    };

    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockGetter p_220071_2_, BlockPos p_220071_3_, CollisionContext p_220071_4_) {
        return getShape(p_220071_1_, p_220071_2_, p_220071_3_, p_220071_4_);
    };

    @Override
    public int getAnalogOutputSignal(BlockState state, Level p_180641_2_, BlockPos p_180641_3_) {
        return 1;
    };

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        return;
    };

    public static HeatLevel getHeatLevelOf(BlockState blockState) {
        return HeatLevel.valueOf("FROSTING");
    };
    
}
