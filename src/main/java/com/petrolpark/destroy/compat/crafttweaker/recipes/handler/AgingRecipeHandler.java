package com.petrolpark.destroy.compat.crafttweaker.recipes.handler;

import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker.api.util.random.Percentaged;
import com.petrolpark.destroy.compat.crafttweaker.CTDestroy;
import com.petrolpark.destroy.recipe.AgingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;

import java.util.stream.Collectors;

@IRecipeHandler.For(AgingRecipe.class)
public class AgingRecipeHandler implements IDestroyRecipeHandler<AgingRecipe> {

    @Override
    public String dumpToCommandString(IRecipeManager<? super AgingRecipe> manager, AgingRecipe recipe) {
        return String.format(
            "<recipetype:destroy:aging>.addRecipe(\"%s\", [%s], %s, %s, %s);",
            recipe.getId(),
            IFluidStack.of(recipe.getRequiredFluid()).getCommandString(),
            IFluidStack.of(recipe.getFluidResults().get(0)).getCommandString(),
            recipe.getRollableResults()
                .stream()
                .map(CTDestroy::mapProcessingResult)
                .map(Percentaged::getCommandString)
                .collect(Collectors.joining(", ")),
            recipe.getProcessingDuration()
        );
    }

    @Override
    public ProcessingRecipeBuilder.ProcessingRecipeFactory<AgingRecipe> factory() {
        return AgingRecipe::new;
    }
}
