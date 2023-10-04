package com.petrolpark.destroy.fluid.ingredient;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class RefrigerantDummyFluidIngredient extends MoleculeTagFluidIngredient {

    public RefrigerantDummyFluidIngredient() {
        super(DestroyMolecules.Tags.REFRIGERANT, 1f);
        amountRequired = 1000;
    };

    @Override
    public MixtureFluidIngredient getNew() {
        return new RefrigerantDummyFluidIngredient();
    };

    @Override
    protected boolean testMixture(Mixture mixture) {
        return false; // This Ingredient should never be used in a Recipe
    };

    @Override
    protected String getMixtureFluidIngredientSubtype() {
        return "mixtureFluidWithRefrigerants";
    };

    @Override
    public List<Component> getDescription(CompoundTag fluidTag) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.addAll(TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.mixture_ingredient.molecule_tag_1").string(), Palette.GRAY_AND_WHITE));
        tooltip.add(DestroyMolecules.Tags.REFRIGERANT.getFormattedName());
        tooltip.addAll(TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.mixture_ingredient.refrigerants").string(), Palette.GRAY_AND_WHITE));
        return tooltip;
    };

    @Override
    protected void readInternal(JsonObject json) {};

    @Override
    protected void writeInternal(JsonObject json) {};
    
};
