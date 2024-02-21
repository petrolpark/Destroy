package com.petrolpark.destroy.fluid.ingredient;

import java.text.DecimalFormat;

import com.google.gson.JsonObject;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public abstract class ConcentrationRangeFluidIngredient<T extends MixtureFluidIngredient<T>> extends MixtureFluidIngredient<T> {

    protected static final DecimalFormat df = new DecimalFormat();
    static {
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(1);
    };

    public float minConcentration;
    public float maxConcentration;

    @Override
    public void addNBT(CompoundTag fluidTag) {
        fluidTag.putFloat("MinimumConcentration", minConcentration);
        fluidTag.putFloat("MaximumConcentration", maxConcentration);
    };

    @Override
    protected void readInternal(FriendlyByteBuf buffer) {
        minConcentration = buffer.readFloat();
        maxConcentration = buffer.readFloat();
    };

    @Override
    protected void writeInternal(FriendlyByteBuf buffer) {
        buffer.writeFloat(minConcentration);
        buffer.writeFloat(maxConcentration);
    };

    @Override
    protected void readInternal(JsonObject json) {
        if (json.has("concentration")) {
            setConcentrations(GsonHelper.getAsFloat(json, "concentration"));
        } else if (json.has("min_concentration") && json.has("max_concentration")) {
            minConcentration = GsonHelper.getAsFloat(json, "min_concentration");
            maxConcentration = GsonHelper.getAsFloat(json, "max_concentration");
        } else {
            throw new IllegalStateException("Mixture ingredients must define a concentration or range of concentrations");
        };
    };

    public void setConcentrations(float concentration) {
        minConcentration = Math.max(0f, concentration - ReadOnlyMixture.IMPURITY_THRESHOLD);
        maxConcentration = concentration + ReadOnlyMixture.IMPURITY_THRESHOLD;
    }

    @Override
    protected void writeInternal(JsonObject json) {
        json.addProperty("min_concentration", minConcentration);
        json.addProperty("max_concentration", maxConcentration);
    };

    public float getTargetConcentration() {
        return (minConcentration + maxConcentration) / 2f;
    };

    /**
     * Get a Mixture containing only the given Molecule with the average acceptable concentration,
     * and the rest water.
     * @param molecule
     */
    public ReadOnlyMixture getMixtureOfRightConcentration(Molecule molecule) {
        float targetConcentration = getTargetConcentration();
        ReadOnlyMixture mixture = new ReadOnlyMixture();
        mixture.addMolecule(molecule, targetConcentration);
        mixture.addMolecule(DestroyMolecules.WATER, DestroyMolecules.WATER.getPureConcentration() * (1f - (targetConcentration / molecule.getPureConcentration())));
        return mixture;
    };
    
};
