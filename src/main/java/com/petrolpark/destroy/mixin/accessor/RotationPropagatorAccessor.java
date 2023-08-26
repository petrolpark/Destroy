package com.petrolpark.destroy.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.Direction;

@Mixin(RotationPropagator.class)
public interface RotationPropagatorAccessor {
    
    @Invoker("getAxisModifier")
    public static float invokeGetAxisModifier(KineticBlockEntity be, Direction direction) {
        return 0f; // This return value is ignored.
    };
};
