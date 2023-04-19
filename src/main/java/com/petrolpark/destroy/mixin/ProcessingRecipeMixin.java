package com.petrolpark.destroy.mixin;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.compat.jei.DestroyJEI;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.fluid.MoleculeFluidIngredient;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.utility.recipe.IRecipeTypeInfo;

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
        for (FluidIngredient ingredient : ((ProcessingRecipeParamsAccessor)params).getFluidIngredients()) {
            if (ingredient instanceof MoleculeFluidIngredient moleculeIngredient) {
                Molecule molecule = moleculeIngredient.getMolecule();
                DestroyJEI.MOLECULES_INPUT.putIfAbsent(molecule, new ArrayList<>()); // Create the List if it's not there
                DestroyJEI.MOLECULES_INPUT.get(molecule).add((ProcessingRecipe<RecipeWrapper>)(Object)this); // Unchecked conversion (fine because this is a Mixin)
            };
            //TODO Molecule Tag ingredients
        };
        for (FluidStack fluidResult : ((ProcessingRecipeParamsAccessor)params).getFluidResults()) {
            if (fluidResult.getFluid() == DestroyFluids.MIXTURE.get()) {
                ReadOnlyMixture mixture = ReadOnlyMixture.readNBT(fluidResult.getOrCreateTag().getCompound("Mixture"));
                for (Molecule molecule : mixture.getContents(true)) {
                    DestroyJEI.MOLECULES_OUTPUT.putIfAbsent(molecule, new ArrayList<>()); // Create the List if it's not there
                    DestroyJEI.MOLECULES_OUTPUT.get(molecule).add((ProcessingRecipe<RecipeWrapper>)(Object)this); // Unchecked conversion (fine because this is a Mixin)
                };
            };
        };


    };


};
