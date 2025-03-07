package com.petrolpark.destroy.compat.createbigcannons.entity;

/*import com.petrolpark.destroy.compat.createbigcannons.block.CreateBigCannonsBlocks;
import com.petrolpark.destroy.compat.createbigcannons.block.CustomExplosiveMixShellBlock; // TODO: CBC
import com.petrolpark.destroy.core.explosion.SmartExplosion;
import com.petrolpark.destroy.core.explosion.mixedexplosive.CustomExplosiveMixExplosion;
import com.petrolpark.destroy.core.explosion.mixedexplosive.ExplosiveProperties;
import com.petrolpark.destroy.core.explosion.mixedexplosive.MixedExplosiveInventory;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.index.CBCMunitionPropertiesHandlers;
import rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBigCannonProjectile;
import rbasamoyai.createbigcannons.munitions.big_cannon.config.BigCannonCommonShellProperties;
import rbasamoyai.createbigcannons.munitions.big_cannon.config.BigCannonFuzePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.big_cannon.config.BigCannonProjectilePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;

public class CustomExplosiveMixShellProjectile extends FuzedBigCannonProjectile {

    protected MixedExplosiveInventory inv;
    public int color;

    protected CustomExplosiveMixShellProjectile(EntityType<CustomExplosiveMixShellProjectile> type, Level level) {
        super(type, level);
    };

    @Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.put("Inventory", inv.serializeNBT());
	};

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		inv = new MixedExplosiveInventory(16);
        inv.deserializeNBT(tag);
	};

    public void setExplosiveInventory(MixedExplosiveInventory inv) {
        this.inv = inv;
    };

    @Override
    public BlockState getRenderedBlockState() {
        return CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_SHELL.getDefaultState().setValue(CustomExplosiveMixShellBlock.FACING, Direction.NORTH);
    };

    @Override
    protected BigCannonFuzePropertiesComponent getFuzeProperties() {
        return getShellProperties().fuze();
    };

    @Override
    protected void detonate(Position position) {
        if (level() instanceof ServerLevel serverLevel && !inv.isEmpty() && inv.getExplosiveProperties().fulfils(ExplosiveProperties.CAN_EXPLODE)) SmartExplosion.explode(serverLevel, CustomExplosiveMixExplosion.create(serverLevel, inv, this, new Vec3(position.x(), position.y(), position.z())));
    };

    @Override
    protected BigCannonProjectilePropertiesComponent getBigCannonProjectileProperties() {
        return getShellProperties().bigCannonProperties();
    };

    @Override
    public EntityDamagePropertiesComponent getDamageProperties() {
        return getShellProperties().damage();
    };

    @Override
    protected BallisticPropertiesComponent getBallisticProperties() {
        return getShellProperties().ballistics();
    };

    public BigCannonCommonShellProperties getShellProperties() {
        return CBCMunitionPropertiesHandlers.COMMON_SHELL_BIG_CANNON_PROJECTILE.getPropertiesOf(this);
    };
    
};*/ // TODO: CBC
