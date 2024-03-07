package com.petrolpark.destroy.compat.crafttweaker.recipes.handler;

import com.blamejared.crafttweaker.api.fluid.CTFluidIngredient;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.component.BuiltinRecipeComponents;
import com.blamejared.crafttweaker.api.recipe.component.IDecomposedRecipe;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.petrolpark.destroy.compat.crafttweaker.CTDestroy;
import com.petrolpark.destroy.recipe.DistillationRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.stream.Collectors;

@IRecipeHandler.For(DistillationRecipe.class)
public class DistillationRecipeHandler implements IDestroyRecipeHandler<DistillationRecipe> {
    @Override
    public String dumpToCommandString(IRecipeManager<? super DistillationRecipe> manager, DistillationRecipe recipe) {
        return String.format(
            "<recipetype:destroy:distillation>.addRecipe(\"%s\", <constant:create:heat_condition:%s>, %s, [%s]);",
            recipe.getId(),
            recipe.getRequiredHeat().name().toLowerCase(Locale.ENGLISH),
            CTDestroy.getMatchingFluidStacks(recipe.getRequiredFluid()).stream()
                .map(IFluidStack::getCommandString)
                .collect(Collectors.joining(", ")),
            recipe.getFluidResults()
                .stream()
                .map(fluid -> IFluidStack.of(fluid).getCommandString())
                .collect(Collectors.joining(", "))
        );
    }

    @Override
    public Optional<IDecomposedRecipe> decompose(IRecipeManager<? super DistillationRecipe> manager, DistillationRecipe recipe) {
        List<IFluidStack> results = new ArrayList<>(recipe.getFractions());
        for(FluidStack fluidResult : recipe.getFluidResults()) {
            results.add(IFluidStack.of(fluidResult));
        }
        IDecomposedRecipe result = IDecomposedRecipe.builder()
            .with(BuiltinRecipeComponents.Input.FLUID_INGREDIENTS, List.of(IFluidStack.of(recipe.getRequiredFluid()).asFluidIngredient()))
            .with(BuiltinRecipeComponents.Output.FLUIDS, results)
            .build();
        return Optional.of(result);
    }

    @Override
    public Optional<DistillationRecipe> recompose(IRecipeManager<? super DistillationRecipe> manager, ResourceLocation name, IDecomposedRecipe recipe) {
        ProcessingRecipeBuilder<DistillationRecipe> builder = new ProcessingRecipeBuilder<>(factory(), name);
        CTFluidIngredient source = recipe
            .getOrThrow(BuiltinRecipeComponents.Input.FLUID_INGREDIENTS)
            .get(0);
        builder.withFluidIngredients(CTDestroy.mapFluidIngredients(source));
        CTDestroy.withFluidOutputs(builder, recipe.getOrThrow(BuiltinRecipeComponents.Output.FLUIDS));
        return Optional.of(builder.build());
    }

    @Override
    public ProcessingRecipeBuilder.ProcessingRecipeFactory<DistillationRecipe> factory() {
        return DistillationRecipe::new;
    }
}
