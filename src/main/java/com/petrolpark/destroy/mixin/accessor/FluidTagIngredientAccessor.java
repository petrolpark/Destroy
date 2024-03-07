package com.petrolpark.destroy.mixin.accessor;

import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = FluidIngredient.FluidTagIngredient.class, remap = false)
public interface FluidTagIngredientAccessor {
    @Accessor(
        remap = false,
        value = "tag"
    )
    TagKey<Fluid> destroy$getTag();
}
