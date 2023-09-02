package com.petrolpark.destroy.compat.jei;

import com.petrolpark.destroy.chemistry.IItemReactant;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.reactionresult.PrecipitateReactionResult;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;

import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import net.minecraft.network.chat.Component;

public class ReactionTooltipHelper {

    public static IRecipeSlotTooltipCallback reactantTooltip(Reaction reaction, Molecule reactant) {
        boolean nerdMode = DestroyAllConfigs.CLIENT.chemistry.nerdMode.get();
        int ratio = reaction.getReactantMolarRatio(reactant);
        return (view, tooltip) -> {
            tooltip.add(Component.literal(" "));
            if (ratio == 1) {
                tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.reaction.reactant_ratio.single").component(), Palette.GRAY_AND_WHITE));
            } else {
                tooltip.set(0, Component.literal(ratio + " ").append(tooltip.get(0)));
                tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.reaction.reactant_ratio.plural", ratio).component(), Palette.GRAY_AND_WHITE));
            };
            if (nerdMode) tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.reaction.order", reaction.getOrders().get(reactant)).component(), Palette.GRAY_AND_WHITE));
        };
    };

    public static IRecipeSlotTooltipCallback productTooltip(Reaction reaction, Molecule product) {
        int ratio = reaction.getProductMolarRatio(product);
        return (view, tooltip) -> {
            tooltip.add(Component.literal(" "));
            if (ratio == 1) {
                tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.reaction.product_ratio.single").component(), Palette.GRAY_AND_WHITE));
            } else {
                tooltip.set(0, Component.literal(ratio + " ").append(tooltip.get(0)));
                tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.reaction.product_ratio.plural", ratio).component(), Palette.GRAY_AND_WHITE));
            };
        };
    };

    public static IRecipeSlotTooltipCallback catalystTooltip(Reaction reaction, Molecule catalyst) {
        boolean nerdMode = DestroyAllConfigs.CLIENT.chemistry.nerdMode.get();
        return (view, tooltip) -> {
            if (nerdMode) {
                tooltip.add(Component.literal(" "));
                tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.reaction.order", reaction.getOrders().get(catalyst)).component(), Palette.GRAY_AND_WHITE));
            };
        };
    };

    public static IRecipeSlotTooltipCallback itemReactantTooltip(Reaction reaction, IItemReactant itemReactant) {
        return (view, tooltip) -> {
            if (!itemReactant.isCatalyst()) {
                tooltip.add(Component.literal(" "));
                tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.reaction.item_reactant", reaction.getMolesPerItem()).component(), Palette.GRAY_AND_WHITE));
            };
        };
    };

    public static IRecipeSlotTooltipCallback precipitateTooltip(Reaction reaction, PrecipitateReactionResult precipitate) {
        return (view, tooltip) -> {
            tooltip.add(Component.literal(" "));
            tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.reaction.precipitate", precipitate.getRequiredMoles()).component(), Palette.GRAY_AND_WHITE));
        };
    };

    public static IRecipeSlotTooltipCallback nerdModeTooltip(Reaction reaction) {
        return (view, tooltip) -> {
            tooltip.clear();
            tooltip.add(DestroyLang.translate("tooltip.reaction.kinetics_information").component());
            tooltip.add(DestroyLang.translate("tooltip.reaction.activation_energy", reaction.getActivationEnergy()).component());
            tooltip.add(DestroyLang.translate("tooltip.reaction.enthalpy_change", reaction.getEnthalpyChange()).component());
            tooltip.add(DestroyLang.translate("tooltip.reaction.preexponential_factor", reaction.getPreexponentialFactor()).component());
        };
    };
    
};
