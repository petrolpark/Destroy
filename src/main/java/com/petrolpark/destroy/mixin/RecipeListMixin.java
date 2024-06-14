package com.petrolpark.destroy.mixin;

import com.blamejared.crafttweaker.api.recipe.RecipeList;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.compat.jei.DestroyJEI;
import com.petrolpark.destroy.fluid.ingredient.ConcentrationRangeFluidIngredient;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(value = RecipeList.class, remap = false)
public class RecipeListMixin<T extends Recipe<?>> {
    @Shadow(remap = false) @Final private Map<ResourceLocation, T> recipes;

    @Shadow(remap = false) @Final private Map<ResourceLocation, Recipe<?>> byName;

    /**
     * @author SashaSemenishchev
     * @reason To properly handle removing from molecule -> recipe lookups
     */
    @Overwrite(remap = false)
    public void removeByRecipeTest(Predicate<T> recipePredicate) {
        Iterator<ResourceLocation> iterator = recipes.keySet().iterator();

        while(iterator.hasNext()) {
            ResourceLocation next = iterator.next();
            T recipe = this.recipes.get(next);
            if(recipePredicate.test(recipe)) {
                byName.remove(next);
                iterator.remove();
                destroy$handleChemicalRecipeRemoval(recipe);
            }
        }
    }

    /**
     * @author SashaSemenishchev
     * @reason Properly handle molecule inputs/outputs
     */
    @Overwrite(remap = false)
    public void remove(ResourceLocation id) {
        T removed = this.recipes.remove(id);
        destroy$handleChemicalRecipeRemoval(removed);
        this.byName.remove(id);
    }

    /**
     * Add new ways of obtaining or ingredient molecules to DestroyJEI lookups
     */
    @Inject(method = "add", at = @At("RETURN"), remap = false)
    public void addToMoleculeLookup(ResourceLocation id, T recipe, CallbackInfo ci) {
        if(!(recipe instanceof ProcessingRecipe<?> processingRecipe)) return;
        for(FluidIngredient ingredient : processingRecipe.getFluidIngredients()) {
            if(!(ingredient instanceof ConcentrationRangeFluidIngredient<?> destroyIngredient)) continue;
            for(Molecule molecule : destroyIngredient.getMoleculeParticipants()) {
                destroy$ifAbsent(DestroyJEI.MOLECULES_INPUT, molecule).add(recipe);
            }
        }

        for(FluidStack fluidResult : processingRecipe.getFluidResults()) {
            Tag mixtureTag = fluidResult.getOrCreateTag().get("Mixture");
            if(mixtureTag == null) continue;
            ReadOnlyMixture resultingMixture = ReadOnlyMixture.readNBT(ReadOnlyMixture::new, (CompoundTag) mixtureTag);
            for(Molecule molecule : resultingMixture.getContents(false)) {
                destroy$ifAbsent(DestroyJEI.MOLECULES_OUTPUT, molecule).add(recipe);
            }
        }
    }

    @Unique
    private static Set<Recipe<?>> destroy$ifAbsent(Map<Molecule, Set<Recipe<?>>> initial, Molecule query) {
        return initial.computeIfAbsent(query, d -> new HashSet<>());
    }

    @Unique
    private static <T extends Recipe<?>> void destroy$handleChemicalRecipeRemoval(T recipe) {
        if(!(recipe instanceof ProcessingRecipe<?> processingRecipe)) return;
        NonNullList<FluidStack> fluidResults = processingRecipe.getFluidResults();
        Set<Molecule> removedMoleculeRecipes = new HashSet<>();
        for(FluidStack fluidResult : fluidResults) {
            Tag mixtureTag = fluidResult.getOrCreateTag().get("Mixture");
            if(mixtureTag == null) continue;
            ReadOnlyMixture resultingMixture = ReadOnlyMixture.readNBT(ReadOnlyMixture::new, (CompoundTag) mixtureTag);
            if(resultingMixture == null) continue;
            removedMoleculeRecipes.addAll(resultingMixture.getContents(false));
        }

        for(Molecule molecule : removedMoleculeRecipes) {
            destroy$ifAbsent(DestroyJEI.MOLECULES_OUTPUT, molecule).remove(recipe);
        }

        for(FluidIngredient ingredient : processingRecipe.getFluidIngredients()) {
            if(!(ingredient instanceof ConcentrationRangeFluidIngredient<?> destroyIngredient)) continue;
            for(Molecule molecule : destroyIngredient.getMoleculeParticipants()) {
                destroy$ifAbsent(DestroyJEI.MOLECULES_INPUT, molecule).remove(recipe);
            }
        }
    }
}
