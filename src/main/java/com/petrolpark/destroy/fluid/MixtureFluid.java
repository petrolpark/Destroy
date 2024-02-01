package com.petrolpark.destroy.fluid;

import javax.annotation.Nullable;

import com.petrolpark.destroy.chemistry.ClientMixture;
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

    public static Mixture airMixture(float temperature) {
        if (temperature <= 0f || Float.isNaN(temperature)) throw new IllegalStateException("Temperature cannot be negative or 0.");
        Mixture air = new Mixture();
        air.addMolecule(DestroyMolecules.NITROGEN, 32.80f * 279f / temperature);
        air.addMolecule(DestroyMolecules.OXYGEN, 8.83f * 279f / temperature);
        air.setTemperature(temperature);
        return air;
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
            return ReadOnlyMixture.readNBT(ClientMixture::new, stack.getChildTag("Mixture")).getName();
        };

    };

    public static int getTintColor(FluidStack stack) {
        if (stack.isEmpty()) return 0x00FFFFFF; // Transparent
        if (!stack.getOrCreateTag().contains("Mixture", Tag.TAG_COMPOUND)) return -1;
        return ReadOnlyMixture.readNBT(ClientMixture::new, stack.getChildTag("Mixture")).getColor();
    };

    
};
