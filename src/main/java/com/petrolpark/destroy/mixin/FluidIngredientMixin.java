package com.petrolpark.destroy.mixin;

import java.util.Objects;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.petrolpark.destroy.fluid.ingredient.MoleculeFluidIngredient;
import com.petrolpark.destroy.mixin.accessor.FluidIngredientAccessor;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.util.GsonHelper;

@Mixin(FluidIngredient.class)
public class FluidIngredientMixin {

	private static final String
	fluidTagMemberName = "fluidTag",
	fluidMemberName = "fluid",
	moleculeFluidMemberName = "moleculeFluid",
	moleculeTagFluidMemberName = "moleculeTagFluid";
    
	/**
	 * Overwriting of {@link com.simibubi.create.foundation.fluid.FluidIngredient#isFluidIngredient FluidIngredient} to
	 * say {@link com.petrolpark.destroy.fluid.ingredient.MoleculeFluidIngredient Molecule ingredients} and //TODO Molecule Tag ingredient
	 * Molecule Tag ingredients are valid.
	 */
    @Overwrite
    public static boolean isFluidIngredient(@Nullable JsonElement je) {
        if (je == null || je.isJsonNull())
			return false;
		if (!je.isJsonObject())
			return false;
		JsonObject json = je.getAsJsonObject();
		if (json.has(fluidTagMemberName) || json.has(fluidMemberName) || json.has(moleculeFluidMemberName) || json.has(moleculeTagFluidMemberName)) {
            return true;
        };
		return false;
    };

	/**
	 * Overwritten but mostly copied from {@link com.simibubi.create.foundation.fluid.FluidIngredient#deserialize FluidIngredient}.
	 * This deserializes {@link com.petrolpark.destroy.fluid.ingredient.MoleculeFluidIngredient Molecule ingredients} and //TODO Molecule Tag ingredient
	 * Molecule Tag ingredients.
	 */
    @Overwrite
	@SuppressWarnings("null")
    public static FluidIngredient deserialize(@Nullable JsonElement je) {

		// All copied from Create source code.
		if (!isFluidIngredient(je))
			throw new JsonSyntaxException("Invalid fluid ingredient: " + Objects.toString(je));

		JsonObject json = je.getAsJsonObject(); // It thinks 'je' might be null (it can't be at this point)
		FluidIngredient ingredient;
		if (json.has(fluidMemberName)) {
			ingredient = new FluidIngredient.FluidStackIngredient();
		} else if (json.has(fluidTagMemberName)) {
			ingredient = new FluidIngredient.FluidTagIngredient();
		//
		
		// Deserialize Molecule-involving ingredients
		} else if (json.has(moleculeFluidMemberName)) {
			ingredient = new MoleculeFluidIngredient();
		} else { // If it's a Molecule Tag Fluid Ingredient
			ingredient = new FluidIngredient.FluidStackIngredient(); // TODO change to right class
		};

		// The rest is all copied from the Create Source code
		((FluidIngredientAccessor)ingredient).invokeReadInternal(json);

		if (!json.has("amount"))
			throw new JsonSyntaxException("Fluid ingredient has to define an amount");
		((FluidIngredientAccessor)ingredient).setAmountRequired(GsonHelper.getAsInt(json, "amount"));

		return ingredient;
	}
};
