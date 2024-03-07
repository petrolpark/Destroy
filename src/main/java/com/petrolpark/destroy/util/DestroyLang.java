package com.petrolpark.destroy.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.fluid.ingredient.MixtureFluidIngredient;
import com.petrolpark.destroy.fluid.ingredient.mixturesubtype.MixtureFluidIngredientSubType;
import com.petrolpark.destroy.util.vat.VatMaterial;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import com.simibubi.create.foundation.utility.LangNumberFormat;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

@MoveToPetrolparkLibrary
public class DestroyLang {

    private static final DecimalFormat df = new DecimalFormat();
    static {
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
    };

    private static final String[] subscriptNumbers = new String[]{"\u2080", "\u2081", "\u2082", "\u2083", "\u2084", "\u2085", "\u2086", "\u2087", "\u2088", "\u2089"};
    private static final String[] superscriptNumbers = new String[]{"\u2070", "\u00b9", "\u00b2", "\u00b3", "\u2074", "\u2075", "\u2076", "\u2077", "\u2078", "\u2079"};

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

    /**
     * Makes a String of text less than or equal to the given width (in pixels) by replacing the end with elipses.
     * @param string The String to shorten
     * @param font The Font that gives the width of the string
     * @param maxWidth The maxmium width (in pixels) the string should take up
     * @return The original string, shortened and mutated
    */
    public static String shorten(String string, Font font, int maxWidth) {
        if (font.width(string) <= maxWidth) return string;
        if (string.isBlank()) return "";
        String elipses = "...";
        int elipsesWidth = font.width(elipses);
        while (font.width(string) > maxWidth - elipsesWidth || string.charAt(string.length() - 1) == ' ') {
            string = string.substring(0, string.length() - 1);
            if (string.isBlank()) return "";
        };
        string += elipses;
        return string;
    };

    public static void fluidContainerInfoHeader(List<Component> tooltip) {
        Lang.translate("gui.goggles.fluid_container")
            .forGoggles(tooltip);
    };

    public static void tankInfoTooltip(List<Component> tooltip, LangBuilder tankName, FluidTank tank) {
        tankInfoTooltip(tooltip, tankName, tank.getFluid(), tank.getCapacity());
    };

    public static void tankInfoTooltip(List<Component> tooltip, LangBuilder tankName, FluidStack contents, int capacity) {
        LangBuilder mb = Lang.translate("generic.unit.millibuckets");

        tankName
            .style(ChatFormatting.GRAY)
            .forGoggles(tooltip, 0);

        if (contents.isEmpty()) {
            Lang.translate("gui.goggles.fluid_container.capacity")
			.add(Lang.number(capacity)
				.add(mb)
				.style(ChatFormatting.GOLD))
			.style(ChatFormatting.GRAY)
			.forGoggles(tooltip, 1);
        } else {
            Lang.fluidName(contents)
            .style(ChatFormatting.GRAY)
            .forGoggles(tooltip, 1);

            Lang.builder()
                .add(Lang.number(contents.getAmount())
                    .add(mb)
                    .style(ChatFormatting.GOLD))
                .text(ChatFormatting.GRAY, " / ")
                .add(Lang.number(capacity)
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

        MixtureFluidIngredientSubType<?> fluidIngredientType = MixtureFluidIngredient.MIXTURE_FLUID_INGREDIENT_SUBTYPES.get(fluidTag.getString("MixtureFluidIngredientSubtype"));

        return new ArrayList<>(fluidIngredientType.getDescription(fluidTag));
    };

    private static final float pressureMin = 0f;
    private static final float pressureMax = 1000000f;
    private static final float conductivityMin = 0f;
    private static final float conductivityMax = 100f;

    private static final Palette GRAYS = Palette.ofColors(ChatFormatting.DARK_GRAY, ChatFormatting.GRAY);

    public static List<Component> vatMaterialTooltip(ItemStack stack) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(stack.getHoverName());

        if (!(stack.getItem() instanceof BlockItem blockItem) || !VatMaterial.isValid(blockItem.getBlock())) return tooltip;
        tooltip.add(Component.literal(""));

        if (DestroyBlocks.VAT_CONTROLLER.isIn(stack)) {
            tooltip.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.vat_material.vat_controller").component(), DestroyLang.GRAYS));
            return tooltip;
        };

        boolean nerdMode = DestroyAllConfigs.CLIENT.chemistry.nerdMode.get();
        VatMaterial material = VatMaterial.BLOCK_MATERIALS.get(blockItem.getBlock());

        float conductivityPercent = Mth.clamp((material.thermalConductivity() - conductivityMin) / (conductivityMax - conductivityMin), 0f, 1f);
        float pressurePercent = Mth.clamp((material.maxPressure() - pressureMin) / (pressureMax - pressureMin), 0f, 1f);

        tooltip.add(
            DestroyLang.translate("tooltip.vat_material.pressure").style(ChatFormatting.WHITE)
            .space()
            .add(Component.literal(TooltipHelper.makeProgressBar(5, (int)(5 * pressurePercent + 0.5f))).withStyle(getStatColor(pressurePercent, false)))
            .component()
        );
        if (nerdMode) tooltip.add(DestroyLang.translate("tooltip.vat_material.pressure.nerd_mode", material.maxPressure() / 1000f).component());
        tooltip.addAll(TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.vat_material.pressure.description").string(), GRAYS));
        tooltip.add(Component.literal("")); 

        tooltip.add(
            DestroyLang.translate("tooltip.vat_material.conductivity").style(ChatFormatting.WHITE)
            .space()
            .add(Component.literal(TooltipHelper.makeProgressBar(5, (int)(5 * conductivityPercent + 0.5f))).withStyle(getStatColor(conductivityPercent, true)))
            .component()
        );
        if (nerdMode) tooltip.add(DestroyLang.translate("tooltip.vat_material.conductivity.nerd_mode", material.thermalConductivity()).component());
        tooltip.addAll(TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.vat_material.conductivity.description").string(), GRAYS));
        tooltip.add(Component.literal(""));

        tooltip.add(
            DestroyLang.translate("tooltip.vat_material.transparent").style(ChatFormatting.WHITE)
            .space()
            .add(material.transparent() ? tick() : cross())
            .component()
        );
        tooltip.addAll(TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.vat_material.transparent.description").string(), GRAYS));

        return tooltip;
    };

    public static MutableComponent tick() {
        return Component.literal("\u2714").withStyle(ChatFormatting.GREEN).copy();
    };

    public static MutableComponent cross() {
        return Component.literal("\u2718").withStyle(ChatFormatting.RED).copy();
    };

    public static ChatFormatting getStatColor(float stat, boolean inverted) {
        if (stat > 0.67f) return inverted ? ChatFormatting.RED : ChatFormatting.GREEN;
        if (stat > 0.33f) return ChatFormatting.YELLOW;
        return inverted ? ChatFormatting.GREEN : ChatFormatting.RED;
    };

    /**
     * Converts a number to Unicode subscript.
     * @param value
     */
    public static String toSubscript(int value) {
        String string = "";
        for (char c : String.valueOf(value).toCharArray()) {
            if (c == '-') string += "\u208b";
            string += subscriptNumbers[Integer.valueOf(String.valueOf(c))];
        };
        return string;
    };

    /**
     * Converts a number to Unicode superscript.
     * @param value Should only be passed strings containing numbers, {@code +} and {@code -}.
     */
    public static String toSuperscript(String value) {
        StringBuilder string = new StringBuilder();
        for (char c : value.toCharArray()) {
            if (c == '-') string.append("\u207b");
            if (c == '+') string.append("\u207a");
            try {
                string.append(superscriptNumbers[Integer.parseInt(String.valueOf(c))]);
            } catch (Throwable e) {};
        };
        return string.toString();
    };

    public static enum TemperatureUnit {

        KELVINS(t -> t, "K"),
        DEGREES_CELCIUS(t -> t - 273f, "\u00B0C"),
        DEGREES_FARENHEIT(t -> (t - 273f) * 9/5 + 32, "\u00B0F");

        private static final DecimalFormat df = new DecimalFormat();
        static {
            df.setMinimumFractionDigits(1);
            df.setMaximumFractionDigits(1);
        };

        private final UnaryOperator<Float> conversionFromKelvins;
        private final String symbol;

        TemperatureUnit(UnaryOperator<Float> conversionFromKelvins, String symbol) {
            this.conversionFromKelvins = conversionFromKelvins;
            this.symbol = symbol;
        };

        public String of(float temperature) {
            return df.format(conversionFromKelvins.apply(temperature)) + symbol;
        };

        public String of(float temperature, DecimalFormat df) {
            return df.format(conversionFromKelvins.apply(temperature)) + symbol;
        };
    };
}
