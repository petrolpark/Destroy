package com.petrolpark.destroy.mixin;

import java.util.Objects;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.petrolpark.destroy.fluid.ingredient.MixtureFluidIngredient;
import com.petrolpark.destroy.fluid.ingredient.mixturesubtype.MixtureFluidIngredientSubType;
import com.petrolpark.destroy.mixin.accessor.FluidIngredientAccessor;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.fluid.FluidIngredient.FluidStackIngredient;
import com.simibubi.create.foundation.fluid.FluidIngredient.FluidTagIngredient;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidIngredient.class)
public abstract class FluidIngredientMixin {

	@Unique
	private static final String destroy$fluidTagMemberName = "fluidTag";
	@Unique
	private static final String destroy$fluidMemberName = "fluid";
    
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
		if (json.has(destroy$fluidTagMemberName) || json.has(destroy$fluidMemberName) || MixtureFluidIngredient.MIXTURE_FLUID_INGREDIENT_SUBTYPES.keySet().stream().anyMatch(json::has)) {
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
		if (json.has(destroy$fluidMemberName)) {
			ingredient = new FluidIngredient.FluidStackIngredient();
		} else if (json.has(destroy$fluidTagMemberName)) {
			ingredient = new FluidIngredient.FluidTagIngredient();
		//
		
		// Deserialize Molecule-involving ingredients
		} else {
			for (Entry<String, MixtureFluidIngredientSubType<?>> mixtureFluidIngredientType : MixtureFluidIngredient.MIXTURE_FLUID_INGREDIENT_SUBTYPES.entrySet()) {
				if (json.has(mixtureFluidIngredientType.getKey())) {
					ingredient = mixtureFluidIngredientType.getValue().getNew();
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

	/**
	 * @author
	 * @reason
	 */
	@Overwrite(remap = false)
	public void write(FriendlyByteBuf buffer) {
		FluidIngredient $this = (FluidIngredient)(Object)this;
		String ingredientType = null;
		if ($this instanceof FluidStackIngredient) {
			ingredientType = destroy$fluidMemberName;
		} else if ($this instanceof FluidTagIngredient) {
			ingredientType = destroy$fluidTagMemberName;
		} else if ($this instanceof MixtureFluidIngredient mixtureFluid) {
			ingredientType = mixtureFluid.getType().getMixtureFluidIngredientSubtype();
		};
		if (ingredientType == null) throw new IllegalStateException("Unknown Fluid ingredient subtype");
		buffer.writeUtf(ingredientType);
		buffer.writeVarInt(((FluidIngredientAccessor)this).getAmountRequired());
		((FluidIngredientAccessor)this).invokeWriteInternal(buffer);
	};

	/**
	 * @author
	 * @reason
	 */
	@Overwrite(remap = false)
	public static FluidIngredient read(FriendlyByteBuf buffer) {
		String ingredientType = buffer.readUtf();
		FluidIngredient ingredient = null;
		if (ingredientType.equals(destroy$fluidMemberName)) {
			ingredient = new FluidStackIngredient();
		} else if (ingredientType.equals(destroy$fluidTagMemberName)) {
			ingredient = new FluidTagIngredient();
		} else {
			ingredient = MixtureFluidIngredient.MIXTURE_FLUID_INGREDIENT_SUBTYPES.get(ingredientType).getNew();
		};
		((FluidIngredientAccessor)ingredient).setAmountRequired(buffer.readVarInt());
		((FluidIngredientAccessor)ingredient).invokeReadInternal(buffer);
		return ingredient;
	};

	@Inject(method = "fromFluidStack", at = @At("HEAD"), cancellable = true, remap = false)
	private static void checkForCustomIngredients(FluidStack fluidStack, CallbackInfoReturnable<FluidIngredient> cir) {
		CompoundTag tag = fluidStack.getTag();
		if(tag == null) return;
		String subtypeName = tag.getString("MixtureFluidIngredientSubtype");
		if(subtypeName.isEmpty()) return;
		MixtureFluidIngredientSubType<?> subtype = MixtureFluidIngredient.MIXTURE_FLUID_INGREDIENT_SUBTYPES.get(subtypeName);
		if(subtype == null) return;
		cir.setReturnValue(subtype.fromNBT(tag, fluidStack.getAmount()));
	}
};
