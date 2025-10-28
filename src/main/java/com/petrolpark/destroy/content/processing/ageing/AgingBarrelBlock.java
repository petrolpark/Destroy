package com.petrolpark.destroy.content.processing.ageing;

import com.petrolpark.destroy.DestroyAdvancementTrigger;
import com.petrolpark.destroy.DestroyBlockEntityTypes;
import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.DestroyVoxelShapes;
import com.petrolpark.destroy.DestroySoundEvents;
import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.item.ItemHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class AgingBarrelBlock extends HorizontalDirectionalBlock implements IBE<AgeingBarrelBlockEntity>, IWrenchable, TransformableBlock {

    public static final BooleanProperty IS_OPEN = BooleanProperty.create("open");
    public static final IntegerProperty PROGRESS = IntegerProperty.create("progress", 0, 4); //0 = smallest balloon, ... 4 = done (biggest balloon)

    public AgingBarrelBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(IS_OPEN, true)
            .setValue(PROGRESS, 0)
        );
    };

    @Override
    public InteractionResult use(BlockState blockstate, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        return onBlockEntityUse(level, pos, be -> {

            if (be.tryOpen()) {
                DestroySoundEvents.AGING_BARREL_OPEN.playOnServer(level, pos);
                DestroyAdvancementTrigger.OPEN_AGING_BARREL.award(level, player);
                return InteractionResult.SUCCESS;
            };

            if (!heldItem.isEmpty()) {
				if (FluidHelper.tryEmptyItemIntoBE(level, player, hand, heldItem, be)) { // Try emptying the Fluid in the Item into the Barrel
                    be.checkRecipe();
					return InteractionResult.SUCCESS;
                };
				if (FluidHelper.tryFillItemFromBE(level, player, hand, heldItem, be)) { // Try filling up the Item with the Fluid in the Barrel
					return InteractionResult.SUCCESS;
                };
                if (GenericItemEmptying.canItemBeEmptied(level, heldItem) || GenericItemFilling.canItemBeFilled(level, heldItem)) { // Try filling up the Item with the Fluid in the Barrel (again)
                    return InteractionResult.SUCCESS;
                };
			} else { // Try picking up an Item from the Barrel
                IItemHandlerModifiable inv = be.itemCapability.orElse(new ItemStackHandler(1));
                boolean success = false;
                for (int slot = 0; slot < inv.getSlots(); slot++) {
                    ItemStack stackInSlot = inv.getStackInSlot(slot);
                    if (stackInSlot.isEmpty())
                        continue;
                    player.getInventory()
                        .placeItemBackInInventory(stackInSlot);
                    inv.setStackInSlot(slot, ItemStack.EMPTY);
                    success = true;
                }
                if (success)
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + player.getRandom().nextFloat());
            };
            return InteractionResult.PASS;
        });
    };

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        BlockState newState = IWrenchable.super.getRotatedBlockState(state, Direction.UP); // Always rotate around Y axis
        if (newState != state) {
            IWrenchable.playRotateSound(context.getLevel(), context.getClickedPos());
            updateAfterWrenched(state, context);
            return InteractionResult.SUCCESS;
        };
        return InteractionResult.PASS;
    };

    @Override
	public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
		super.updateEntityAfterFallOn(worldIn, entityIn);
		if (!DestroyBlocks.AGING_BARREL.has(worldIn.getBlockState(entityIn.blockPosition())))
			return;
		if (!(entityIn instanceof ItemEntity))
			return;
		if (!entityIn.isAlive())
			return;
		ItemEntity itemEntity = (ItemEntity) entityIn;
		withBlockEntityDo(worldIn, entityIn.blockPosition(), be -> {

			ItemStack insertItem = ItemHandlerHelper.insertItem(be.inventory, itemEntity.getItem().copy(), false);

            be.checkRecipe();

			if (insertItem.isEmpty()) {
				itemEntity.discard();
				return;
			};

			itemEntity.setItem(insertItem);
		});
	};

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        BlockEntity be = (BlockEntity)level.getBlockEntity(pos);
        if (be != null && be instanceof AgeingBarrelBlockEntity agingBarrelBE) {
            return agingBarrelBE.getLuminosity();
        };
        return 0;
    };

    @Override
    public VoxelShape getShape(BlockState blockstate, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (blockstate.getValue(IS_OPEN)) {
            return DestroyVoxelShapes.AGING_BARREL_OPEN.get(blockstate.getValue(FACING));
        } else {
            return DestroyVoxelShapes.agingBarrelClosed(blockstate.getValue(PROGRESS));
        }
    };

    @Override
    public VoxelShape getCollisionShape(BlockState blockstate, BlockGetter level, BlockPos pos, CollisionContext context) {
        // If the Entity is an Item and the Barrel is empty the collision box is higher so the Items actually register when they fall in
        if (context instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext.getEntity() instanceof ItemEntity) {
            return DestroyVoxelShapes.AGING_BARREL_INTERIOR;
        };
        return getShape(blockstate, level, pos, context);
    };

    @Override
    public VoxelShape getInteractionShape(BlockState blockstate, BlockGetter level, BlockPos pos) {
        return DestroyVoxelShapes.AGING_BARREL_OPEN_RAYTRACE.get(blockstate.getValue(FACING));
    };

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    };

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        switch (state.getValue(PROGRESS)) {
            case 1: return 3;
            case 2: return 7;
            case 3: return 11;
            case 4: return 15;
            default: return 0;
        }
    };

    @Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
		if (!state.hasBlockEntity() || state.getBlock() == newState.getBlock()) return;
		withBlockEntityDo(level, pos, be -> {
			ItemHelper.dropContents(level, pos, be.inventory);
		});
	};

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockstate = this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
        return blockstate;
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING, IS_OPEN, PROGRESS);
        super.createBlockStateDefinition(builder);
    };

    @Override
    public Class<AgeingBarrelBlockEntity> getBlockEntityClass() {
        return AgeingBarrelBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends AgeingBarrelBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.AGING_BARREL.get();
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        return state.setValue(FACING, transform.rotation.rotate(state.getValue(FACING)));
    };

}
