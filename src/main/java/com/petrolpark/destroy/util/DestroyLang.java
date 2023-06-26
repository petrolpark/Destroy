package com.petrolpark.destroy.util;

import java.util.ArrayList;
import java.util.List;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import com.simibubi.create.foundation.utility.LangNumberFormat;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class DestroyLang {

    public static LangBuilder builder() {
        return new LangBuilder(Destroy.MOD_ID);
    };

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    };

    public static LangBuilder number(double d) {
        return builder().text(LangNumberFormat.format(d));
    };

    public static LangBuilder direction(Direction direction) {
        return translate("generic.direction."+Lang.asId(direction.name())+"");
    };

    public static void fluidContainerInfoHeader(List<Component> tooltip) {
        Lang.translate("gui.goggles.fluid_container")
            .forGoggles(tooltip);
    };

    public static void tankInfoTooltip(List<Component> tooltip, LangBuilder tankName, FluidTank tank) {
        LangBuilder mb = Lang.translate("generic.unit.millibuckets");

        tankName
            .style(ChatFormatting.GRAY)
            .forGoggles(tooltip, 0);

        if (tank.isEmpty()) {
            Lang.translate("gui.goggles.fluid_container.capacity")
			.add(Lang.number(tank.getCapacity())
				.add(mb)
				.style(ChatFormatting.GOLD))
			.style(ChatFormatting.GRAY)
			.forGoggles(tooltip, 1);
        } else {
            Lang.fluidName(tank.getFluid())
            .style(ChatFormatting.GRAY)
            .forGoggles(tooltip, 1);

            Lang.builder()
                .add(Lang.number(tank.getFluid().getAmount())
                    .add(mb)
                    .style(ChatFormatting.GOLD))
                .text(ChatFormatting.GRAY, " / ")
                .add(Lang.number(tank.getCapacity())
                    .add(mb)
                    .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);
        };
    };

    /**
     * Returns a progress bar which changes color depending on how full it is.
     * @param value
     * @param maxValue Should not be greater than value
     * @return Pretty text component
     */
    public static MutableComponent barMeterComponent(int value, int maxValue) {
        return barMeterComponent(value, maxValue, maxValue);
    };

    /**
     * Returns a progress bar which changes color depending on how full it is.
     * @param value
     * @param maxValue Should not be greater than value
     * @param totalBars How many total bars should be displayed
     * @return Pretty text component
     */
    public static MutableComponent barMeterComponent(int value, int maxValue, int totalBars) {
        float proportion = (float)value / maxValue;
        ChatFormatting color;
        if (proportion <= 0.25) {
            color = ChatFormatting.DARK_RED;
        } else if (proportion <= 0.5) {
            color = ChatFormatting.GOLD;
        } else {
            color = ChatFormatting.DARK_GREEN;
        };
        int bars = (int) Math.round((proportion * totalBars));
        return Component.empty()
            .append(Component.literal("|".repeat(bars)).withStyle(color))
            .append(Component.literal("|".repeat(totalBars - bars)).withStyle(ChatFormatting.DARK_GRAY));
    };

    /**
     * The required contents of a Mixture to be used as an Ingredient in a Recipe.
     * @param fluidTag The NBT of the Fluid Stack
     */
    public static List<Component> mixtureIngredientTooltip(CompoundTag fluidTag) {
        List<Component> tooltip = new ArrayList<>();
        String moleculeID = fluidTag.getString("IngredientMolecule");
        float concentration = fluidTag.getFloat("IngredientConcentration");

        Molecule molecule = Molecule.getMolecule(moleculeID);
        Component moleculeName = molecule == null ? DestroyLang.translate("tooltip.unknown_molecule").component() : molecule.getName(DestroyAllConfigs.CLIENT.chemistry.iupacNames.get());

        tooltip.addAll(TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.mixture_ingredient", moleculeName.getString(), Float.toString(concentration) + "M").string(), TooltipHelper.Palette.GRAY_AND_WHITE));

        return tooltip;
    };
}
