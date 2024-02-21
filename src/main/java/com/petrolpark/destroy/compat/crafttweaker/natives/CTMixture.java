package com.petrolpark.destroy.compat.crafttweaker.natives;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.CTFluidIngredient;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.util.random.Percentaged;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.MoleculeTag;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.fluid.ingredient.*;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.openzen.zencode.java.ZenCodeType;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Predicate;

@ZenRegister
@Document("mods/destroy/Mixture")
@NativeTypeRegistration(value = Mixture.class, zenCodeName = "mods.destroy.Mixture")
public class CTMixture {
    /**
     *
     * @param data data of molecules and concentration
     * @param temperature In kelvins, 0 for unspecified
     * @return
     */
    @ZenCodeType.StaticExpansionMethod
    public static Mixture create(List<Percentaged<Molecule>> data, @ZenCodeType.OptionalFloat(0) float temperature, @ZenCodeType.OptionalString String translationKey) {
        Mixture mixture = new Mixture();
        for(Percentaged<Molecule> moleculePercentage : data) {
            mixture.addMolecule(moleculePercentage.getData(), (float) moleculePercentage.getPercentage());
        }
        mixture.setTemperature(ReadOnlyMixture.BASE_TEMPERATURE);
        if(!"".equals(translationKey)) {
            mixture.setTranslationKey(translationKey);
        }
        return mixture;
    }

    @ZenCodeType.StaticExpansionMethod
    public static IFluidStack createMixtureStack(Mixture mixture, @ZenCodeType.OptionalInt(1000) int amount) {
        FluidStack fluidStack = new FluidStack(DestroyFluids.MIXTURE.get(), amount);
        fluidStack.getOrCreateTag().put("Mixture", mixture.writeNBT());
        return IFluidStack.of(fluidStack);
    }

    @ZenCodeType.StaticExpansionMethod
    public static CTFluidIngredient createMoleculeFluidIngredient(Molecule molecule, float concentration, @ZenCodeType.OptionalInt(1000) int amount) {
        MoleculeFluidIngredient result = new MoleculeFluidIngredient();
        result.molecule = molecule;
        result.setConcentrations(concentration);
        FluidStack fluidStack = new FluidStack(DestroyFluids.MIXTURE.get(), amount);
        result.addNBT(fluidStack.getOrCreateTag());
        return new CTFluidIngredient.FluidStackIngredient(IFluidStack.of(fluidStack));
    }

    @ZenCodeType.StaticExpansionMethod
    public static CTFluidIngredient createSaltFluidIngredient(Molecule cation, Molecule anion, float concentration, @ZenCodeType.OptionalInt(1000) int amount) {
        SaltFluidIngredient result = new SaltFluidIngredient();
        result.cation = cation;
        result.anion = anion;
        result.setConcentrations(concentration);
        FluidStack fluidStack = new FluidStack(DestroyFluids.MIXTURE.get(), amount);
        result.addNBT(fluidStack.getOrCreateTag());
        return new CTFluidIngredient.FluidStackIngredient(IFluidStack.of(fluidStack));
    }

    @ZenCodeType.StaticExpansionMethod
    public static CTFluidIngredient createMoleculeTagFluidIngredient(MoleculeTag tag, float concentration, @ZenCodeType.OptionalInt(1000) int amount) {
        MoleculeTagFluidIngredient result = new MoleculeTagFluidIngredient();
        result.tag = tag;
        result.setConcentrations(concentration);
        FluidStack fluidStack = new FluidStack(DestroyFluids.MIXTURE.get(), amount);
        result.addNBT(fluidStack.getOrCreateTag());
        return new CTFluidIngredient.FluidStackIngredient(IFluidStack.of(fluidStack));
    }

    @ZenCodeType.StaticExpansionMethod
    public static CTFluidIngredient createIonFluidIngredient(float concentration, @ZenCodeType.OptionalInt(1000) int amount) {
        IonFluidIngredient result = new IonFluidIngredient();
        result.setConcentrations(concentration);
        FluidStack fluidStack = new FluidStack(DestroyFluids.MIXTURE.get(), amount);
        result.addNBT(fluidStack.getOrCreateTag());
        return new CTFluidIngredient.FluidStackIngredient(IFluidStack.of(fluidStack));
    }

//    private static Mixture internal, ;

    @ZenCodeType.StaticExpansionMethod
    public static Mixture pure(Molecule molecule) {
        return Mixture.pure(molecule);
    }

    @ZenCodeType.StaticExpansionMethod
    public static Mixture mix(Map<Mixture, Double> mixtures) {
        return Mixture.mix(mixtures);
    }

    @ZenCodeType.Method
    @ZenCodeType.Setter("temperature")
    public static Mixture setTemperature(Mixture internal, float temperature) {
        return internal.setTemperature(temperature);
    }

    @ZenCodeType.Method
    public static Mixture addMolecule(Mixture internal, Molecule molecule, float concentration) {
        return internal.addMolecule(molecule, concentration);
    }

    @ZenCodeType.Method
    public static List<Molecule> getContents(Mixture internal, boolean excludeNovel) {
        return internal.getContents(excludeNovel);
    }

    @ZenCodeType.Method
    public static float getConcentrationOf(Mixture internal, Molecule molecule) {
        return internal.getConcentrationOf(molecule);
    }

    @ZenCodeType.Method
    public static boolean isAtEquilibrium(Mixture internal) {
        return internal.isAtEquilibrium();
    }

    @ZenCodeType.Method
    public static void disturbEquilibrium(Mixture internal) {
        internal.disturbEquilibrium();
    }

    @ZenCodeType.Method
    public static void heat(Mixture internal, float energyDensity) {
        internal.heat(energyDensity);
    }

    @ZenCodeType.Method
    public static void scale(Mixture internal, float volumeIncreaseFactor) {
        internal.scale(volumeIncreaseFactor);
    }

    @ZenCodeType.Method
    public static float getVolumetricHeatCapacity(Mixture internal) {
        return internal.getVolumetricHeatCapacity();
    }

    @ZenCodeType.Method
    public static int getColor(Mixture internal) {
        return internal.getColor();
    }

    @ZenCodeType.Method
    public static void setTranslationKey(Mixture internal, String translationKey) {
        internal.setTranslationKey(translationKey);
    }

    @ZenCodeType.Method
    public static float getTemperature(Mixture internal) {
        return internal.getTemperature();
    }

    @ZenCodeType.Method
    public static boolean isEmpty(Mixture internal) {
        return internal.isEmpty();
    }

    @ZenCodeType.Method
    public static float getTotalConcentration(Mixture internal) {
        return internal.getTotalConcentration();
    }

    @ZenCodeType.Method
    public static boolean hasUsableMolecule(Mixture internal, Molecule molecule, float minConcentration, float maxConcentration, @Nullable Predicate<Molecule> ignore) {
        return internal.hasUsableMolecule(molecule, minConcentration, maxConcentration, ignore);
    }

    @ZenCodeType.Method
    public static boolean hasUsableMolecules(Mixture internal, Predicate<Molecule> molecules, float minConcentration, float maxConcentration, @Nullable Predicate<Molecule> ignore) {
        return internal.hasUsableMolecules(molecules, minConcentration, maxConcentration, ignore);
    }

    @ZenCodeType.Method
    public static String getContentsString(Mixture internal) {
        return internal.getContentsString();
    }

    @ZenCodeType.Method
    public static List<Component> getContentsTooltip(Mixture internal, boolean iupac, boolean monospace, boolean useMoles, int amount, DecimalFormat concentrationFormatter) {
        return internal.getContentsTooltip(iupac, monospace, useMoles, amount, concentrationFormatter);
    }
}
