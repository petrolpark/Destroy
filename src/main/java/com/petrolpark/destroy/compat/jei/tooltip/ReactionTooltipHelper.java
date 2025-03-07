package com.petrolpark.destroy.compat.jei.tooltip;

import com.petrolpark.destroy.chemistry.legacy.IItemReactant;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.chemistry.legacy.LegacyReaction;
import com.petrolpark.destroy.chemistry.legacy.reactionresult.PrecipitateReactionResult;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.simibubi.create.foundation.item.TooltipHelper;
import net.createmod.catnip.lang.FontHelper.Palette;

import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ReactionTooltipHelper {

    public static IRecipeSlotRichTooltipCallback reactantTooltip(LegacyReaction reaction, LegacySpecies reactant) {
        boolean nerdMode = DestroyAllConfigs.CLIENT.chemistry.nerdMode.get();
        int ratio = reaction.getReactantMolarRatio(reactant);
        return (view, tooltip) -> {
            tooltip.add(Component.literal(" "));
            if (ratio == 1) {
                tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.reaction.reactant_ratio.single").component(), Palette.GRAY_AND_WHITE));
            } else {
                tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.reaction.reactant_ratio.plural", ratio).component(), Palette.GRAY_AND_WHITE));
            };
            if (nerdMode) tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.reaction.order", reaction.getOrders().get(reactant)).component(), Palette.GRAY_AND_WHITE));
        };
    };

    public static IRecipeSlotRichTooltipCallback productTooltip(LegacyReaction reaction, LegacySpecies product) {
        int ratio = reaction.getProductMolarRatio(product);
        return (view, tooltip) -> {
            tooltip.add(Component.literal(" "));
            if (ratio == 1) {
                tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.reaction.product_ratio.single").component(), Palette.GRAY_AND_WHITE));
            } else {
                tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.reaction.product_ratio.plural", ratio).component(), Palette.GRAY_AND_WHITE));
            };
        };
    };

    public static IRecipeSlotRichTooltipCallback catalystTooltip(LegacyReaction reaction, LegacySpecies catalyst) {
        boolean nerdMode = DestroyAllConfigs.CLIENT.chemistry.nerdMode.get();
        return (view, tooltip) -> {
            if (nerdMode) {
                tooltip.add(Component.literal(" "));
                tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.reaction.order", reaction.getOrders().get(catalyst)).component(), Palette.GRAY_AND_WHITE));
            };
        };
    };

    public static IRecipeSlotRichTooltipCallback itemReactantTooltip(LegacyReaction reaction, IItemReactant itemReactant) {
        return (view, tooltip) -> {
            if (!itemReactant.isCatalyst()) {
                tooltip.add(Component.literal(" "));
                tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.reaction.item_reactant", reaction.getMolesPerItem()).component(), Palette.GRAY_AND_WHITE));
            };
        };
    };

    public static IRecipeSlotRichTooltipCallback precipitateTooltip(LegacyReaction reaction, PrecipitateReactionResult precipitate) {
        return (view, tooltip) -> {
            tooltip.add(Component.literal(" "));
            tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.reaction.precipitate", precipitate.getRequiredMoles()).component(), Palette.GRAY_AND_WHITE));
        };
    };

    public static IRecipeSlotRichTooltipCallback nerdModeTooltip(LegacyReaction reaction) {
        return (view, tooltip) -> {
            boolean reversible = reaction.getReverseReactionForDisplay().isPresent();
            tooltip.add(DestroyLang.translate("tooltip.reaction.kinetics_information").component());
            if (reaction.isHalfReaction()) tooltip.add(DestroyLang.translate("tooltip.reaction.standard_half_cell_potential", reaction.getStandardHalfCellPotential()).style(ChatFormatting.GRAY).component());
            if (reversible) tooltip.add(DestroyLang.translate("tooltip.reaction.forward").component());
            tooltip.add(Component.literal(reversible ? "  " : "").append(DestroyLang.translate("tooltip.reaction.activation_energy", reaction.getActivationEnergy()).style(ChatFormatting.GRAY).component()));
            tooltip.add(Component.literal(reversible ? "  " : "").append(DestroyLang.translate("tooltip.reaction.enthalpy_change", reaction.getEnthalpyChange()).style(ChatFormatting.GRAY).component()));
            tooltip.add(Component.literal(reversible ? "  " : "").append(DestroyLang.preexponentialFactor(reaction)).withStyle(ChatFormatting.GRAY));
            if (reversible) {
                tooltip.add(DestroyLang.translate("tooltip.reaction.reverse").component());
                LegacyReaction reverseReaction = reaction.getReverseReactionForDisplay().get();
                tooltip.add(Component.literal("  ").append(DestroyLang.translate("tooltip.reaction.activation_energy", reverseReaction.getActivationEnergy()).style(ChatFormatting.GRAY).component()));
                tooltip.add(Component.literal("  ").append(DestroyLang.translate("tooltip.reaction.enthalpy_change", reverseReaction.getEnthalpyChange()).style(ChatFormatting.GRAY).component()));
                tooltip.add(Component.literal("  ").append(DestroyLang.preexponentialFactor(reverseReaction)).withStyle(ChatFormatting.GRAY));
            };
        };
    };
    
};
