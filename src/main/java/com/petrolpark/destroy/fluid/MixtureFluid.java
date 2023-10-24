package com.petrolpark.destroy.fluid;

import javax.annotation.Nullable;

import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.simibubi.create.AllFluids.TintedFluidType;
import com.simibubi.create.content.fluids.VirtualFluid;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;

public class MixtureFluid extends VirtualFluid {

    public MixtureFluid(Properties properties) {
        super(properties);
    };

    private static Mixture AIR_MIXTURE;

    public static Mixture airMixture() {
        if (AIR_MIXTURE == null) {
            AIR_MIXTURE = new Mixture();
            AIR_MIXTURE.addMolecule(DestroyMolecules.NITROGEN, 32.80f);
            AIR_MIXTURE.addMolecule(DestroyMolecules.OXYGEN, 8.83f);
            AIR_MIXTURE.setTemperature(289f); // 16 degrees C
        };
        return AIR_MIXTURE;
    };

    /**
     * Creates a Fluid Stack of the given {@link com.petrolpark.destroy.chemistry.Mixture Mixture}.
     * @param amount How many mB this Fluid Stack is
     * @param mixture This does not have to be read-only
     */
    public static FluidStack of(int amount, ReadOnlyMixture mixture) {
        return of(amount, mixture, null);
    };

    /**
     * Creates a Fluid Stack of the given {@link com.petrolpark.destroy.chemistry.Mixture Mixture}.
     * @param amount How many mB this Fluid Stack is
     * @param mixture This does not have to be read-only
     * @param translationKey The translation key of the custom name of this Mixture (which will override the normal naming algorithm). {@code null} or {@code ""} for no name
     */
    public static FluidStack of(int amount, ReadOnlyMixture mixture, @Nullable String translationKey) {
        if (amount == 0) return FluidStack.EMPTY;
        FluidStack fluidStack = new FluidStack(DestroyFluids.MIXTURE.getSource(), amount);
        mixture.setTranslationKey(translationKey);
        addMixtureToFluidStack(fluidStack, mixture);
        return fluidStack;
    };

    public static FluidStack addMixtureToFluidStack(FluidStack fluidStack, ReadOnlyMixture mixture) {
        if (mixture.isEmpty()) {
            fluidStack.removeChildTag("Mixture");
            return fluidStack;
        };
        fluidStack.getOrCreateTag().put("Mixture", mixture.writeNBT());
        return fluidStack;
    };

    public static class MixtureFluidType extends TintedFluidType {

        public MixtureFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            super(properties, stillTexture, flowingTexture);
        };

        @Override
        protected int getTintColor(FluidStack stack) {
            return MixtureFluid.getTintColor(stack);
        };

        @Override
        protected int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
            // As Mixture Fluids are virtual they should never exist in a Fluid State, so there is no need to have a tint
            return 0;
        };

        @Override
        public Component getDescription(FluidStack stack) {
            return ReadOnlyMixture.readNBT(stack.getChildTag("Mixture")).getName(); //TODO cache Mixture name to avoid calculating every time
        };

    };

    public static int getTintColor(FluidStack stack) {
        if (stack.isEmpty()) return 0x00FFFFFF; // Transparent
        if (!stack.getOrCreateTag().contains("Mixture", Tag.TAG_COMPOUND)) return -1;
        return ReadOnlyMixture.readNBT(stack.getChildTag("Mixture")).getColor(); //TODO cache Mixture color to avoid calculating every time
    };

    
};
