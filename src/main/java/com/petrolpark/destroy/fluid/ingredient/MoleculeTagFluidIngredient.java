package com.petrolpark.destroy.fluid.ingredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonObject;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.MoleculeTag;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

public class MoleculeTagFluidIngredient extends MixtureFluidIngredient {

    protected MoleculeTag tag;
    protected float concentration;

    public MoleculeTagFluidIngredient() {};

    public MoleculeTagFluidIngredient(MoleculeTag tag, float concentration) {
        this.tag = tag;
        this.concentration = concentration;
    };

    @Override
    public MixtureFluidIngredient getNew() {
        return new MoleculeTagFluidIngredient();
    };

    @Override
    protected boolean testMixture(Mixture mixture) {
        return mixture.hasUsableTaggedMolecules(tag, concentration, molecule -> molecule.getCharge() != 0);
    };

    @Override
    protected String getMixtureFluidIngredientSubtype() {
        return "mixtureFluidWithTaggedMolecules";
    };

    @Override
    public void addNBT(CompoundTag fluidTag) {
        fluidTag.putString("MoleculeTag", tag.getId());
        fluidTag.putFloat("RequiredConcentration", concentration);
    };

    @Override
    public List<Component> getDescription(CompoundTag fluidTag) {
        String tagId = fluidTag.getString("MoleculeTag");
        if (tagId == null || tagId.isEmpty()) return List.of();
        MoleculeTag tag = MoleculeTag.MOLECULE_TAGS.get(tagId);
        if (tag == null) return List.of();
        float concentration = fluidTag.getFloat("RequiredConcentration");

        List<Component> tooltip = new ArrayList<>();
        tooltip.addAll(TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.mixture_ingredient.molecule_tag_1").string(), Palette.GRAY_AND_WHITE));
        tooltip.add(tag.getFormattedName());
        tooltip.addAll(TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.mixture_ingredient.molecule_tag_2", concentration).string(), Palette.GRAY_AND_WHITE));

        return tooltip;
    };

    @Override
    public Collection<Molecule> getContainedMolecules(CompoundTag fluidTag) {
        return MoleculeTag.MOLECULES_WITH_TAGS.get(MoleculeTag.MOLECULE_TAGS.get(fluidTag.getString("MoleculeTag")));
    };

    @Override
    protected void readInternal(FriendlyByteBuf buffer) {
        tag = MoleculeTag.MOLECULE_TAGS.get(buffer.readUtf());
        concentration = buffer.readFloat();
    };

    @Override
    protected void writeInternal(FriendlyByteBuf buffer) {
        buffer.writeUtf(tag.getId());
        buffer.writeFloat(concentration);
    };

    @Override
    protected void readInternal(JsonObject json) {
        IllegalStateException e = new IllegalStateException("Molecule Tag fluid ingredients must declare a valid tag and concentration");
        if (!json.has("tag") || !json.has("concentration")) throw e;
        String tagId = GsonHelper.getAsString(json, "tag");
        tag = MoleculeTag.MOLECULE_TAGS.get(tagId);
        concentration = GsonHelper.getAsFloat(json, "concentration");
        if (tag == null) throw e;
    };

    @Override
    protected void writeInternal(JsonObject json) {
        json.addProperty("tag", tag.getId());
        json.addProperty("concentration", concentration);
    };
    
};
