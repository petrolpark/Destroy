package com.petrolpark.destroy.recipe;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.entity.behaviour.BasinTooFullBehaviour;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.ReactionResult;
import com.petrolpark.destroy.chemistry.reactionresult.PrecipitateReactionResult;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.fluid.MixtureFluid;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ReactionInBasinRecipe extends MixingRecipe {

    private static final int BASIN_MAX_OUTPUT = 1000;

    public ReactionInBasinRecipe(ProcessingRecipeParams params) {
        super(params);
    };

    @Nullable
    public static ReactionInBasinRecipe create(Collection<FluidStack> availableFluids, Collection<ItemStack> availableItems, BasinBlockEntity basin) {
        ProcessingRecipeBuilder<ReactionInBasinRecipe> builder = new ProcessingRecipeBuilder<>(ReactionInBasinRecipe::new, Destroy.asResource("reaction_in_basin_"));

        boolean canReact = true; // Start by assuming we will be able to React

        boolean isBasinTooFullToReact = false;
        HeatLevel heatLevel = BasinBlockEntity.getHeatLevelOf(basin.getLevel().getBlockState(basin.getBlockPos().below())); // It thinks basin.getLevel() might be null

        Map<Mixture, Double> mixtures = new HashMap<>(availableFluids.size()); // A Map of all available Mixtures to the volume of them available (in Buckets)
        int totalAmount = 0; // How much Mixture there is

        // Check all Fluids are Mixturess
        for (FluidStack fluidStack : availableFluids) {
            if (!DestroyFluids.MIXTURE.get().isSame(fluidStack.getFluid())) { // Can't react with any non-Mixtures
                canReact = false;
                break;
            };
            int amount = fluidStack.getAmount();
            totalAmount += amount;
            Mixture mixture = Mixture.readNBT(fluidStack.getOrCreateTag().getCompound("Mixture"));
            if (mixture.isAtEquilibrium() && availableFluids.size() == 1) { // Don't do anything if there is only one Mixture and it is already at equilibrium
                canReact = false;
                break;
            };
            mixtures.put(mixture, (double)amount / 1000d);
        };

        if (canReact) {
            // TODO modify temp according to Heat Level
            Mixture mixture = Mixture.mix(mixtures);
            ReactionInBasinResult result = mixture.reactInBasin(totalAmount); // Mutably react the Mixture 

            int duration = Mth.clamp(result.ticks(), 40, 600); // Ensure this takes at least 2 seconds and less than 30 seconds

            // Set the duration of the Recipe to the time it took to React
            builder.duration(duration);
            // Add the resultant Mixture to the results for this Recipe
            FluidStack outputMixtureStack = MixtureFluid.of(result.amount(), mixture, "");
            builder.output(outputMixtureStack);

            // Add all the given Fluid Stacks as "required ingredients"
            availableFluids.stream().map(fluidStack -> FluidIngredient.fromFluidStack(fluidStack)).forEach(fluidIngredient -> builder.require(fluidIngredient));

            for (ReactionResult reactionresult : result.reactionresults()) {
                if (reactionresult instanceof PrecipitateReactionResult precipitationResult) {
                    builder.output(precipitationResult.getPrecipitate());
                };
                // TODO other Reaction Results
            };

            // Let the Player know if the Reaction cannot occur because the output Fluid will not fit
            if (outputMixtureStack.getAmount() > BASIN_MAX_OUTPUT) {
                isBasinTooFullToReact = true;
                canReact = false;
            };
        };

        basin.getBehaviour(BasinTooFullBehaviour.TYPE).tooFullToReact = isBasinTooFullToReact;
        basin.sendData();

        if (!canReact) {
            return null;
        };

        return builder.build();
    };

    @Override
    protected int getMaxFluidInputCount() {
        return 4;
    };

    /**
     * The outcome of {@link com.petrolpark.destroy.chemistry.Reaction reacting} a {@link com.petrolpark.destroy.chemistry.Reaction Mixture} in a Basin.
     * @param ticks The number of ticks it took for the Mixture to reach equilibrium
     * @param reactionresults The {@link com.petrolpark.destroy.chemistry.ReactionResult results} of Reacting this Mixture
     * @param amount The amount (in mB) of resultant Mixture
     */
    public static record ReactionInBasinResult(int ticks, List<ReactionResult> reactionresults, int amount) {};
    
};
