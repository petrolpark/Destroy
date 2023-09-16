package com.petrolpark.destroy.fluid.ingredient;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.fluid.MixtureFluid;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;

public abstract class MixtureFluidIngredient extends FluidIngredient {

    public static Map<String,MixtureFluidIngredient> MIXTURE_FLUID_INGREDIENT_SUBTYPES = new HashMap<>();

    static {
        registerMixtureFluidIngredientSubtype(new MoleculeFluidIngredient());
        registerMixtureFluidIngredientSubtype(new SaltFluidIngredient());
        registerMixtureFluidIngredientSubtype(new MoleculeTagFluidIngredient());
        registerMixtureFluidIngredientSubtype(new RefrigerantDummyFluidIngredient());
    };

    public static void registerMixtureFluidIngredientSubtype(MixtureFluidIngredient mixtureFluidIngredient) {
        MIXTURE_FLUID_INGREDIENT_SUBTYPES.put(mixtureFluidIngredient.getMixtureFluidIngredientSubtype(), mixtureFluidIngredient);
    };

    @Override
    protected boolean testInternal(FluidStack fluidStack) {
        if (!(fluidStack.getFluid().getFluidType() == DestroyFluids.MIXTURE.getType())) return false; // If it's not a Mixture
        CompoundTag mixtureTag = fluidStack.getChildTag("Mixture");
        if (mixtureTag.isEmpty()) return false; // If this Mixture Fluid has no associated Mixture
        return testMixture(Mixture.readNBT(mixtureTag));
    };

    @Override
    protected List<FluidStack> determineMatchingFluidStacks() {
        Mixture mixture = new Mixture();
        mixture.addMolecule(DestroyMolecules.WATER, 55.56f); //TODO get generated Mixture
        FluidStack stack = MixtureFluid.of(amountRequired, mixture);
        stack.getOrCreateTag().putString("MixtureFluidIngredientSubtype", getMixtureFluidIngredientSubtype());
        addNBT(stack.getOrCreateTag());
        return List.of(stack);
    };

    public abstract MixtureFluidIngredient getNew();

    protected abstract boolean testMixture(Mixture mixture);

    protected abstract String getMixtureFluidIngredientSubtype();

    /**
     * Add data to the NBT of the Fluid Ingredient when it is displayed in JEI. The only use of this is to control the
     * {@link MixtureFluidIngredient#getDescription description}. Careful not to overwite the tags {@code Mixture} or 
     * {@code MixtureFluidIngredientSubtype}.
     * @param fluidTag
     */
    public abstract void addNBT(CompoundTag fluidTag);

    /**
     * Generate a tooltip to act as the description of an ingredient in JEI. This method should be effectively static.
     * Do not call any local variables; all data should come from {@code fluidTag}. You should add this data {@link
     * MixtureFluidIngredient#addNBT here}.
     * @param fluidTag
     */
    public abstract List<Component> getDescription(CompoundTag fluidTag);

    /**
     * Get the Molecules which this ingredient could contain, so if this Ingredient is clicked in JEI, it knows
     * which Molecules to look up. This method should be effectively static. Do not call any local variables; all
     * data should come from {@code fluidTag}. You should add this data {@link MixtureFluidIngredient#addNBT here}.
     * @param fluidTag
     */
    public abstract Collection<Molecule> getContainedMolecules(CompoundTag fluidTag);
    
};
