package com.petrolpark.destroy.core.recipe.ingredient.fluid;

import java.util.Collection;
import java.util.List;

import com.google.gson.JsonObject;
import com.petrolpark.destroy.chemistry.legacy.LegacyMixture;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.chemistry.legacy.ReadOnlyMixture;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.simibubi.create.foundation.item.TooltipHelper;
import net.createmod.catnip.lang.FontHelper.Palette;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

public class MoleculeFluidIngredient extends ConcentrationRangeFluidIngredient<MoleculeFluidIngredient> {

    public static final Type TYPE = new Type();

    protected LegacySpecies molecule;

    @Override
    public MixtureFluidIngredientSubType<MoleculeFluidIngredient> getType() {
        return TYPE;
    };

    @Override
    public void addNBT(CompoundTag fluidTag) {
        super.addNBT(fluidTag);
        fluidTag.putString("MoleculeRequired", molecule.getFullID());
    };

    @Override
    protected boolean testMixture(LegacyMixture mixture) {
        return mixture.hasUsableMolecule(molecule, minConcentration, maxConcentration, null);
    };

    @Override
    protected void readInternal(FriendlyByteBuf buffer) {
        super.readInternal(buffer);
        molecule = LegacySpecies.getMolecule(buffer.readUtf());
    };

    @Override
    protected void writeInternal(FriendlyByteBuf buffer) {
        super.writeInternal(buffer);
        buffer.writeUtf(molecule.getFullID());
    };

    @Override
    protected void readInternal(JsonObject json) {
        molecule = LegacySpecies.getMolecule(GsonHelper.getAsString(json, "molecule"));
        super.readInternal(json);
    };

    @Override
    protected void writeInternal(JsonObject json) {
        super.writeInternal(json);
        json.addProperty("molecule", molecule.getFullID());
    };

    @Override
    public List<ReadOnlyMixture> getExampleMixtures() {
        return List.of(getMixtureOfRightConcentration(molecule));
    };

    protected static class Type extends MixtureFluidIngredientSubType<MoleculeFluidIngredient> {

        @Override
        public MoleculeFluidIngredient getNew() {
            return new MoleculeFluidIngredient();
        };

        @Override
        public String getMixtureFluidIngredientSubtype() {
            return "mixtureFluidWithMolecule";
        };

        @Override
        public List<Component> getDescription(CompoundTag fluidTag) {
            String moleculeID = fluidTag.getString("MoleculeRequired");
            float minConc = fluidTag.getFloat("MinimumConcentration");
            float maxConc = fluidTag.getFloat("MaximumConcentration");
    
            LegacySpecies molecule = LegacySpecies.getMolecule(moleculeID);
            Component moleculeName = molecule == null ? DestroyLang.translate("tooltip.unknown_molecule").component() : molecule.getName(DestroyAllConfigs.CLIENT.chemistry.iupacNames.get());
    
            return TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.mixture_ingredient.molecule", moleculeName, df.format(minConc), df.format(maxConc)).string(), Palette.GRAY_AND_WHITE);
        };
    
        @Override
        public Collection<LegacySpecies> getContainedMolecules(CompoundTag fluidTag) {
            String moleculeID = fluidTag.getString("MoleculeRequired");
            LegacySpecies molecule = LegacySpecies.getMolecule(moleculeID);
            if (molecule == null) return List.of();
            return List.of(molecule);
        };

    };

};
