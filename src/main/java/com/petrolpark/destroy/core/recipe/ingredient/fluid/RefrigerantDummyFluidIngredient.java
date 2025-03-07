package com.petrolpark.destroy.core.recipe.ingredient.fluid;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.petrolpark.destroy.chemistry.legacy.LegacyMixture;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyMolecules;
import com.petrolpark.destroy.client.DestroyLang;
import com.simibubi.create.foundation.item.TooltipHelper;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class RefrigerantDummyFluidIngredient extends MoleculeTagFluidIngredient {

    public static final Type TYPE = new Type();

    public RefrigerantDummyFluidIngredient() {
        super(DestroyMolecules.Tags.REFRIGERANT, 0.9f, 1.1f);
        amountRequired = 1000;
    };

    @Override
    protected boolean testMixture(LegacyMixture mixture) {
        return false; // This Ingredient should never be used in a Recipe
    };

    @Override
    protected void readInternal(JsonObject json) {};

    @Override
    protected void writeInternal(JsonObject json) {};

    protected static class Type extends MoleculeTagFluidIngredient.Type {

        @Override
        public RefrigerantDummyFluidIngredient getNew() {
            return new RefrigerantDummyFluidIngredient();
        };

        @Override
        public String getMixtureFluidIngredientSubtype() {
            return "mixtureFluidWithRefrigerants";
        };
    
        @Override
        public List<Component> getDescription(CompoundTag fluidTag) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.addAll(TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.mixture_ingredient.molecule_tag_1").string(), FontHelper.Palette.GRAY_AND_WHITE));
            tooltip.add(DestroyMolecules.Tags.REFRIGERANT.getFormattedName());
            tooltip.addAll(TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.mixture_ingredient.refrigerants").string(), FontHelper.Palette.GRAY_AND_WHITE));
            return tooltip;
        };

    };
    
};
