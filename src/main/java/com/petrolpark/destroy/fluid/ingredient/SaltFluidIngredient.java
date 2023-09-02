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

public class SaltFluidIngredient extends MixtureFluidIngredient {

    protected Molecule cation;
    protected Molecule anion;
    protected float concentration;

    @Override
    public MixtureFluidIngredient getNew() {
        return new SaltFluidIngredient();
    };

    @Override
    protected String getMixtureFluidIngredientSubtype() {
        return "mixtureFluidWithSalt";
    };

    @Override
    public Collection<Molecule> getRequiredMolecules() {
        return List.of(cation, anion);
    };

    @Override
    public void addNBT(CompoundTag fluidTag) {
        fluidTag.putString("RequiredCation", cation.getFullID());
        fluidTag.putString("RequiredAnion", anion.getFullID());
        fluidTag.putFloat("RequiredConcentration", concentration);
    };

    @Override
    public String getDescription(CompoundTag fluidTag) {
        String cationID = fluidTag.getString("RequiredCation");
        String anionID = fluidTag.getString("RequiredAnion");
        float concentration = fluidTag.getFloat("RequiredConcentration");
        boolean iupac = DestroyAllConfigs.CLIENT.chemistry.iupacNames.get();

        Molecule cation = Molecule.getMolecule(cationID);
        Molecule anion = Molecule.getMolecule(anionID);
        Component compoundName = (cation == null || anion == null) ? DestroyLang.translate("tooltip.unknown_molecule").component() : 
            DestroyLang.translate("mixture.simple_salt", cation.getName(iupac).getString(), anion.getName(iupac).getString()).component()
        ;

        return DestroyLang.translate("tooltip.mixture_ingredient.molecule", compoundName, concentration).string();
    };

    @Override
    protected boolean testMixture(Mixture mixture) {
        return mixture.hasUsableMolecule(cation, concentration * cation.getCharge(), anion::equals) && mixture.hasUsableMolecule(anion, concentration * -anion.getCharge(), cation::equals);
    };

    @Override
    protected void readInternal(FriendlyByteBuf buffer) {
        cation = Molecule.getMolecule(buffer.readUtf());
        anion = Molecule.getMolecule(buffer.readUtf());
        concentration = buffer.readFloat();
    };

    @Override
    protected void writeInternal(FriendlyByteBuf buffer) {
        buffer.writeUtf(cation.getFullID());
        buffer.writeUtf(anion.getFullID());
        buffer.writeFloat(concentration);
    };

    @Override
    protected void readInternal(JsonObject json) {
        if (!json.has("cation") || !json.has("anion")) throw new IllegalStateException("Salt Mixture Ingredients must declare a cation and anion");
        cation = Molecule.getMolecule(GsonHelper.getAsString(json, "cation"));
        if (cation.getCharge() <= 0) throw new IllegalStateException("Cations miust be positively charged.");
        anion = Molecule.getMolecule(GsonHelper.getAsString(json, "anion"));
        if (anion.getCharge() >= 0) throw new IllegalStateException("Anions must be negatively charged.");
        if (json.has("concentration")) {
            concentration = GsonHelper.getAsFloat(json, "concentration");
        } else {
            concentration = 1f; // Default to 1 mol/B
        };
    };

    @Override
    protected void writeInternal(JsonObject json) {
        json.addProperty("cation", cation.getFullID());
        json.addProperty("anion", anion.getFullID());
        json.addProperty("concentration", concentration);
    };
    
};
