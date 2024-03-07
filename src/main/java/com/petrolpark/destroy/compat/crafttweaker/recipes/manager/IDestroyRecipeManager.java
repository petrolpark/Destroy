package com.petrolpark.destroy.compat.crafttweaker.recipes.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.action.recipe.ActionRemoveRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.CTFluidIngredient;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.compat.crafttweaker.CTDestroy;
import com.petrolpark.destroy.compat.jei.DestroyJEI;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@ZenRegister
@ZenCodeType.Name("mods.destroy.IDestroyRecipeManager")
@Document("mods/destroy/IDestroyRecipeManager")
public interface IDestroyRecipeManager <T extends ProcessingRecipe<?>> extends IRecipeManager<T> {
    default ProcessingRecipeSerializer<T> getSerializer() {
        return getDestroyRecipeType().getSerializer();
    }

    /**
     * Registers a recipe using a builder approach.
     *
     * @param name          The name of the recipe.
     * @param recipeBuilder The recipe builder.
     */
    @ZenCodeType.Method
    default void registerRecipe(String name, Consumer<ProcessingRecipeBuilder<T>> recipeBuilder) {
        name = fixRecipeName(name);
        ResourceLocation recipeId = new ResourceLocation("crafttweaker", name);
        ProcessingRecipeBuilder<T> builder = new ProcessingRecipeBuilder<>(getSerializer().getFactory(), recipeId);
        recipeBuilder.accept(builder);
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, builder.build(), ""));
    }

    @ZenCodeType.Method("isMixture")
    default boolean checkMixture(CTFluidIngredient ingredient) {
        if(!(ingredient instanceof CTFluidIngredient.FluidStackIngredient fluidStack)) {
            return false;
        }
        CompoundTag internalTag = fluidStack.getMatchingStacks().get(0).getInternalTag();
        if(internalTag == null) return false;
        return internalTag.contains("MaximumConcentration") || internalTag.contains("Mixture");
    }

    /**
     * Throws an exception if the ingredient is a mixture
     * @param ingredient
     */
    @ZenCodeType.Method("checkMixture")
    default void checkMixtureOrThrow(CTFluidIngredient ingredient) {
        // check if the mixture is a fluid
        if(checkMixture(ingredient)) {
            throw new IllegalArgumentException("Input fluid cannot be a mixture");
        }
    }

    @ZenCodeType.Method
    default void removeRecipe(CTFluidIngredient input) {
        FluidIngredient nativeInput = CTDestroy.mapFluidIngredients(input);
        CraftTweakerAPI.apply(new ActionRemoveRecipe<>(this, iRecipe -> iRecipe.getFluidIngredients()
            .stream()
            .anyMatch(ingredient -> ingredient.equals(nativeInput))));
    }

    DestroyRecipeTypes getDestroyRecipeType();

    @Override
    default RecipeType<T> getRecipeType() {
        return getDestroyRecipeType().getType();
    }
}
