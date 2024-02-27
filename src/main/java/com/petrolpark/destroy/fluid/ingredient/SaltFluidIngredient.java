package com.petrolpark.destroy.fluid.ingredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonObject;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.naming.NamedSalt;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.fluid.ingredient.mixturesubtype.MixtureFluidIngredientSubType;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

public class SaltFluidIngredient extends ConcentrationRangeFluidIngredient<SaltFluidIngredient> {

    public static final Type TYPE = new Type();

    public Molecule cation;
    public Molecule anion;

    @Override
    public MixtureFluidIngredientSubType<SaltFluidIngredient> getType() {
        return TYPE;
    };

    @Override
    public void addNBT(CompoundTag fluidTag) {
        super.addNBT(fluidTag);
        fluidTag.putString("RequiredCation", cation.getFullID());
        fluidTag.putString("RequiredAnion", anion.getFullID());
    };

    @Override
    protected boolean testMixture(Mixture mixture) {
        return mixture.hasUsableMolecule(
            cation,
            minConcentration * cation.getCharge(),
            maxConcentration * cation.getCharge(),
            (molecule) -> molecule == anion
        ) && mixture.hasUsableMolecule(
            anion,
            minConcentration * -anion.getCharge(),
            maxConcentration * -anion.getCharge(),
            (molecule) -> molecule == cation
        );
    };

    @Override
    protected void readInternal(FriendlyByteBuf buffer) {
        super.readInternal(buffer);
        cation = Molecule.getMolecule(buffer.readUtf());
        anion = Molecule.getMolecule(buffer.readUtf());
    };

    @Override
    protected void writeInternal(FriendlyByteBuf buffer) {
        super.writeInternal(buffer);
        buffer.writeUtf(cation.getFullID());
        buffer.writeUtf(anion.getFullID());
    };

    @Override
    protected void readInternal(JsonObject json) {
        super.readInternal(json);
        if (!json.has("cation") || !json.has("anion")) throw new IllegalStateException("Salt Mixture Ingredients must declare a cation and anion");
        cation = Molecule.getMolecule(GsonHelper.getAsString(json, "cation"));
        if (cation.getCharge() <= 0) throw new IllegalStateException("Cations must be positively charged.");
        anion = Molecule.getMolecule(GsonHelper.getAsString(json, "anion"));
        if (anion.getCharge() >= 0) throw new IllegalStateException("Anions must be negatively charged.");
    };

    @Override
    protected void writeInternal(JsonObject json) {
        super.writeInternal(json);
        json.addProperty("cation", cation.getFullID());
        json.addProperty("anion", anion.getFullID());
    };

    @Override
    public List<ReadOnlyMixture> getExampleMixtures() {
        ReadOnlyMixture mixture = new ReadOnlyMixture();
        float targetConcentration = getTargetConcentration();
        mixture.addMolecule(DestroyMolecules.WATER, DestroyMolecules.WATER.getPureConcentration()); // We're assuming the ions have 0 density
        mixture.addMolecule(cation, targetConcentration);
        mixture.addMolecule(anion, targetConcentration);
        return List.of(mixture);
    };

    public static class Type extends MixtureFluidIngredientSubType<SaltFluidIngredient> {

        @Override
        public SaltFluidIngredient getNew() {
            return new SaltFluidIngredient();
        };

        @Override
        public SaltFluidIngredient fromNBT(CompoundTag tag, int amount) {
            SaltFluidIngredient result = new SaltFluidIngredient();
            result.cation = Molecule.getMolecule(tag.getString("RequiredCation"));
            result.anion = Molecule.getMolecule(tag.getString("RequiredAnion"));
            result.minConcentration = tag.getFloat("MinimumConcentration");
            result.maxConcentration = tag.getFloat("MaximumConcentration");
            result.amountRequired = amount;
            return result;
        }

        @Override
        public String getMixtureFluidIngredientSubtype() {
            return "mixtureFluidWithSalt";
        };

        @Override
        public List<Component> getDescription(CompoundTag fluidTag) {
            String cationID = fluidTag.getString("RequiredCation");
            String anionID = fluidTag.getString("RequiredAnion");
            float minConc = fluidTag.getFloat("MinimumConcentration");
            float maxConc = fluidTag.getFloat("MaximumConcentration");
            boolean iupac = DestroyAllConfigs.CLIENT.chemistry.iupacNames.get();
    
            Molecule cation = Molecule.getMolecule(cationID);
            Molecule anion = Molecule.getMolecule(anionID);
            Component compoundName = (cation == null || anion == null) ? DestroyLang.translate("tooltip.unknown_molecule").component() : new NamedSalt(cation, anion).getName(iupac);
    
            return TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.mixture_ingredient.molecule", compoundName, df.format(minConc), df.format(maxConc)).string(), Palette.GRAY_AND_WHITE);
        };
    
        @Override
        public Collection<Molecule> getContainedMolecules(CompoundTag fluidTag) {
            String cationID = fluidTag.getString("RequiredCation");
            String anionID = fluidTag.getString("RequiredAnion");
            Molecule cation = Molecule.getMolecule(cationID);
            Molecule anion = Molecule.getMolecule(anionID);
            List<Molecule> molecules = new ArrayList<>(2);
            if (cation != null) molecules.add(cation);
            if (anion != null) molecules.add(anion);
            return molecules;
        };

    };
    
};
