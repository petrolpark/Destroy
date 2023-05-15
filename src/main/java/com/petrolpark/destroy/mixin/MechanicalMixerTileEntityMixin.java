package com.petrolpark.destroy.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.recipe.ReactionInBasinRecipe;
import com.simibubi.create.content.contraptions.components.mixer.MechanicalMixerTileEntity;
import com.simibubi.create.content.contraptions.processing.BasinTileEntity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

@Mixin(MechanicalMixerTileEntity.class)
public class MechanicalMixerTileEntityMixin {
    
    /**
     * Injection into {@link com.simibubi.create.content.contraptions.components.mixer.MechanicalMixerTileEntity Mechanical Mixers}
     * to allow them to recognise Mixtures that are able to React. A lot is copied from the {@link }
     * @see com.petrolpark.destroy.recipe.ReactionInBasinRecipe Reactions in Basins
     */
    @Inject(method = "getMatchingRecipes", at = @At(value = "HEAD"), cancellable = true)
    @SuppressWarnings("null")
    public void inGetMatchingRecipes(CallbackInfoReturnable<List<Recipe<?>>> ci) {

        Destroy.LOGGER.info("Hello im in the market for doing a recipe please");

        ((BasinOperatingTileEntityAccessor)this).invokeGetBasin().ifPresent(basin -> {

            Destroy.LOGGER.info("well i sure do have a basin");

            if (!basin.hasLevel()) return;

            Destroy.LOGGER.info("well i sure do have a level");

            IFluidHandler fluidHandler = basin.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
            IItemHandler itemHandler = basin.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
            if (fluidHandler == null || itemHandler == null) return;

            Destroy.LOGGER.info("well i sure do have fluid and item capabilities");

            boolean containsOnlyMixtures = true;
            List<ItemStack> availableItemStacks = new ArrayList<>();
            List<FluidStack> availableFluidStacks = new ArrayList<>(); // All (Mixture) Fluid Stacks in this Basin

            for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
                FluidStack fluidStack = fluidHandler.getFluidInTank(tank);
                if (DestroyFluids.MIXTURE.get().isSame(fluidStack.getFluid())) {
                    availableFluidStacks.add(fluidStack);
                } else if (!fluidStack.isEmpty()) {
                    containsOnlyMixtures = false;
                };
            };

            if (!containsOnlyMixtures) return; // If there are Fluids other than Mixtures, don't bother Reacting
            if (availableFluidStacks.size() <= 0) return; // If there are no Mixtures, don't bother Reacting

            Destroy.LOGGER.info("well i sure do have only mixtures");
                
            for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                availableItemStacks.add(itemHandler.getStackInSlot(slot));
            };
              
            // Only return this if there is definitely a Reaction possible
            Destroy.LOGGER.info("I will react a mixture");
            ReactionInBasinRecipe recipe = ReactionInBasinRecipe.create(availableFluidStacks, availableItemStacks, BasinTileEntity.getHeatLevelOf(basin.getLevel().getBlockState(basin.getBlockPos().below())));
            if (!(recipe == null)) ci.setReturnValue(List.of(recipe)); // It thinks basin.getLevel() might be null
        });
    };
    
};
