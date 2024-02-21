package com.petrolpark.destroy.compat.crafttweaker.recipes.handler;

import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.component.BuiltinRecipeComponents;
import com.blamejared.crafttweaker.api.recipe.component.IDecomposedRecipe;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.petrolpark.destroy.compat.crafttweaker.CTDestroy;
import com.petrolpark.destroy.recipe.ChargingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

@IRecipeHandler.For(ChargingRecipe.class)
public class ChargingRecipeHandler implements IDestroyRecipeHandler<ChargingRecipe> {

    @Override
    public String dumpToCommandString(IRecipeManager<? super ChargingRecipe> iRecipeManager, ChargingRecipe recipe) {
        return String.format(
            "<recipetype:destroy:charging>.addRecipe(\"%s\", %s, %s);",
            recipe.getId(),
            IIngredient.fromIngredient(recipe.getInput()).getCommandString(),
            CTDestroy.mapProcessingResult(recipe.getResult()).getCommandString()
        );
    }

    @Override
    public Optional<IDecomposedRecipe> decompose(IRecipeManager<? super ChargingRecipe> manager, ChargingRecipe recipe) {
        IDecomposedRecipe result = IDecomposedRecipe.builder()
            .with(BuiltinRecipeComponents.Input.INGREDIENTS, List.of(IIngredient.fromIngredient(recipe.getInput())))
            .with(BuiltinRecipeComponents.Output.CHANCED_ITEMS, List.of(CTDestroy.mapProcessingResult(recipe.getResult())))
            .build();
        return Optional.of(result);
    }

    @Override
    public Optional<ChargingRecipe> recompose(IRecipeManager<? super ChargingRecipe> manager, ResourceLocation name, IDecomposedRecipe recipe) {
        ProcessingRecipeBuilder<ChargingRecipe> builder = new ProcessingRecipeBuilder<>(factory(), name);
        builder.withItemIngredients(recipe.getOrThrow(BuiltinRecipeComponents.Input.INGREDIENTS).get(0).asVanillaIngredient());
        builder.withItemOutputs(CTDestroy.mapPercentagedToProcessingOutput(recipe.getOrThrow(BuiltinRecipeComponents.Output.CHANCED_ITEMS).get(0)));
        return Optional.of(builder.build());
    }

    @Override
    public ProcessingRecipeBuilder.ProcessingRecipeFactory<ChargingRecipe> factory() {
        return ChargingRecipe::new;
    }
}
