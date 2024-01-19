package com.petrolpark.destroy.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.google.gson.JsonObject;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.network.FriendlyByteBuf;

@Mixin(FluidIngredient.class)
public interface FluidIngredientAccessor {
    
    @Accessor(
		value = "amountRequired",
		remap = false
	)
	void setAmountRequired(int amountRequired);

	@Accessor(
		value = "amountRequired",
		remap = false
	)
	int getAmountRequired();

	@Invoker(
		value = "readInternal",
		remap = false
	)
	void invokeReadInternal(JsonObject json);

	@Invoker(
		value = "readInternal",
		remap = false
	)
	void invokeReadInternal(FriendlyByteBuf buffer);

	@Invoker(
		value = "writeInternal",
		remap = false
	)
	void invokeWriteInternal(FriendlyByteBuf buffer);
};
