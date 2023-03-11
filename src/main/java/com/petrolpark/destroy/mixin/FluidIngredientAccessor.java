package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.google.gson.JsonObject;
import com.simibubi.create.foundation.fluid.FluidIngredient;

@Mixin(FluidIngredient.class)
public interface FluidIngredientAccessor {
    
    @Accessor("amountRequired")
	void setAmountRequired(int amountRequired);

	@Invoker("readInternal")
	void invokeReadInternal(JsonObject json);
};
