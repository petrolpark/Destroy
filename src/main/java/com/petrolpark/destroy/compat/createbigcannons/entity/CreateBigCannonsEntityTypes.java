package com.petrolpark.destroy.compat.createbigcannons.entity;

import static com.petrolpark.destroy.Destroy.REGISTRATE;

//import com.petrolpark.destroy.compat.createbigcannons.entity.renderer.CustomExplosiveMixShellProjectileRenderer; // TODO: CBC
import com.tterrag.registrate.util.entry.EntityEntry;

import net.minecraft.world.entity.MobCategory;
import rbasamoyai.createbigcannons.index.CBCMunitionPropertiesHandlers;
import rbasamoyai.createbigcannons.multiloader.EntityTypeConfigurator;
import rbasamoyai.createbigcannons.munitions.config.MunitionPropertiesHandler;

public class CreateBigCannonsEntityTypes {

    /*public static final EntityEntry<CustomExplosiveMixShellProjectile> CUSTOM_EXPLOSIVE_MIX_SHELL = REGISTRATE
        .entity("custom_explosive_mix_shell", CustomExplosiveMixShellProjectile::new, MobCategory.MISC)
        .properties(b -> EntityTypeConfigurator.of(b)
            .size(0.2f, 0.2f)
            .fireImmune()
            .updateInterval(1)
            .updateVelocity(false)
            .trackingRange(16)
        ).renderer(() -> CustomExplosiveMixShellProjectileRenderer::new)
        .onRegister(type -> MunitionPropertiesHandler.registerProjectileHandler(type, CBCMunitionPropertiesHandlers.COMMON_SHELL_BIG_CANNON_PROJECTILE))
        .register();*/ // TODO: CBC

    public static final void register() {};
    
};
