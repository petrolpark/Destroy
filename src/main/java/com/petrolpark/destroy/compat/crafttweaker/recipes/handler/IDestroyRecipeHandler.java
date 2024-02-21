package com.petrolpark.destroy.compat.crafttweaker.recipes.handler;

import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.component.BuiltinRecipeComponents;
import com.blamejared.crafttweaker.api.recipe.component.IDecomposedRecipe;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker.api.util.IngredientUtil;
import com.blamejared.crafttweaker.api.util.random.Percentaged;
import com.mojang.datafixers.util.Either;
import com.petrolpark.destroy.compat.crafttweaker.CTDestroy;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface IDestroyRecipeHandler<T extends ProcessingRecipe<?>> extends IRecipeHandler<T> {

    default String getRecipeTypeName() {
        return null;
    }

    @Override
    default String dumpToCommandString(IRecipeManager<? super T> iRecipeManager, T recipe) {
        Either<Stream<Percentaged<IItemStack>>, IFluidStack> result;
        List<IFluidStack> fluidResults = CTDestroy.getRecipeFluidResults(recipe);
        if(!fluidResults.isEmpty()) {
            result = Either.right(fluidResults.get(0));
        } else {
            result = Either.left(recipe.getRollableResults()
                .stream()
                .map(CTDestroy::mapProcessingResult));
        }

        return String.format("<recipetype:destroy:" + getRecipeTypeName() + ">.addRecipe(\"%s\", <constant:create:heat_condition:%s>, [%s], [%s], [%s], [%s]);",
            recipe.getId(),
            recipe.getRequiredHeat().name().toLowerCase(Locale.ENGLISH),
            result.map(results -> results.map(Percentaged::getCommandString)
                .collect(Collectors.joining(", ")), IFluidStack::getCommandString),
            recipe.getIngredients()
                .stream()
                .map(IIngredient::fromIngredient)
                .map(IIngredient::getCommandString)
                .collect(Collectors.joining(", ")),
            recipe.getFluidIngredients()
                .stream()
                .map(CTDestroy::getMatchingFluidStacks)
                .flatMap(Collection::stream)
                .map(IFluidStack::getCommandString)
                .collect(Collectors.joining(", ")),
            CTDestroy.streamFluidResults(recipe)
                .map(IFluidStack::getCommandString)
                .collect(Collectors.joining(", "))
        );
    }
    @Override
    default  <U extends Recipe<?>> boolean doesConflict(IRecipeManager<? super T> manager, T firstRecipe, U secondRecipe) {
        if(isGoodRecipe(firstRecipe, secondRecipe)) {
            final T second = (T) secondRecipe;
            if(firstRecipe.getIngredients().size() != second.getIngredients().size() || !firstRecipe.getRequiredHeat()
                .equals(second.getRequiredHeat())) {
                return false;
            }
            return IngredientUtil.doIngredientsConflict(firstRecipe.getIngredients(), secondRecipe.getIngredients())
                 && CTDestroy.doFluidIngredientsConflict(firstRecipe, second);

        }

        return false;
    }

    default boolean isGoodRecipe(Recipe<?> self, Recipe<?> other) {
        return other.getClass().isAssignableFrom(self.getClass());
    }

    default Optional<IDecomposedRecipe> decompose(IRecipeManager<? super T> manager, T recipe) {
        return Optional.of(IDecomposedRecipe.builder()
            .with(BuiltinRecipeComponents.Input.INGREDIENTS, recipe.getIngredients()
                .stream()
                .map(IIngredient::fromIngredient)
                .toList())
            .with(BuiltinRecipeComponents.Output.CHANCED_ITEMS, recipe.getRollableResults()
                .stream()
                .map(CTDestroy::mapProcessingResult)
                .toList())
            .with(BuiltinRecipeComponents.Input.FLUID_INGREDIENTS, recipe.getFluidIngredients()
                .stream()
                .map(CTDestroy::mapFluidIngredientsToCT)
                .toList())
            .with(BuiltinRecipeComponents.Output.FLUIDS, CTDestroy.getRecipeFluidResults(recipe)
                .stream()
                .toList())
            .with(BuiltinRecipeComponents.Processing.TIME, recipe.getProcessingDuration())
            .with(CTDestroy.RecipeInput.HEAT, recipe.getRequiredHeat())
            .build()
        );
    }

    default Optional<T> recompose(IRecipeManager<? super T> manager, ResourceLocation name, IDecomposedRecipe recipe) {
        ProcessingRecipeBuilder<T> builder = new ProcessingRecipeBuilder<>(factory(), name);
        builder.withItemIngredients(recipe.getOrThrow(BuiltinRecipeComponents.Input.INGREDIENTS)
            .stream()
            .map(IIngredient::asVanillaIngredient)
            .collect(Collectors.toCollection(NonNullList::create)));
        builder.withItemOutputs(recipe.getOrThrow(BuiltinRecipeComponents.Output.CHANCED_ITEMS)
            .stream()
            .map(CTDestroy::mapPercentagedToProcessingOutput)
            .collect(Collectors.toCollection(NonNullList::create)));
        builder.withFluidIngredients(recipe.getOrThrow(BuiltinRecipeComponents.Input.FLUID_INGREDIENTS)
            .stream()
            .map(CTDestroy::mapFluidIngredients)
            .collect(Collectors.toCollection(NonNullList::create)));
        CTDestroy.withFluidOutputs(builder, recipe.getOrThrow(BuiltinRecipeComponents.Output.FLUIDS));
        builder.duration(recipe.getOrThrowSingle(BuiltinRecipeComponents.Processing.TIME));
        builder.requiresHeat(recipe.getOrThrowSingle(CTDestroy.RecipeInput.HEAT));
        return Optional.of(builder.build());
    }

    ProcessingRecipeBuilder.ProcessingRecipeFactory<T> factory();
}
