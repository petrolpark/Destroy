package com.petrolpark.destroy.fluid.ingredient;

import java.util.Collection;
import java.util.List;

import com.google.gson.JsonObject;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.util.DestroyLang;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

public class MoleculeFluidIngredient extends MixtureFluidIngredient {

    protected Molecule molecule;
    protected float concentration;

    @Override
    public MixtureFluidIngredient getNew() {
        return new MoleculeFluidIngredient();
    };

    @Override
    protected String getMixtureFluidIngredientSubtype() {
        return "mixtureFluidWithMolecule";
    };

    @Override
    public Collection<Molecule> getRequiredMolecules() {
        return List.of(molecule);
    };

    @Override
    public void addNBT(CompoundTag fluidTag) {
        fluidTag.putString("MoleculeRequired", molecule.getFullID());
        fluidTag.putFloat("ConcentrationRequired", concentration);
    };

    @Override
    public String getDescription(CompoundTag fluidTag) {
        String moleculeID = fluidTag.getString("MoleculeRequired");
        float concentration = fluidTag.getFloat("ConcentrationRequired");

        Molecule molecule = Molecule.getMolecule(moleculeID);
        Component moleculeName = molecule == null ? DestroyLang.translate("tooltip.unknown_molecule").component() : molecule.getName(DestroyAllConfigs.CLIENT.chemistry.iupacNames.get());

        return DestroyLang.translate("tooltip.mixture_ingredient.molecule", moleculeName, concentration).string();
    };

    @Override
    protected boolean testMixture(Mixture mixture) {
        return mixture.hasUsableMolecule(molecule, concentration, null);
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

};
