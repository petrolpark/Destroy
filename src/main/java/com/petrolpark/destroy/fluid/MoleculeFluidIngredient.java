package com.petrolpark.destroy.fluid;

import java.util.List;

import com.google.gson.JsonObject;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;

public class MoleculeFluidIngredient extends FluidIngredient {

    protected Molecule molecule;
    protected float concentration;

    @Override
    protected boolean testInternal(FluidStack fluidStack) {
        if (fluidStack.getFluid().getFluidType() == DestroyFluids.MIXTURE.getType()) return false; // If it's not a Mixture
        CompoundTag mixtureTag = fluidStack.getChildTag("Mixture");
        if (mixtureTag.isEmpty()) return false; // If this Mixture Fluid has no associated Mixture
        return Mixture.readNBT(mixtureTag).hasUsableMolecule(molecule, concentration);
    };

    @Override
    protected void readInternal(FriendlyByteBuf buffer) {
        molecule = Molecule.getMolecule(buffer.readUtf());
        concentration = buffer.readFloat();
    };

    @Override
    protected void writeInternal(FriendlyByteBuf buffer) {
       buffer.writeUtf(molecule.getFullID());
       buffer.writeFloat(concentration);
    };

    @Override
    protected void readInternal(JsonObject json) {
        molecule = Molecule.getMolecule(GsonHelper.getAsString(json, "molecule"));
        if (json.has("concentration")) {
            concentration = GsonHelper.getAsFloat(json, "concentration");
        } else {
            concentration = 1f; // Default to 1 mol/B
        };
    };

    @Override
    protected void writeInternal(JsonObject json) {
        json.addProperty("molecule", molecule.getFullID());
        json.addProperty("concentration", concentration);
    };

    // As far as I can tell this is only used for displaying (e.g. in JEI) so a comprehensive list isn't required.
    @Override
    protected List<FluidStack> determineMatchingFluidStacks() {
        FluidStack fluidStack = new FluidStack(DestroyFluids.MIXTURE.get(), amountRequired);
        CompoundTag fluidTag = fluidStack.getOrCreateTag();
        // To avoid having to regenerate the Mixture every tick, generate all the information we need here, stick it in the NBT and just read it off when needed.
        fluidTag.putString("DisplayName", ReadOnlyMixture.readNBT(fluidTag.getCompound("Mixture")).getName().getString());
        fluidTag.putString("IngredientMolecule", molecule.getFullID());
        fluidTag.putFloat("IngredientConcentration", concentration);
        return List.of(fluidStack);
    };

};
