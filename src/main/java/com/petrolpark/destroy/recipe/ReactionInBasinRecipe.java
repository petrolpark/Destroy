package com.petrolpark.destroy.recipe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.ReactionResult;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.fluid.MixtureFluid;
import com.petrolpark.destroy.item.DestroyItems;
import com.simibubi.create.content.contraptions.components.mixer.MixingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ReactionInBasinRecipe extends MixingRecipe {

    public ReactionInBasinRecipe(ProcessingRecipeParams params) {
        super(params);
    };

    @Nullable
    public static ReactionInBasinRecipe create(Collection<FluidStack> availableFluids, Collection<ItemStack> availableItems, HeatLevel heatLevel) {
        ProcessingRecipeBuilder<ReactionInBasinRecipe> builder = new ProcessingRecipeBuilder<>(ReactionInBasinRecipe::new, Destroy.asResource("reaction_in_basin_"));

        Map<Mixture, Double> mixtures = new HashMap<>(availableFluids.size()); // A Map of all available Mixtures to the volume of them available (in Buckets)
        int totalAmount = 0; // How much Mixture there is

        Destroy.LOGGER.info("Ive got these fluids:");
        // Check all Fluids are Mixturess
        for (FluidStack fluidStack : availableFluids) {
            Destroy.LOGGER.info(fluidStack.getTranslationKey()+" with tag "+fluidStack.getTag().getAsString());
            if (!DestroyFluids.MIXTURE.get().isSame(fluidStack.getFluid())) return null;
            int amount = fluidStack.getAmount();
            totalAmount += amount;
            Mixture mixture = Mixture.readNBT(fluidStack.getOrCreateTag().getCompound("Mixture"));

            if (mixture.isAtEquilibrium() && availableFluids.size() == 1) return null; // Don't do anything if there is only one Mixture and it is already at equilibrium

            mixtures.put(mixture, (double)amount / 1000d);
        };

        // TODO modify temp according to Heat Level
        Mixture mixture = Mixture.mix(mixtures);
        Destroy.LOGGER.info("Heres my result: "+mixture.getContentsString());
        ReactionInBasinResult result = mixture.reactInBasin(); // Mutably react the Mixture 

        int duration = 40;
        //int duration = result.ticks() > 40 ? result.ticks() : 40; // Ensure this takes at least 2 seconds 

        // Set the duration of the Recipe to the time it took to React
        builder.duration(duration);
        // Add the resultant Mixture to the results for this Recipe
        builder.output(MixtureFluid.of(totalAmount, mixture, mixture.getName().getString()));

        // Add all the given Fluid Stacks as "required ingredients"
        availableFluids.stream().map(fluidStack -> FluidIngredient.fromFluidStack(fluidStack)).forEach(fluidIngredient -> builder.require(fluidIngredient));
        builder.require(DestroyItems.ABS.get());

        Destroy.LOGGER.info("I'm going to need thesefluids please: ");
        //builder.build().fluidIngredients.forEach(ingredient -> ingredient.getMatchingFluidStacks().forEach(fluidStack -> Destroy.LOGGER.info("required ingredient: "+fluidStack.getTranslationKey()+" with tag "+fluidStack.getTag().getAsString())));
        //builder.build().fluidResults.forEach(fluidStack -> Destroy.LOGGER.info("resultant fluid: "+fluidStack.getTranslationKey()+" with tag "+fluidStack.getTag().getAsString()));

        return builder.build();
    };

    @Override
    protected int getMaxFluidInputCount() {
        return 4;
    };

    /**
     * The outcome of {@link com.petrolpark.destroy.chemistry.Reaction reacting} a {@link com.petrolpark.destroy.chemistry.Reaction Mixture} in a Basin.
     * @param ticks The number of ticks it took for the Mixture to reach equilibrium.
     * @param reactionResults The {@link com.petrolpark.destroy.chemistry.ReactionResult results} of Reacting this Mixture.
     */
    public static record ReactionInBasinResult(int ticks, Set<ReactionResult> reactionResults) {};
    
};
