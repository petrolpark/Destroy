package com.petrolpark.destroy.fluid.ingredient.mixturesubtype;

import java.util.Collection;
import java.util.List;

import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.fluid.ingredient.MixtureFluidIngredient;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public abstract class MixtureFluidIngredientSubType<T extends MixtureFluidIngredient<T>> {

    public abstract T getNew();

    /**
     * Gets a MixtureFluidIngredient from FluidStack NBT
     * @param tag Tag to parse ingredient from
     * @return Destroy's native FluidIngredient
     */
    public abstract T fromNBT(CompoundTag tag, int amount);

    /**
     * Get the string which is used to identify this ingredient type in the JSON recipe deserializer.
     * @return
     */
    public abstract String getMixtureFluidIngredientSubtype();

    /**
     * Generate a tooltip to act as the description of an ingredient in JEI. All data should come from {@code
     * fluidTag}. You should add this data {@link MixtureFluidIngredient#addNBT here}.
     * @param fluidTag
     */
    public abstract List<Component> getDescription(CompoundTag fluidTag);

    /**
     * Get the Molecules which this ingredient could contain, so if this Ingredient is clicked in JEI, it knows
     * which Molecules to look up. All data should come from {@code fluidTag}. You should add this data {@link
     * MixtureFluidIngredient#addNBT here}.
     * @param fluidTag
     */
    public abstract Collection<Molecule> getContainedMolecules(CompoundTag fluidTag);
};
