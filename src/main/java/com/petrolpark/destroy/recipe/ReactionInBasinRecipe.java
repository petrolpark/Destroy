package com.petrolpark.destroy.recipe;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.entity.behaviour.ExtendedBasinBehaviour;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.ReactionResult;
import com.petrolpark.destroy.chemistry.Mixture.Phases;
import com.petrolpark.destroy.chemistry.reactionresult.CombinedReactionResult;
import com.petrolpark.destroy.chemistry.reactionresult.PrecipitateReactionResult;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.fluid.MixtureFluid;
import com.petrolpark.destroy.util.vat.IVatHeaterBlock;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class ReactionInBasinRecipe extends MixingRecipe {

    private static final int BASIN_MAX_OUTPUT = 1000;

    public ReactionInBasinRecipe(ProcessingRecipeParams params) {
        super(params);
    };

    @Nullable
    public static ReactionInBasinRecipe create(Collection<FluidStack> availableFluids, Collection<ItemStack> availableItems, BasinBlockEntity basin) {
        ProcessingRecipeBuilder<ReactionInBasinRecipe> builder = new ProcessingRecipeBuilder<>(ReactionInBasinRecipe::new, Destroy.asResource("reaction_in_basin_"));

        List<ItemStack> availableItemsCopy = availableItems.stream().map(ItemStack::copy).filter(stack -> !stack.isEmpty()).toList();

        boolean canReact = true; // Start by assuming we will be able to React

        boolean isBasinTooFullToReact = false;
        
        Level level = basin.getLevel();
        BlockPos pos = basin.getBlockPos();
        float heatingPower = IVatHeaterBlock.getHeatingPower(level, pos.below(), Direction.UP);
        float outsideTemperature = LevelPollution.getLocalTemperature(level, pos);

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
            mixtures.put(mixture, (double)amount / 1000d);
        };

        tryReact: if (canReact) {
            // TODO modify temp according to Heat Level
            Mixture mixture = Mixture.mix(mixtures);
            ReactionInBasinResult result = mixture.reactInBasin(totalAmount, availableItemsCopy, heatingPower, outsideTemperature); // Mutably react the Mixture and change the Item Stacks

            // If equilibrium was not disturbed, don't do anything else
            if (result.ticks() == 0) {
                canReact = false;
                break tryReact;
            };

            Phases phases = mixture.separatePhases(result.amount());

            // Add the resultant Mixture to the results for this Recipe
            FluidStack outputMixtureStack = MixtureFluid.of((int)Math.round(phases.liquidVolume()), phases.liquidMixture());
            builder.output(outputMixtureStack);

            // Let the Player know if the Reaction cannot occur because the output Fluid will not fit
            if (outputMixtureStack.getAmount() > BASIN_MAX_OUTPUT) {
                isBasinTooFullToReact = true;
                canReact = false;
            };

            int duration = Mth.clamp(result.ticks(), 40, 600); // Ensure this takes at least 2 seconds and less than 30 seconds

            // Set the duration of the Recipe to the time it took to React
            builder.duration(duration);

            // Add the resultant Item Stacks to the results for this Recipe
            availableItemsCopy.stream().forEach(stack -> {
                if (stack.isEmpty()) return;
                builder.output(stack);
            });

            // Add all the given Fluid Stacks as "required ingredients"
            availableFluids.stream().map(FluidIngredient::fromFluidStack).forEach(builder::require);
            // Add all the given Item Stacks as "required ingredients"
            availableItems.stream().forEach(stack -> {
                if (stack.isEmpty()) return;
                for (int i = 0; i < stack.getCount(); i++) builder.require(Ingredient.of(stack.getItem()));
            });

            Map<ReactionResult, Integer> reactionResults = new HashMap<>();

            gatherReactionResults(result.reactionresults(), reactionResults, builder); // Gather all 

            ExtendedBasinBehaviour behaviour = basin.getBehaviour(ExtendedBasinBehaviour.TYPE);
            behaviour.setReactionResults(reactionResults); // Schedule the Reaction Results to occur once the Mixing has finished
            behaviour.evaporatedFluid = MixtureFluid.of((int)Math.round(phases.gasVolume()), phases.gasMixture());
        };

        basin.getBehaviour(ExtendedBasinBehaviour.TYPE).tooFullToReact = isBasinTooFullToReact;
        basin.sendData();

        if (!canReact) {
            return null;
        };

        return builder.build();
    };

    private static void gatherReactionResults(Map<ReactionResult, Integer> resultsOfReaction, Map<ReactionResult, Integer> resultsToEnact, ProcessingRecipeBuilder<ReactionInBasinRecipe> builder) {
        for (ReactionResult reactionresult : resultsOfReaction.keySet()) {
            if (reactionresult instanceof CombinedReactionResult combinedResult) {
                Map<ReactionResult, Integer> childMap = new HashMap<>();
                for (ReactionResult childResult : combinedResult.getChildren()) {
                    childMap.put(childResult, resultsOfReaction.get(combinedResult));
                };
                gatherReactionResults(childMap, resultsToEnact, builder);
            } else if (reactionresult instanceof PrecipitateReactionResult precipitationResult) {
                builder.output(precipitationResult.getPrecipitate());
            } else { // Don't deal with precipitations in the normal way
                resultsToEnact.put(reactionresult, resultsOfReaction.get(reactionresult));
            };
        };
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
    public static record ReactionInBasinResult(int ticks, Map<ReactionResult, Integer> reactionresults, int amount) {};
    
};
