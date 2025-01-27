package com.petrolpark.destroy.recipe;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.entity.behaviour.ExtendedBasinBehaviour;
import com.petrolpark.destroy.capability.Pollution;
import com.petrolpark.destroy.chemistry.api.util.Constants;
import com.petrolpark.destroy.chemistry.legacy.LegacyMixture;
import com.petrolpark.destroy.chemistry.legacy.ReactionResult;
import com.petrolpark.destroy.chemistry.legacy.LegacyMixture.Phases;
import com.petrolpark.destroy.chemistry.legacy.reactionresult.CombinedReactionResult;
import com.petrolpark.destroy.chemistry.legacy.reactionresult.PrecipitateReactionResult;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.fluid.MixtureFluid;
import com.petrolpark.destroy.util.ItemHelper;
import com.petrolpark.destroy.util.vat.IVatHeaterBlock;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.RecipeFinder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class ReactionInBasinRecipe extends BasinRecipe {

    private static final Object recipeCacheKey = new Object();
    private static final int BASIN_MAX_OUTPUT = 1000;

    public ReactionInBasinRecipe(ProcessingRecipeParams params) {
        super(params);
    };

    //TODO replace with apply()
    @Nullable
    public static ReactionInBasinRecipe create(Collection<FluidStack> availableFluids, Collection<ItemStack> availableItems, BasinBlockEntity basin) {
        ProcessingRecipeBuilder<ReactionInBasinRecipe> builder = new ProcessingRecipeBuilder<>(ReactionInBasinRecipe::new, Destroy.asResource("reaction_in_basin_"));

        List<ItemStack> availableItemsCopy = availableItems.stream().map(ItemStack::copy).filter(stack -> !stack.isEmpty()).toList();

        boolean canReact = true; // Start by assuming we will be able to React
        boolean containsRawMixtures = false; // If the ONLY thing we have are non-Mixtures, even if they can be converted to Mixtures we don't want to react

        boolean isBasinTooFullToReact = false;
        
        Level level = basin.getLevel();
        BlockPos pos = basin.getBlockPos();
        float heatingPower = IVatHeaterBlock.getHeatingPower(level, pos.below(), Direction.UP);
        float outsideTemperature = Pollution.getLocalTemperature(level, pos);
        ExtendedBasinBehaviour behaviour = basin.getBehaviour(ExtendedBasinBehaviour.TYPE);
        boolean shouldUpdateBasin = false;

        Map<LegacyMixture, Double> mixtures = new HashMap<>(availableFluids.size()); // A Map of all available Mixtures to the volume of them available (in Buckets)
        int totalAmount = 0; // How much Mixture there is

        // Check all Fluids are Mixturess
        for (FluidStack fluidStack : availableFluids) {

            LegacyMixture mixture;
            if (DestroyFluids.isMixture(fluidStack)) {
                // True Mixtures
                mixture = LegacyMixture.readNBT(fluidStack.getOrCreateTag().getCompound("Mixture"));
                containsRawMixtures = true;
            } else {
                // Non-Mixture -> Mixture conversions
                MixtureConversionRecipe recipe = RecipeFinder.get(recipeCacheKey, basin.getLevel(), r -> r.getType() == DestroyRecipeTypes.MIXTURE_CONVERSION.getType())
                    .stream()
                    .map(r -> (MixtureConversionRecipe)r)
                    .filter(r -> r.getFluidIngredients().get(0).test(fluidStack))
                    .findFirst()
                    .orElse(null);
                if (recipe == null) {
                    canReact = false;
                    break;
                } else {
                    mixture = LegacyMixture.readNBT(recipe.getFluidResults().get(0).getOrCreateTag().getCompound("Mixture"));
                };
            };

            int amount = fluidStack.getAmount();
            totalAmount += amount;
            mixtures.put(mixture, (double)amount / Constants.MILLIBUCKETS_PER_LITER);
        };

        if (!containsRawMixtures && mixtures.size() == 1) canReact = false; // Don't react without Mixtures, even if there are fluids which could be converted into Mixtures 

        tryReact: if (canReact) {
            // TODO modify temp according to Heat Level
            LegacyMixture mixture = LegacyMixture.mix(mixtures);
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

            gatherReactionResults(result.reactionResults(), reactionResults, builder); // Gather all 

            behaviour.setReactionResults(reactionResults); // Schedule the Reaction Results to occur once the Mixing has finished
            behaviour.evaporatedFluid = MixtureFluid.of((int)Math.round(phases.gasVolume()), phases.gasMixture());
            shouldUpdateBasin = true;
        };

        if (behaviour.tooFullToReact != isBasinTooFullToReact) {
            behaviour.tooFullToReact = isBasinTooFullToReact;
            shouldUpdateBasin = true;
        };
        if (shouldUpdateBasin) basin.sendData();

        if (!canReact) return null;
        
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
                ItemStack precipitate = precipitationResult.getPrecipitate();
                ItemHelper.withCount(precipitate, resultsOfReaction.get(reactionresult) * precipitate.getCount()).forEach(builder::output);
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
     * The outcome of {@link com.petrolpark.destroy.chemistry.legacy.LegacyReaction reacting} a {@link com.petrolpark.destroy.chemistry.legacy.LegacyReaction Mixture} in a Basin.
     * @param ticks The number of ticks it took for the Mixture to reach equilibrium
     * @param reactionResults The {@link com.petrolpark.destroy.chemistry.legacy.ReactionResult results} of Reacting this Mixture
     * @param amount The amount (in mB) of resultant Mixture
     */
    public static record ReactionInBasinResult(int ticks, Map<ReactionResult, Integer> reactionResults, int amount) {};
    
};
