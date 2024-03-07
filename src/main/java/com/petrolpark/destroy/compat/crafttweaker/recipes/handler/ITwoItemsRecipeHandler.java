package com.petrolpark.destroy.compat.crafttweaker.recipes.handler;

import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.component.BuiltinRecipeComponents;
import com.blamejared.crafttweaker.api.recipe.component.IDecomposedRecipe;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.petrolpark.destroy.compat.crafttweaker.CTDestroy;
import com.petrolpark.destroy.recipe.ChargingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.Optional;

public interface ITwoItemsRecipeHandler<T extends ProcessingRecipe<?>> extends IDestroyRecipeHandler<T> {
    @Override
    default String dumpToCommandString(IRecipeManager<? super T> iRecipeManager, T recipe) {
        return String.format(
            "<recipetype:destroy:charging>.addRecipe(\"%s\", %s, %s);",
            recipe.getId(),
            IIngredient.fromIngredient(recipe.getIngredients().get(0)).getCommandString(),
            CTDestroy.mapProcessingResult(recipe.getRollableResults().get(0)).getCommandString()
        );
    }

    @Override
    default Optional<IDecomposedRecipe> decompose(IRecipeManager<? super T> manager, T recipe) {
        IDecomposedRecipe result = IDecomposedRecipe.builder()
            .with(BuiltinRecipeComponents.Input.INGREDIENTS, List.of(IIngredient.fromIngredient(recipe.getIngredients().get(0))))
            .with(BuiltinRecipeComponents.Output.CHANCED_ITEMS, List.of(CTDestroy.mapProcessingResult(recipe.getRollableResults().get(0))))
            .build();
        return Optional.of(result);
    }

    @Override
    default Optional<T> recompose(IRecipeManager<? super T> manager, ResourceLocation name, IDecomposedRecipe recipe) {
        ProcessingRecipeBuilder<T> builder = new ProcessingRecipeBuilder<>(factory(), name);
        builder.withItemIngredients(recipe.getOrThrow(BuiltinRecipeComponents.Input.INGREDIENTS).get(0).asVanillaIngredient());
        builder.withItemOutputs(CTDestroy.mapPercentagedToProcessingOutput(recipe.getOrThrow(BuiltinRecipeComponents.Output.CHANCED_ITEMS).get(0)));
        return Optional.of(builder.build());
    }
}
