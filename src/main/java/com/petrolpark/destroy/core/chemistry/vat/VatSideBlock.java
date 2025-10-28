package com.petrolpark.destroy.core.chemistry.vat;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.petrolpark.destroy.DestroyBlockEntityTypes;
import com.petrolpark.destroy.core.chemistry.storage.IMixtureStorageItem;
import com.petrolpark.destroy.core.chemistry.storage.ISpecialMixtureContainerBlock;
import com.petrolpark.destroy.core.chemistry.vat.VatSideBlockEntity.DisplayType;
import com.petrolpark.destroy.core.chemistry.vat.observation.RedstoneMonitorVatSideScreen;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntityTicker;

import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.DistExecutor;

public class VatSideBlock extends CopycatBlock implements SpecialBlockItemRequirement, ISpecialMixtureContainerBlock {

    public VatSideBlock(Properties properties) {
        super(properties);
    };

    @Override
    @Nullable
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level level, BlockState state, BlockEntityType<S> blockEntityType) {
        return new SmartBlockEntityTicker<>();
    };

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (AllItems.WRENCH.isIn(player.getItemInHand(hand)) || IMixtureStorageItem.isHolding(player, hand)) return InteractionResult.PASS;
        return onBlockEntityUse(level, pos, be -> {
            if (!(be instanceof VatSideBlockEntity vbe)) return InteractionResult.PASS;
            if (vbe.getDisplayType().quantityObserved.isPresent()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> openScreen(vbe, player));
                return InteractionResult.SUCCESS;
            };
            return InteractionResult.PASS;
        });
    };

    @OnlyIn(Dist.CLIENT)
    public void openScreen(VatSideBlockEntity vbe, Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new RedstoneMonitorVatSideScreen(vbe));
    };

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return onBlockEntityUse(context.getLevel(), context.getClickedPos(), be -> {
            if (!(be instanceof VatSideBlockEntity vatSide)) return InteractionResult.PASS;
            boolean blocked = !be.getLevel().getBlockState(be.getBlockPos().relative(context.getClickedFace())).isAir();
            switch (vatSide.getDisplayType()) {
                case PIPE: {
                    return InteractionResult.PASS;
                } case NORMAL: {
                    vatSide.setDisplayType(vatSide.direction == Direction.UP ? DisplayType.OPEN_VENT : (blocked ? DisplayType.THERMOMETER_BLOCKED : DisplayType.THERMOMETER));
                    return InteractionResult.SUCCESS;
                } case THERMOMETER: {
                    vatSide.setDisplayType(DisplayType.BAROMETER);
                    return InteractionResult.SUCCESS;
                } case THERMOMETER_BLOCKED: {
                    vatSide.setDisplayType(DisplayType.BAROMETER_BLOCKED);
                    return InteractionResult.SUCCESS;
                } case BAROMETER: case BAROMETER_BLOCKED: case OPEN_VENT: case CLOSED_VENT: {
                    vatSide.setDisplayType(DisplayType.NORMAL);
                    return InteractionResult.SUCCESS;
                } default:
                    return InteractionResult.PASS;  
            }
        });
    };

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getBlockEntityOptional(level, pos).map(be -> be instanceof VatSideBlockEntity vatSide && vatSide.direction == direction ? vatSide.redstoneMonitor.getStrength() : 0).orElse(0);
    };

    @Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		return getBlockEntityOptional(level, pos).map(be -> be instanceof VatSideBlockEntity vatSide ? vatSide.redstoneMonitor.getStrength() : 0).orElse(0);
	};

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
		return InteractionResult.PASS;
	};

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        withBlockEntityDo(level, currentPos, be -> {
            if (!(be instanceof VatSideBlockEntity vatSide)) return;
            vatSide.updateRedstoneInput();
            if (facing != vatSide.direction) return;
            vatSide.updateDisplayType(facingPos);
            vatSide.setPowerFromAdjacentBlock(facingPos);   
        });
        return state;
    };

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        withBlockEntityDo(level, pos, be -> {
            if (!(be instanceof VatSideBlockEntity vatSide) || vatSide.direction == null) return;
            if (!pos.relative(vatSide.direction).equals(neighbor)) return;
            vatSide.updateDisplayType(neighbor);
            vatSide.setPowerFromAdjacentBlock(neighbor);   
        });
        super.onNeighborChange(state, level, pos, neighbor);
    };

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        withBlockEntityDo(level, pos, be -> {
            if (!(be instanceof VatSideBlockEntity vatSide)) return;
            vatSide.updateRedstoneInput();
        });
    };

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
        level.removeBlockEntity(pos);
    };

    @Override
    public List<ItemStack> getDrops(BlockState state, Builder paramsBuilder) {
        BlockEntity be = paramsBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if ((be instanceof VatSideBlockEntity vatSide)) {
            ResourceLocation lootResourceLocation = vatSide.getMaterial().getBlock().getLootTable();
            if (lootResourceLocation != BuiltInLootTables.EMPTY) {
                LootParams params = paramsBuilder.withParameter(LootContextParams.BLOCK_STATE, vatSide.getMaterial()).create(LootContextParamSets.BLOCK);
                LootTable lootTable = params.getLevel().getServer().getLootData().getLootTable(lootResourceLocation);
                return lootTable.getRandomItems(params); // Return loot table of wrapped Block so Glass does not drop without Silk Touch etc.
            };
        };
        return Collections.emptyList();
    };

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (placer == null) {
            withBlockEntityDo(level, pos, be -> {
                level.setBlockAndUpdate(pos, be.getMaterial());
            });
        };
    };

    @Override
    public boolean canFaceBeOccluded(BlockState state, Direction face) {
        return true;
    };

    @Override
    public boolean canConnectTexturesToward(BlockAndTintGetter reader, BlockPos fromPos, BlockPos toPos, BlockState state) {
        return true;
    };

    @Override
    public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        return getPickBlockItemStack(pLevel, pPos);
    };

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return getPickBlockItemStack(level, pos);
    };

    protected ItemStack getPickBlockItemStack(BlockGetter level, BlockPos pos) {
        if (getBlockEntity(level, pos) instanceof VatSideBlockEntity vatSide) return vatSide.getConsumedItem();
        return ItemStack.EMPTY;
    };

    @Override
    @SuppressWarnings("deprecation")
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        return getMaterial(level, pos).getBlock().skipRendering(state, neighborState, dir);
    };

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return getMaterial(level, pos).getShadeBrightness(level, pos);
    };
  
    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return getMaterial(reader, pos).propagatesSkylightDown(reader, pos);
    };

    @Override
    public VoxelShape getVisualShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.empty();
    };

    @Override
    public BlockEntityType<? extends CopycatBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.VAT_SIDE.get();
    };

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity blockEntity) {
        return ItemRequirement.NONE;
    }

    @Override
    public IFluidHandler getTankForMixtureStorageItems(IMixtureStorageItem item, Level level, BlockPos pos, BlockState state, Direction face, Player player, InteractionHand hand, ItemStack stack, boolean filling) {
        if (getBlockEntity(level, pos) instanceof VatSideBlockEntity vatSide) {
            VatControllerBlockEntity vatController = vatSide.getController();
            if (vatController != null) return item.selectVatTank(level, pos, state, face, player, hand, stack, filling, vatController);
        };
        return null;
    };
    
};
