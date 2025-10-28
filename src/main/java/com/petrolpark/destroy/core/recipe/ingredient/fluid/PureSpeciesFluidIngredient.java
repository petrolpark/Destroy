package com.petrolpark.destroy.core.recipe.ingredient.fluid;

import java.util.Collection;
import java.util.Collections;
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

public class PureSpeciesFluidIngredient extends MixtureFluidIngredient<PureSpeciesFluidIngredient> {

    public static final Type TYPE = new Type();

    public LegacySpecies species;

    @Override
    public MixtureFluidIngredientSubType<PureSpeciesFluidIngredient> getType() {
        return TYPE;
    };

    @Override
    protected boolean testMixture(LegacyMixture mixture) {
        return mixture.getContents(false).stream().allMatch(s -> s.equals(species) || mixture.getConcentrationOf(s) == 0f);
    };

    @Override
    public void addNBT(CompoundTag fluidTag) {
        fluidTag.putString("Species", species.getFullID());
    };

    @Override
    protected void readInternal(FriendlyByteBuf buffer) {
        species = LegacySpecies.getMolecule(buffer.readUtf());
    };

    @Override
    protected void writeInternal(FriendlyByteBuf buffer) {
        buffer.writeUtf(species.getFullID());
    };

    @Override
    protected void readInternal(JsonObject json) {
        species = LegacySpecies.getMolecule(GsonHelper.getAsString(json, "species"));
    };

    @Override
    protected void writeInternal(JsonObject json) {
        json.addProperty("species", species.getFullID());
    };

    @Override
    public List<ReadOnlyMixture> getExampleMixtures() {
        return Collections.singletonList(LegacyMixture.pure(species));
    };

    public static class Type extends MixtureFluidIngredientSubType<PureSpeciesFluidIngredient> {

        @Override
        public PureSpeciesFluidIngredient getNew() {
            return new PureSpeciesFluidIngredient();
        };

        @Override
        public String getMixtureFluidIngredientSubtype() {
            return "pureMixture";
        };

        @Override
        public List<Component> getDescription(CompoundTag fluidTag) {
            return TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.mixture_ingredient.pure", LegacySpecies.getMolecule(fluidTag.getString("Species")).getName(DestroyAllConfigs.CLIENT.chemistry.iupacNames.get())).component(), Palette.GRAY_AND_WHITE);
        };

        @Override
        public Collection<LegacySpecies> getContainedMolecules(CompoundTag fluidTag) {
            return Collections.singleton(LegacySpecies.getMolecule(fluidTag.getString("Species")));
        };

    };
    
};
