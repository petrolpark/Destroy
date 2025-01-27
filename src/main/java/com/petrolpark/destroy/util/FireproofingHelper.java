package com.petrolpark.destroy.util;

import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.contamination.Contaminables;
import com.petrolpark.contamination.Contaminant;
import com.petrolpark.contamination.ItemContamination;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.petrolpark.destroy.recipe.SingleFluidRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class FireproofingHelper {

    public static final ResourceLocation CONTAMINANT_RL = Destroy.asResource("fireproof");

    private static final RecipeWrapper WRAPPER = new RecipeWrapper(new ItemStackHandler());

    public static final Contaminant getFireproofContaminanant(RegistryAccess registryAccess) {
        return registryAccess.registry(PetrolparkRegistries.Keys.CONTAMINANT).orElseThrow().get(CONTAMINANT_RL);
    };

    public static boolean canApply(Level world, ItemStack stack) {
        return couldApply(world, stack) && DestroyRecipeTypes.FLAME_RETARDANT_APPLICATION.find(WRAPPER, world).isPresent();
    };

    public static boolean couldApply(Level world, ItemStack stack) {
        if (stack.getItem().isFireResistant() || isFireproof(world.registryAccess(), stack)) return false;
        return Contaminables.ITEM.isContaminableStack(stack);
    };

    public static int getRequiredAmountForItem(Level world, ItemStack stack, FluidStack availableFluid) {
        if (!canApply(world, stack)) return -1;
        return world.getRecipeManager().getRecipeFor(DestroyRecipeTypes.FLAME_RETARDANT_APPLICATION.getType(), WRAPPER, world).stream()
            .map(SingleFluidRecipe.class::cast)
            .map(SingleFluidRecipe::getRequiredFluid)
            .filter(i -> i.test(availableFluid))
            .findFirst()
            .map(FluidIngredient::getRequiredAmount)
            .orElse(-1);
    };

    public static ItemStack fillItem(Level world, int requiredAmount, ItemStack stack, FluidStack availableFluid) {
        if (!canApply(world, stack)) return ItemStack.EMPTY;
        return world.getRecipeManager().getRecipeFor(DestroyRecipeTypes.FLAME_RETARDANT_APPLICATION.getType(), WRAPPER, world)
            .map(SingleFluidRecipe.class::cast)
            .filter(r  -> r.getRequiredFluid().test(availableFluid))
            .map(r -> {
                availableFluid.shrink(100);
                ItemStack result = stack.copy();
                stack.shrink(1);
                apply(world, result);
                return result;
            })
            .orElse(ItemStack.EMPTY);
    };

    public static void apply(Level world, ItemStack stack) {
        if (getFireproofContaminanant(world.registryAccess()) != null) ItemContamination.get(stack).contaminate(getFireproofContaminanant(world.registryAccess()));
    };

    public static boolean isFireproof(RegistryAccess registryAccess, ItemStack stack) {
        if (getFireproofContaminanant(registryAccess) == null) return false;
        return ItemContamination.get(stack).has(getFireproofContaminanant(registryAccess));
    };
};
