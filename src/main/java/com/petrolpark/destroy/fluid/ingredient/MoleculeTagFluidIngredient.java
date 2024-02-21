package com.petrolpark.destroy.fluid.ingredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonObject;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.MoleculeTag;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.fluid.ingredient.mixturesubtype.MixtureFluidIngredientSubType;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

public class MoleculeTagFluidIngredient extends ConcentrationRangeFluidIngredient<MoleculeTagFluidIngredient> {

    public static final Type TYPE = new Type();

    public MoleculeTag tag;

    public MoleculeTagFluidIngredient(MoleculeTag tag, float minConc, float maxConc) {
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
    protected boolean testMixture(Mixture mixture) {
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
        tag = MoleculeTag.MOLECULE_TAGS.get(buffer.readUtf());
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
        tag = MoleculeTag.MOLECULE_TAGS.get(tagId);
        if (tag == null) throw e;
    };

    @Override
    protected void writeInternal(JsonObject json) {
        super.writeInternal(json);
        json.addProperty("tag", tag.getId());
    };

    @Override
    public List<ReadOnlyMixture> getExampleMixtures() {
        return MoleculeTag.MOLECULES_WITH_TAGS.get(tag).stream().map(this::getMixtureOfRightConcentration).toList();
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
            MoleculeTag tag = MoleculeTag.MOLECULE_TAGS.get(tagId);
            if (tag == null) return List.of();
            float minConc = fluidTag.getFloat("MinimumConcentration");
            float maxConc = fluidTag.getFloat("MaximumConcentration");
    
            List<Component> tooltip = new ArrayList<>();
            tooltip.addAll(TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.mixture_ingredient.molecule_tag_1").string(), Palette.GRAY_AND_WHITE));
            tooltip.add(tag.getFormattedName());
            tooltip.addAll(TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.mixture_ingredient.molecule_tag_2", df.format(minConc), df.format(maxConc)).string(), Palette.GRAY_AND_WHITE));
    
            return tooltip;
        };
    
        @Override
        public Collection<Molecule> getContainedMolecules(CompoundTag fluidTag) {
            return MoleculeTag.MOLECULES_WITH_TAGS.get(MoleculeTag.MOLECULE_TAGS.get(fluidTag.getString("MoleculeTag")));
        };

    };
    
};
