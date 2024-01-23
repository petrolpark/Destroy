package com.petrolpark.destroy.fluid.ingredient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.fluid.MixtureFluid;
import com.petrolpark.destroy.fluid.ingredient.mixturesubtype.MixtureFluidIngredientSubType;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;

public abstract class MixtureFluidIngredient<T extends MixtureFluidIngredient<T>> extends FluidIngredient {

    public static Map<String, MixtureFluidIngredientSubType<?>> MIXTURE_FLUID_INGREDIENT_SUBTYPES = new HashMap<>();

    static {
        registerMixtureFluidIngredientSubType(MoleculeFluidIngredient.TYPE);
        registerMixtureFluidIngredientSubType(SaltFluidIngredient.TYPE);
        registerMixtureFluidIngredientSubType(MoleculeTagFluidIngredient.TYPE);
        registerMixtureFluidIngredientSubType(RefrigerantDummyFluidIngredient.TYPE);
        registerMixtureFluidIngredientSubType(IonFluidIngredient.TYPE);
    };

    public static void registerMixtureFluidIngredientSubType(MixtureFluidIngredientSubType<?> ingredientType) {
        MIXTURE_FLUID_INGREDIENT_SUBTYPES.put(ingredientType.getMixtureFluidIngredientSubtype(), ingredientType);
    };

    @Override
    protected boolean testInternal(FluidStack fluidStack) {
        if (!(fluidStack.getFluid().getFluidType() == DestroyFluids.MIXTURE.getType())) return false; // If it's not a Mixture
        CompoundTag mixtureTag = fluidStack.getChildTag("Mixture");
        if (mixtureTag.isEmpty()) return false; // If this Mixture Fluid has no associated Mixture
        return testMixture(Mixture.readNBT(mixtureTag));
    };

    @Override
    protected final List<FluidStack> determineMatchingFluidStacks() {
        return getExampleMixtures().stream().map(mixture -> {
            FluidStack stack = MixtureFluid.of(amountRequired, mixture);
            stack.getOrCreateTag().putString("MixtureFluidIngredientSubtype", getType().getMixtureFluidIngredientSubtype());
            addNBT(stack.getOrCreateTag());
            return stack;
        }).toList();
    };

    public abstract MixtureFluidIngredientSubType<T> getType();

    protected abstract boolean testMixture(Mixture mixture);

    /**
     * Add data to the NBT of the Fluid Ingredient when it is displayed in JEI. The only use of this is to control the
     * {@link MixtureFluidIngredientSubType#getDescription description}. Careful not to overwite the tags {@code Mixture} or 
     * {@code MixtureFluidIngredientSubtype}.
     * @param fluidTag
     */
    public abstract void addNBT(CompoundTag fluidTag);

    /**
     * Get the Mixtures this ingredient should cycle through when shown in JEI.
     * @return
     */
    public List<ReadOnlyMixture> getExampleMixtures() {
        return List.of();
    };
    
};
