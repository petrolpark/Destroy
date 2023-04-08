package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.AgeableMob;

@Mixin(AgeableMob.class)
public interface AgeableMobAccessor {

    @Invoker("isBaby")
    public boolean invokeIsBaby();
};
