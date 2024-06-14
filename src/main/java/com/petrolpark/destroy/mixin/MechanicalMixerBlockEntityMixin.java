package com.petrolpark.destroy.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.simibubi.create.content.fluids.potion.PotionMixingRecipes;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.mixin.accessor.BasinOperatingBlockEntityAccessor;
import com.petrolpark.destroy.recipe.ReactionInBasinRecipe;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

@Mixin(MechanicalMixerBlockEntity.class)
public abstract class MechanicalMixerBlockEntityMixin extends BasinOperatingBlockEntity {

    @Shadow public boolean running;

    public MechanicalMixerBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    /**
     * Injection into {@link com.simibubi.create.content.contraptions.components.mixer.MechanicalMixerBlockEntity Mechanical Mixers}
     * to allow them to recognise Mixtures that are able to React.
     *
     * @return
     * @see ReactionInBasinRecipe Reactions in Basins
     * @author petrolpark
     * @reason Properly handle recipes which involve mixtures
     */
    @Overwrite(remap = false)
    public List<Recipe<?>> getMatchingRecipes() {
        List<Recipe<?>> matchingRecipes = super.getMatchingRecipes();
        List<Recipe<?>> basinReactions = destroy$getBasinReactionRecipes();
        List<Recipe<?>> together = new ArrayList<>(matchingRecipes.size() + basinReactions.size());
        together.addAll(matchingRecipes);
        together.addAll(basinReactions);
        if (!AllConfigs.server().recipes.allowBrewingInMixer.get())
            return together;

        Optional<BasinBlockEntity> basin = getBasin();
        if (basin.isEmpty())
            return together;

        BasinBlockEntity basinBlockEntity = basin.get();

        IItemHandler availableItems = basinBlockEntity
            .getCapability(ForgeCapabilities.ITEM_HANDLER)
            .orElse(null);
        if (availableItems == null) {
            return together;
        }

        for (int i = 0; i < availableItems.getSlots(); i++) {
            ItemStack stack = availableItems.getStackInSlot(i);
            if (stack.isEmpty())
                continue;

            List<MixingRecipe> list = PotionMixingRecipes.BY_ITEM.get(stack.getItem());
            if (list == null)
                continue;
            for (MixingRecipe mixingRecipe : list)
                if (matchBasinRecipe(mixingRecipe))
                    matchingRecipes.add(mixingRecipe);
        }

        return together;
    }

    @Unique
    public List<Recipe<?>> destroy$getBasinReactionRecipes() {

        BasinBlockEntity basin = ((BasinOperatingBlockEntityAccessor) this).invokeGetBasin().orElse(null);
        if(basin == null) return List.of();
        if (!basin.hasLevel()) return List.of();

        // ignore IDE warnings here
        IFluidHandler fluidHandler = basin.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
        IItemHandler itemHandler = basin.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        if(fluidHandler == null || itemHandler == null) return List.of();
        boolean containsOnlyMixtures = true;
        List<ItemStack> availableItemStacks = new ArrayList<>();
        List<FluidStack> availableFluidStacks = new ArrayList<>(); // All (Mixture) Fluid Stacks in this Basin

        for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
            FluidStack fluidStack = fluidHandler.getFluidInTank(tank);
            if (DestroyFluids.isMixture(fluidStack)) {
                availableFluidStacks.add(fluidStack);
            } else if (!fluidStack.isEmpty()) {
                containsOnlyMixtures = false;
            }
        }

        if (!containsOnlyMixtures) return List.of(); // If there are Fluids other than Mixtures, don't bother Reacting
        if (availableFluidStacks.isEmpty()) return List.of(); // If there are no Mixtures, don't bother Reacting

        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            availableItemStacks.add(itemHandler.getStackInSlot(slot));
        }

        // Only return this if there is definitely a Reaction possible
        ReactionInBasinRecipe recipe = ReactionInBasinRecipe.create(availableFluidStacks, availableItemStacks, basin);
        if(recipe == null) return List.of();
        if (BasinRecipe.match(basin, recipe)) {
            return List.of(recipe);
        }
        return List.of();
    }

}
