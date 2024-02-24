package com.petrolpark.destroy.compat.crafttweaker.recipes.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.CTFluidIngredient;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import org.openzen.zencode.java.ZenCodeType;

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

    default void checkMixture(CTFluidIngredient ingredient) {
        // check if the mixture is a fluid
        if(!(ingredient instanceof CTFluidIngredient.FluidStackIngredient fluidStack)) {
            return;
        }
        CompoundTag internalTag = fluidStack.getMatchingStacks().get(0).getInternalTag();
        if(internalTag == null) return;
        if(internalTag.contains("MaximumConcentration") || internalTag.contains("Mixture")) {
            throw new IllegalArgumentException("Input fluid cannot be a mixture");
        }
    }
    DestroyRecipeTypes getDestroyRecipeType();

    @Override
    default RecipeType<T> getRecipeType() {
        return getDestroyRecipeType().getType();
    }
}
