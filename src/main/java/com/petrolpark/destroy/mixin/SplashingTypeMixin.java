package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.destroy.util.ChemistryDamageHelper;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes.SplashingType;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(SplashingType.class)
public class SplashingTypeMixin {
    
    /**
     * Injection into {@link com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes.SplashingType#affectEntity Create}
     * to allow fans to wash contaminated hazmat suits.
     * @param entity
     * @param level
     * @param ci
     */
    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/fan/processing/AllFanProcessingTypes$SplashingType;affectEntity(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;)V",
        at = @At("RETURN"),
        remap = false
    )
    public void inAffectEntity(Entity entity, Level level, CallbackInfo ci) {
        if (entity instanceof LivingEntity livingEntity) {
            for (ItemStack armor : livingEntity.getArmorSlots()) ChemistryDamageHelper.decontaminate(armor);
        };
    };
};
