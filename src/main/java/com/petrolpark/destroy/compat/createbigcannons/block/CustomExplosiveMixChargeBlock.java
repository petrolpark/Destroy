package com.petrolpark.destroy.compat.createbigcannons.block;

import java.util.List;

import javax.annotation.Nullable;

import java.util.Collections;

import com.petrolpark.destroy.compat.createbigcannons.DestroyMunitionPropertiesHandlers;
import com.petrolpark.destroy.compat.createbigcannons.block.entity.CreateBigCannonBlockEntityTypes;
import com.petrolpark.destroy.compat.createbigcannons.item.CustomExplosiveMixChargeBlockItem;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.core.explosion.mixedexplosive.ExplosiveProperties;
import com.petrolpark.destroy.core.explosion.mixedexplosive.MixedExplosiveInventory;
import com.petrolpark.destroy.core.explosion.mixedexplosive.SimpleDyeableNameableMixedExplosiveBlockEntity;
import com.petrolpark.destroy.core.explosion.mixedexplosive.ExplosiveProperties.ExplosiveProperty;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;
import rbasamoyai.createbigcannons.munitions.big_cannon.propellant.PowderChargeBlock;
import rbasamoyai.createbigcannons.munitions.big_cannon.propellant.config.BigCannonPropellantPropertiesComponent;

public class CustomExplosiveMixChargeBlock extends PowderChargeBlock implements IBE<SimpleDyeableNameableMixedExplosiveBlockEntity> {

    public CustomExplosiveMixChargeBlock(Properties properties) {
        super(properties);
    };

    @Nullable
	@Override
	public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level pLevel, BlockState pState, BlockEntityType<S> pBlockEntityType) {
		return null; // This type of block does not need to tick
	};

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity pPlacer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, pPlacer, stack);
        withBlockEntityDo(level, pos, be -> be.onPlace(stack));
    };

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult pHit) {
        InteractionResult dyeingResult = onBlockEntityUse(level, pos, be -> be.tryDye(player.getItemInHand(hand), pHit, level, pos, player));
        if (dyeingResult != InteractionResult.PASS) return dyeingResult;
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            withBlockEntityDo(level, pos, be -> NetworkHooks.openScreen(serverPlayer, be, be::writeToBuffer));
            return InteractionResult.SUCCESS;
        };
        return InteractionResult.PASS;
    };

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (!(be instanceof SimpleDyeableNameableMixedExplosiveBlockEntity ebe)) return Collections.emptyList();
        return Collections.singletonList(ebe.getFilledItemStack(CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_CHARGE.asStack()));
    };

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return getCloneItemStack(level, pos);
    };

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return getCloneItemStack(level, pos);
    };

    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos) {
        SimpleDyeableNameableMixedExplosiveBlockEntity be = getBlockEntity(level, pos);
        if (be == null) return ItemStack.EMPTY;
        return be.getFilledItemStack(CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_CHARGE.asStack());
    };

    @Override
    public ItemStack getExtractedItem(StructureBlockInfo info) {
        return getItem().fromStructureInfo(info);
    };

    @Override
    public StructureBlockInfo getHandloadingInfo(ItemStack stack, BlockPos localPos, Direction cannonOrientation) {
        BlockState state = defaultBlockState().setValue(RotatedPillarBlock.AXIS, cannonOrientation.getAxis());
        return getItem().toStructureInfo(localPos, state, stack);
    };

    @Override
    public float getChargePower(StructureBlockInfo data) {
		return getPropellantProperties(data).strength();
	};

	@Override
    public float getChargePower(ItemStack stack) {
		return getPropellantProperties(stack).strength();
	};

	@Override
    public float getStressOnCannon(StructureBlockInfo data) {
		return getPropellantProperties(data).addedStress();
	};

	@Override
    public float getStressOnCannon(ItemStack stack) {
		return getPropellantProperties(stack).addedStress();
	};

	@Override
    public float getSpread(StructureBlockInfo data) {
		return getPropellantProperties(data).addedSpread();
	};

	@Override
    public float getRecoil(StructureBlockInfo data) {
		return getPropellantProperties(data).addedRecoil();
	};

    @Override
    public Class<SimpleDyeableNameableMixedExplosiveBlockEntity> getBlockEntityClass() {
        return SimpleDyeableNameableMixedExplosiveBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends SimpleDyeableNameableMixedExplosiveBlockEntity> getBlockEntityType() {
        return CreateBigCannonBlockEntityTypes.CUSTOM_EXPLOSIVE_MIX_CHARGE.get();
    };

    public CustomExplosiveMixChargeBlockItem getItem() {
        return ((CustomExplosiveMixChargeBlockItem)CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_CHARGE.asItem());
    };

    public BigCannonPropellantPropertiesComponent getPropellantProperties(ItemStack stack) {
        return getPropellantProperties(stack.getOrCreateTag());
    };

    public BigCannonPropellantPropertiesComponent getPropellantProperties(StructureBlockInfo data) {
        return getPropellantProperties(data.nbt());
    };

    public BigCannonPropellantPropertiesComponent getPropellantProperties(CompoundTag nbt) {
        MixedExplosiveInventory inv = new MixedExplosiveInventory(DestroyAllConfigs.SERVER.compat.customExplosiveMixChargeSize.get());
        inv.deserializeNBT(nbt.getCompound("ExplosiveMix"));
        if (inv.isEmpty()) return BigCannonPropellantPropertiesComponent.DEFAULT;
        CustomExplosiveMixChargeProperties chargeProperties = DestroyMunitionPropertiesHandlers.CUSTOM_EXPLOSIVE_MIX_CHARGE.getPropertiesOf(this);
        ExplosiveProperties explosiveProperties = inv.getExplosiveProperties();
        if (!explosiveProperties.fulfils(ExplosiveProperties.CAN_EXPLODE)) return BigCannonPropellantPropertiesComponent.DEFAULT;
        float strength = chargeProperties.basePropellantProperties().strength();
        float stress = chargeProperties.basePropellantProperties().addedStress();
        float recoil = chargeProperties.basePropellantProperties().addedRecoil();
        float spread = chargeProperties.basePropellantProperties().addedStress();
        for (ExplosiveProperty property : ExplosiveProperty.values()) {
            strength += explosiveProperties.get(property).value * chargeProperties.propellantPropertyModifiers().get(property).strength();
            stress += explosiveProperties.get(property).value * chargeProperties.propellantPropertyModifiers().get(property).addedStress();
            recoil += explosiveProperties.get(property).value * chargeProperties.propellantPropertyModifiers().get(property).addedRecoil();
            spread += explosiveProperties.get(property).value * chargeProperties.propellantPropertyModifiers().get(property).addedSpread();
        };
        return new BigCannonPropellantPropertiesComponent(strength, stress, recoil, spread, BigCannonPropellantPropertiesComponent.DEFAULT.explosionPower(), BigCannonPropellantPropertiesComponent.DEFAULT.strength(), BigCannonPropellantPropertiesComponent.DEFAULT.dampAmmoDoesntIgniteAsStarter());
    };
    
};
