package com.petrolpark.destroy.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.fluid.CTFluidIngredient;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.component.IRecipeComponent;
import com.blamejared.crafttweaker.api.tag.CraftTweakerTagRegistry;
import com.blamejared.crafttweaker.api.tag.type.KnownTag;
import com.blamejared.crafttweaker.api.util.GenericUtil;
import com.blamejared.crafttweaker.api.util.IngredientUtil;
import com.blamejared.crafttweaker.api.util.random.Percentaged;
import com.google.gson.reflect.TypeToken;
import com.petrolpark.destroy.mixin.accessor.FluidTagIngredientAccessor;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CTDestroy {
    private static final Logger logger = CraftTweakerAPI.getLogger("Destroy");

    public static Logger getLogger() {
        return logger;
    }


    // Taken from MIT licensed code https://github.com/jaredlll08/CreateTweaker/blob/1.20.1/forge/src/main/java/com/blamejared/createtweaker/services/ForgePlatformService.java
    public static FluidIngredient mapFluidIngredients(CTFluidIngredient ingredient) {
        return ingredient.mapTo(CTDestroy::fromFluidStack, CTDestroy::fromTag, stream -> {
            throw new IllegalArgumentException("Unable to use a compound ingredient for Create!");
        });
    }

    private static FluidIngredient fromFluidStack(IFluidStack stack) {
        return FluidIngredient.fromFluidStack(stack.getInternal());
    }

    public static Percentaged<IItemStack> mapProcessingResult(ProcessingOutput result) {
        return IItemStack.of(result.getStack()).percent(result.getChance() * 100);
    }

    public static ProcessingOutput mapPercentagedToProcessingOutput(Percentaged<IItemStack> stack) {
        return new ProcessingOutput(stack.getData().getInternal(), (float) stack.getPercentage());
    }

    public static ProcessingOutput mapItemStackToProcessingOutput(IItemStack stack) {
        return new ProcessingOutput(stack.getInternal(), 1);
    }

    public static <T extends ProcessingRecipe<? extends Container>> ProcessingRecipeBuilder<T> withFluidOutputs(ProcessingRecipeBuilder<T> builder, List<IFluidStack> fluidOutputs) {

        builder.withFluidOutputs(fluidOutputs.stream()
            .map(IFluidStack::<FluidStack>getInternal)
            .collect(Collectors.toCollection(NonNullList::create)));
        return builder;
    }

    public static ProcessingRecipeBuilder<ProcessingRecipe<Container>> output(ProcessingRecipeBuilder<?> builder, IFluidStack output) {

        return GenericUtil.uncheck(builder.output(output.<FluidStack> getInternal()));
    }

    public static FluidIngredient fromTag(TagKey<Fluid> tag, int amount) {
        return FluidIngredient.fromTag(tag, amount);
    }

    public static CTFluidIngredient mapFluidIngredientsToCT(FluidIngredient ingredient) {
        if(ingredient instanceof FluidIngredient.FluidTagIngredient fti) {
            KnownTag<Fluid> tag = CraftTweakerTagRegistry.INSTANCE.knownTagManager(Registries.FLUID)
                .tag(((FluidTagIngredientAccessor) fti).destroy$getTag());
            return new CTFluidIngredient.FluidTagWithAmountIngredient(tag.asTagWithAmount());
        }
        Optional<CTFluidIngredient> reduce = ingredient.getMatchingFluidStacks()
            .stream()
            .map(IFluidStack::of)
            .map(IFluidStack::asFluidIngredient)
            .reduce(CTFluidIngredient::asCompound);
        return reduce.orElseThrow();
    }

    public static  <T extends ProcessingRecipe<?>> boolean doFluidIngredientsConflict(T first, T second) {
        return IngredientUtil.doIngredientsConflict(first.getFluidIngredients(),
            second.getFluidIngredients(),
            FluidIngredient.EMPTY::equals,
            fluidIngredient -> fluidIngredient.getMatchingFluidStacks().toArray(FluidStack[]::new),
            FluidStack::containsFluid);
    }

    public static Stream<IFluidStack> streamFluidResults(ProcessingRecipe<?> recipe) {
        return recipe.getFluidResults()
            .stream()
            .map(IFluidStack::of);
    }
    public static List<IFluidStack> getRecipeFluidResults(ProcessingRecipe<?> recipe) {
        return streamFluidResults(recipe).toList();
    }

    public static List<IFluidStack> getMatchingFluidStacks(FluidIngredient ingredient) {
        return ingredient.getMatchingFluidStacks()
            .stream()
            .map(IFluidStack::of)
            .collect(Collectors.toList());
    }

    public static final class RecipeInput {
        public static final IRecipeComponent<HeatCondition> HEAT = IRecipeComponent.simple(
            new ResourceLocation("crafttweaker", "input/heat"),
            new TypeToken<>() {},
            Objects::equals
        );

        private RecipeInput() {}
    }
}
