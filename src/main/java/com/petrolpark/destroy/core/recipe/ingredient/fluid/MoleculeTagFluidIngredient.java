package com.petrolpark.destroy.core.recipe.ingredient.fluid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonObject;
import com.petrolpark.destroy.chemistry.legacy.LegacyMixture;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.chemistry.legacy.LegacySpeciesTag;
import com.petrolpark.destroy.chemistry.legacy.ReadOnlyMixture;
import com.petrolpark.destroy.client.DestroyLang;
import com.simibubi.create.foundation.item.TooltipHelper;
import net.createmod.catnip.lang.FontHelper.Palette;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

public class MoleculeTagFluidIngredient extends ConcentrationRangeFluidIngredient<MoleculeTagFluidIngredient> {

    public static final Type TYPE = new Type();

    protected LegacySpeciesTag tag;

    public MoleculeTagFluidIngredient(LegacySpeciesTag tag, float minConc, float maxConc) {
        this.tag = tag;
        minConcentration = minConc;
        maxConcentration = maxConc;
    };

    public MoleculeTagFluidIngredient() {};

    @Override
    public MixtureFluidIngredientSubType<MoleculeTagFluidIngredient> getType() {
       return TYPE;
    };

    @Override
    protected boolean testMixture(LegacyMixture mixture) {
        return mixture.hasUsableMolecules(m -> m.hasTag(tag), minConcentration, maxConcentration, (m) -> false);
    };

    @Override
    public void addNBT(CompoundTag fluidTag) {
        super.addNBT(fluidTag);
        fluidTag.putString("MoleculeTag", tag.getId());
    };

    @Override
    protected void readInternal(FriendlyByteBuf buffer) {
        super.readInternal(buffer);
        tag = LegacySpeciesTag.MOLECULE_TAGS.get(buffer.readUtf());
    };

    @Override
    protected void writeInternal(FriendlyByteBuf buffer) {
        super.writeInternal(buffer);
        buffer.writeUtf(tag.getId());
    };

    @Override
    protected void readInternal(JsonObject json) {
        super.readInternal(json);
        IllegalStateException e = new IllegalStateException("Molecule Tag fluid ingredients must declare a valid tag");
        if (!json.has("tag")) throw e;
        String tagId = GsonHelper.getAsString(json, "tag");
        tag = LegacySpeciesTag.MOLECULE_TAGS.get(tagId);
        if (tag == null) throw e;
    };

    @Override
    protected void writeInternal(JsonObject json) {
        super.writeInternal(json);
        json.addProperty("tag", tag.getId());
    };

    @Override
    public List<ReadOnlyMixture> getExampleMixtures() {
        return LegacySpeciesTag.MOLECULES_WITH_TAGS.get(tag).stream().map(this::getMixtureOfRightConcentration).toList();
    };

    protected static class Type extends MixtureFluidIngredientSubType<MoleculeTagFluidIngredient> {

        @Override
        public MoleculeTagFluidIngredient getNew() {
            return new MoleculeTagFluidIngredient();
        };

        @Override
        public String getMixtureFluidIngredientSubtype() {
            return "mixtureFluidWithTaggedMolecules";
        };

        @Override
        public List<Component> getDescription(CompoundTag fluidTag) {
            String tagId = fluidTag.getString("MoleculeTag");
            if (tagId == null || tagId.isEmpty()) return List.of();
            LegacySpeciesTag tag = LegacySpeciesTag.MOLECULE_TAGS.get(tagId);
            if (tag == null) return List.of();
            float minConc = fluidTag.getFloat("MinimumConcentration");
            float maxConc = fluidTag.getFloat("MaximumConcentration");
    
            List<Component> tooltip = new ArrayList<>();
            tooltip.addAll(TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.mixture_ingredient.molecule_tag_1").string(), Palette.GRAY_AND_WHITE));
            tooltip.add(tag.getFormattedName());
            tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.mixture_ingredient.molecule_tag_2", df.format(minConc), df.format(maxConc)).component(), Palette.GRAY_AND_WHITE.primary(), Palette.GRAY_AND_WHITE.highlight()));
    
            return tooltip;
        };
    
        @Override
        public Collection<LegacySpecies> getContainedMolecules(CompoundTag fluidTag) {
            return LegacySpeciesTag.MOLECULES_WITH_TAGS.get(LegacySpeciesTag.MOLECULE_TAGS.get(fluidTag.getString("MoleculeTag")));
        };

    };
    
};
