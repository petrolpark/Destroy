package com.petrolpark.destroy.core.recipe.ingredient.fluid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.math.IntMath;
import com.google.gson.JsonObject;
import com.petrolpark.destroy.chemistry.legacy.LegacyMixture;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.chemistry.legacy.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.naming.NamedSalt;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.simibubi.create.foundation.item.TooltipHelper;
import net.createmod.catnip.lang.FontHelper.Palette;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

public class SaltFluidIngredient extends ConcentrationRangeFluidIngredient<SaltFluidIngredient> {

    public static final Type TYPE = new Type();

    protected LegacySpecies cation;
    protected LegacySpecies anion;

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
    protected boolean testMixture(LegacyMixture mixture) {
        int gcd = IntMath.gcd(cation.getCharge(), -anion.getCharge());
        return mixture.hasUsableMolecule(cation, minConcentration * -anion.getCharge() / gcd, maxConcentration * -anion.getCharge() / gcd, (molecule) -> molecule == anion) && mixture.hasUsableMolecule(anion, minConcentration * cation.getCharge() / gcd, maxConcentration * cation.getCharge() / gcd, (molecule) -> molecule == cation);
    };

    @Override
    protected void readInternal(FriendlyByteBuf buffer) {
        super.readInternal(buffer);
        cation = LegacySpecies.getMolecule(buffer.readUtf());
        anion = LegacySpecies.getMolecule(buffer.readUtf());
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
        cation = LegacySpecies.getMolecule(GsonHelper.getAsString(json, "cation"));
        if (cation.getCharge() <= 0) throw new IllegalStateException("Cations must be positively charged.");
        anion = LegacySpecies.getMolecule(GsonHelper.getAsString(json, "anion"));
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

    protected static class Type extends MixtureFluidIngredientSubType<SaltFluidIngredient> {

        @Override
        public SaltFluidIngredient getNew() {
            return new SaltFluidIngredient();
        };

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
    
            LegacySpecies cation = LegacySpecies.getMolecule(cationID);
            LegacySpecies anion = LegacySpecies.getMolecule(anionID);
            Component compoundName = (cation == null || anion == null) ? DestroyLang.translate("tooltip.unknown_molecule").component() : new NamedSalt(cation, anion).getName(iupac);
    
            return TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.mixture_ingredient.molecule", compoundName, df.format(minConc), df.format(maxConc)).string(), Palette.GRAY_AND_WHITE);
        };
    
        @Override
        public Collection<LegacySpecies> getContainedMolecules(CompoundTag fluidTag) {
            String cationID = fluidTag.getString("RequiredCation");
            String anionID = fluidTag.getString("RequiredAnion");
            LegacySpecies cation = LegacySpecies.getMolecule(cationID);
            LegacySpecies anion = LegacySpecies.getMolecule(anionID);
            List<LegacySpecies> molecules = new ArrayList<>(2);
            if (cation != null) molecules.add(cation);
            if (anion != null) molecules.add(anion);
            return molecules;
        };

    };
    
};
