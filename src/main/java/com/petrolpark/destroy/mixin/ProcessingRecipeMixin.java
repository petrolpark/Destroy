package com.petrolpark.destroy.mixin;

import java.util.ArrayList;
import java.util.HashSet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.compat.jei.DestroyJEI;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.fluid.ingredient.MixtureFluidIngredient;
import com.petrolpark.destroy.mixin.accessor.ProcessingRecipeParamsAccessor;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;

@Mixin(ProcessingRecipe.class)
public abstract class ProcessingRecipeMixin {
    
    /**
     * Injection into {@link com.simibubi.create.content.contraptions.processing.ProcessingRecipe#ProcessingRecipe ProcessingRecipe}.
     * If a Recipe produces or required a Molecule, this adds the created Recipe to the lists of Recipes in which a {@link com.petrolpark.destroy.chemistry.Mixture Mixture} is an
     * {@link com.petrolpark.destroy.compat.jei.DestroyJEI#MOLECULES_INPUT ingredient} or {@link com.petrolpark.destroy.compat.jei.DestroyJEI#MOLECULES_OUTPUT result}.
     */
    @SuppressWarnings("unchecked")
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void inInit(IRecipeTypeInfo typeInfo, ProcessingRecipeParams params, CallbackInfo ci) {
        if (!DestroyJEI.MOLECULE_RECIPES_NEED_PROCESSING) return;
        for (FluidIngredient ingredient : ((ProcessingRecipeParamsAccessor)params).getFluidIngredients()) {
            if (ingredient instanceof MixtureFluidIngredient<?> mixtureFluidIngredient) {
                CompoundTag fluidTag = new CompoundTag();
                mixtureFluidIngredient.addNBT(fluidTag);
                for (Molecule molecule : mixtureFluidIngredient.getType().getContainedMolecules(fluidTag)) {
                    DestroyJEI.MOLECULES_INPUT.computeIfAbsent(molecule, e -> new HashSet<>()).add((ProcessingRecipe<RecipeWrapper>)(Object)this);
                };
            };
        };
        for (FluidStack fluidResult : ((ProcessingRecipeParamsAccessor)params).getFluidResults()) {
            if (DestroyFluids.isMixture(fluidResult)) {
                ReadOnlyMixture mixture = ReadOnlyMixture.readNBT(ReadOnlyMixture::new, fluidResult.getOrCreateTag().getCompound("Mixture"));
                for (Molecule molecule : mixture.getContents(true)) {
                    DestroyJEI.MOLECULES_OUTPUT.computeIfAbsent(molecule, e -> new HashSet<>()).add((ProcessingRecipe<RecipeWrapper>)(Object)this);
                };
            };
        };


    };


};
