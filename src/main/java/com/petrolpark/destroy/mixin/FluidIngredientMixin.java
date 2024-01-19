package com.petrolpark.destroy.mixin;

import java.util.Objects;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.petrolpark.destroy.fluid.ingredient.MixtureFluidIngredient;
import com.petrolpark.destroy.mixin.accessor.FluidIngredientAccessor;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.fluid.FluidIngredient.FluidStackIngredient;
import com.simibubi.create.foundation.fluid.FluidIngredient.FluidTagIngredient;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

@Mixin(FluidIngredient.class)
public abstract class FluidIngredientMixin {

	private static final String
	fluidTagMemberName = "fluidTag",
	fluidMemberName = "fluid";
    
	/**
	 * Overwriting of {@link com.simibubi.create.foundation.fluid.FluidIngredient#isFluidIngredient FluidIngredient} to
	 * say {@link com.petrolpark.destroy.fluid.ingredient.MoleculeFluidIngredient Molecule ingredients}
	 * are valid.
	 */
    @Overwrite(remap = false)
    public static boolean isFluidIngredient(@Nullable JsonElement je) {
        if (je == null || je.isJsonNull())
			return false;
		if (!je.isJsonObject())
			return false;
		JsonObject json = je.getAsJsonObject();
		if (json.has(fluidTagMemberName) || json.has(fluidMemberName) || MixtureFluidIngredient.MIXTURE_FLUID_INGREDIENT_SUBTYPES.keySet().stream().anyMatch(json::has)) {
            return true;
        };
		return false;
    };

	/**
	 * Overwritten but mostly copied from {@link com.simibubi.create.foundation.fluid.FluidIngredient#deserialize FluidIngredient}.
	 * This deserializes {@link com.petrolpark.destroy.fluid.ingredient.MoleculeFluidIngredient Molecule ingredients}.
	 */
    @Overwrite(remap = false)
    public static FluidIngredient deserialize(@Nullable JsonElement je) {

		if (je == null) return FluidIngredient.EMPTY;

		// All copied from Create source code.
		if (!isFluidIngredient(je))
			throw new JsonSyntaxException("Invalid fluid ingredient: " + Objects.toString(je));

		JsonObject json = je.getAsJsonObject(); // It thinks 'je' might be null (it can't be at this point)
		FluidIngredient ingredient = null;
		if (json.has(fluidMemberName)) {
			ingredient = new FluidIngredient.FluidStackIngredient();
		} else if (json.has(fluidTagMemberName)) {
			ingredient = new FluidIngredient.FluidTagIngredient();
		//
		
		// Deserialize Molecule-involving ingredients
		} else {
			for (Entry<String, MixtureFluidIngredient> mixtureFluidIngredient : MixtureFluidIngredient.MIXTURE_FLUID_INGREDIENT_SUBTYPES.entrySet()) {
				if (json.has(mixtureFluidIngredient.getKey())) {
					ingredient = mixtureFluidIngredient.getValue().getNew();
				};
			};
		};

		if (ingredient == null) throw new IllegalStateException("Unknown Fluid Type");

		// The rest is all copied from the Create Source code
		((FluidIngredientAccessor)ingredient).invokeReadInternal(json);

		if (!json.has("amount"))
			throw new JsonSyntaxException("Fluid ingredient has to define an amount");
		((FluidIngredientAccessor)ingredient).setAmountRequired(GsonHelper.getAsInt(json, "amount"));

		return ingredient;
	};

	@Overwrite(remap = false)
	public void write(FriendlyByteBuf buffer) {
		FluidIngredient $this = (FluidIngredient)(Object)this;
		String ingredientType = null;
		if ($this instanceof FluidStackIngredient) {
			ingredientType = fluidMemberName;
		} else if ($this instanceof FluidTagIngredient) {
			ingredientType = fluidTagMemberName;
		} else if ($this instanceof MixtureFluidIngredient mixtureFluid) {
			ingredientType = mixtureFluid.getMixtureFluidIngredientSubtype();
		};
		if (ingredientType == null) throw new IllegalStateException("Unknown Fluid ingredient subtype");
		buffer.writeUtf(ingredientType);
		buffer.writeVarInt(((FluidIngredientAccessor)this).getAmountRequired());
		((FluidIngredientAccessor)this).invokeWriteInternal(buffer);
	};

	@Overwrite(remap = false)
	public static FluidIngredient read(FriendlyByteBuf buffer) {
		String ingredientType = buffer.readUtf();
		FluidIngredient ingredient = null;
		if (ingredientType.equals(fluidMemberName)) {
			ingredient = new FluidStackIngredient();
		} else if (ingredientType.equals(fluidTagMemberName)) {
			ingredient = new FluidTagIngredient();
		} else {
			ingredient = MixtureFluidIngredient.MIXTURE_FLUID_INGREDIENT_SUBTYPES.get(ingredientType);
		};
		((FluidIngredientAccessor)ingredient).setAmountRequired(buffer.readInt());
		((FluidIngredientAccessor)ingredient).invokeReadInternal(buffer);
		return ingredient;
	};
};
