package com.petrolpark.destroy.block;

import com.petrolpark.destroy.block.entity.AgingBarrelBlockEntity;
import com.petrolpark.destroy.block.entity.DestroyBlockEntities;
import com.petrolpark.destroy.block.shape.DestroyShapes;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.fluids.actors.GenericItemFilling;
import com.simibubi.create.content.contraptions.processing.EmptyingByBasin;
import com.simibubi.create.foundation.block.ITE;
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
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class AgingBarrelBlock extends Block implements ITE<AgingBarrelBlockEntity> {

    public static final DirectionProperty DIRECTION = DirectionProperty.create("direction");
    public static final BooleanProperty IS_OPEN = BooleanProperty.create("open");
    public static final IntegerProperty PROGRESS = IntegerProperty.create("progress", 0, 4); //0 = smallest balloon, ... 4 = done (biggest balloon)

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
        return onTileEntityUse(level, pos, be -> {
            if (!heldItem.isEmpty()) {
				if (FluidHelper.tryEmptyItemIntoTE(level, player, hand, heldItem, be)) { //try emptying the Fluid in the Item into the Barrel
					return InteractionResult.SUCCESS;
                };
				if (FluidHelper.tryFillItemFromTE(level, player, hand, heldItem, be)) { //try filling up the Item with the Fluid in the Barrel
					return InteractionResult.SUCCESS;
                };
                if (EmptyingByBasin.canItemBeEmptied(level, heldItem) || GenericItemFilling.canItemBeFilled(level, heldItem)) { //try filling up the Item with the Fluid in the Barrel (again)
					return InteractionResult.SUCCESS;
                };
			} else { //try picking up the item in the Barrel
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
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + Create.RANDOM.nextFloat());
            };
            return InteractionResult.PASS;
        });
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
		withTileEntityDo(worldIn, entityIn.blockPosition(), be -> {

			ItemStack insertItem = ItemHandlerHelper.insertItem(be.inventory, itemEntity.getItem().copy(), false);

			if (insertItem.isEmpty()) {
				itemEntity.discard();
				return;
			};

			itemEntity.setItem(insertItem);
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
    public VoxelShape getCollisionShape(BlockState blockstate, BlockGetter level, BlockPos pos, CollisionContext context) {
        //if the Entity is an Item and the Barrel is empty the collision box is higher so the Items actually register when they fall in
        if (context instanceof EntityCollisionContext && ((EntityCollisionContext) context).getEntity() instanceof ItemEntity) {
            return DestroyShapes.AGING_BARREL_INTERIOR;
        };
        return getShape(blockstate, level, pos, context);
    };

    @Override
    public VoxelShape getInteractionShape(BlockState blockstate, BlockGetter level, BlockPos pos) {
        return DestroyShapes.AGING_BARREL_OPEN_RAYTRACE.get(blockstate.getValue(DIRECTION));
    };

    @Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.hasBlockEntity() || state.getBlock() == newState.getBlock()) return;
		withTileEntityDo(level, pos, be -> {
			ItemHelper.dropContents(level, pos, be.inventory);
		});
		level.removeBlockEntity(pos);
	};

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockstate = this.defaultBlockState().setValue(DIRECTION, pContext.getHorizontalDirection().getOpposite());
        return blockstate;
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
