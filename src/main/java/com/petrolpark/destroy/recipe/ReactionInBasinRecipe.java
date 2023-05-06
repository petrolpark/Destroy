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
        ProcessingRecipeBuilder<ReactionInBasinRecipe> builder = new ProcessingRecipeBuilder<>(ReactionInBasinRecipe::new, Destroy.asResource("reaction_in_basin"));

        Map<Mixture, Double> mixtures = new HashMap<>(availableFluids.size()); // A Map of all available Mixtures to the volume of them available (in Buckets)
        int totalAmount = 0; // How much Mixture there is

        // Check all Fluids are Mixturess
        for (FluidStack fluidStack : availableFluids) {
            if (DestroyFluids.MIXTURE.get().isSame(fluidStack.getFluid())) return null;
            int amount = fluidStack.getAmount();
            totalAmount += amount;
            Mixture mixture = Mixture.readNBT(fluidStack.getOrCreateTag().getCompound("Mixture"));

            if (mixture.isAtEquilibrium() && availableFluids.size() == 1) return null; // Don't do anything if there is only one Mixture and it is already at equilibrium

            mixtures.put(mixture, (double)amount / 1000d);
        };

        // TODO modify temp according to Heat Level
        Mixture mixture = Mixture.mix(mixtures);
        ReactionInBasinResult result = mixture.reactInBasin(); // Mutably react the Mixture

        int duration = result.ticks > 40 ? result.ticks : 40; // Ensure this takes at least 2 seconds

        // Set the duration of the Recipe to the time it took to React
        builder.duration(duration);
        // Add the resultant Mixture to the results for this Recipe
        builder.output(MixtureFluid.of(totalAmount, mixture, mixture.getName().getString()));

        // Add all the given Fluid Stacks as "required ingredients"
        availableFluids.stream().map(fluidStack -> FluidIngredient.fromFluidStack(fluidStack)).forEach(fluidIngredient -> builder.require(fluidIngredient));

        return builder.build();
    };

    /**
     * The outcome of {@link com.petrolpark.destroy.chemistry.Reaction reacting} a {@link com.petrolpark.destroy.chemistry.Reaction Mixture} in a Basin.
     */
    public static class ReactionInBasinResult {
        /**
         * The number of ticks it took for the Mixture to reach equilibrium.
         */
        int ticks;
        /**
         * The {@link com.petrolpark.destroy.chemistry.ReactionResult results} of Reacting this Mixture.
         */
        Set<ReactionResult> reactionResults;

        public ReactionInBasinResult(int ticks, Set<ReactionResult> reactionResults) {
            this.ticks = ticks;
            this.reactionResults = reactionResults;
        };
    };
    
};
