package com.petrolpark.destroy.block;

import com.petrolpark.destroy.block.entity.AgingBarrelBlockEntity;
import com.petrolpark.destroy.block.entity.DestroyBlockEntities;
import com.petrolpark.destroy.block.shape.DestroyShapes;
import com.simibubi.create.content.contraptions.fluids.actors.GenericItemFilling;
import com.simibubi.create.content.contraptions.processing.EmptyingByBasin;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.fluid.FluidHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AgingBarrelBlock extends Block implements ITE<AgingBarrelBlockEntity>{

    public static final DirectionProperty DIRECTION = DirectionProperty.create("direction");
    public static final BooleanProperty IS_OPEN = BooleanProperty.create("open");
    public static final IntegerProperty PROGRESS = IntegerProperty.create("progress", 0, 6); //0 = empty, 1 = water, 2 = yeast, 3 = smallest balloon, ... 7 = done (biggest balloon)

    public AgingBarrelBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState()
            .setValue(DIRECTION, Direction.NORTH)
            .setValue(IS_OPEN, true)
            .setValue(PROGRESS, 0)
        );
    };

    @Override
    public InteractionResult use(BlockState blockstate, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        return onTileEntityUse(level, pos, te -> {
            if (!heldItem.isEmpty()) {
				if (FluidHelper.tryEmptyItemIntoTE(level, player, hand, heldItem, te)) {
					return InteractionResult.SUCCESS;
                };
				if (FluidHelper.tryFillItemFromTE(level, player, hand, heldItem, te)) {
					return InteractionResult.SUCCESS;
                };
                if (EmptyingByBasin.canItemBeEmptied(level, heldItem) || GenericItemFilling.canItemBeFilled(level, heldItem)) {
					return InteractionResult.SUCCESS;
                };
			};
            return InteractionResult.PASS;
        });
    };

    @Override
    public VoxelShape getShape(BlockState blockstate, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (blockstate.getValue(IS_OPEN)) {
            return DestroyShapes.AGING_BARREL_OPEN.get(blockstate.getValue(DIRECTION));
        } else {
            return DestroyShapes.agingBarrelClosed(blockstate.getValue(PROGRESS));
        }
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(DIRECTION, IS_OPEN, PROGRESS);
        super.createBlockStateDefinition(builder);
    };

    @Override
    public Class<AgingBarrelBlockEntity> getTileEntityClass() {
        return AgingBarrelBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends AgingBarrelBlockEntity> getTileEntityType() {
        return DestroyBlockEntities.AGING_BARREL.get();
    };

}
