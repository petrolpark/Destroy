package com.petrolpark.destroy.compat.jei.animation;

import java.util.ArrayList;
import java.util.List;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.utility.Lang;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class HeatConditionRenderer {

    public static void drawHeatConditionName(Font font, GuiGraphics graphics, int x, int y, HeatCondition requiredHeat) {
        MutableComponent name = Component.empty();
        if (requiredHeat.name() == "COOLED") { // Scuffed but okay keep your opinions to yourself
            name = DestroyLang.translate(requiredHeat.getTranslationKey()).component();
        } else {
            name = Lang.translate(requiredHeat.getTranslationKey()).component();
        };
        graphics.drawString(font, name, x, y, requiredHeat.getColor(), false); // This is equivalent of the line being overwritten
    };

    /**
     * Render the Blaze Burner or Cooler, and superheating treat if necessary.
     */
    public static void addHeatConditionSlots(IRecipeLayoutBuilder builder, int x, int y, HeatCondition requiredHeat) {
        List<ItemStack> blazeTreatStacks = new ArrayList<>();
        ForgeRegistries.ITEMS.tags().getTag(AllTags.AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.tag).forEach(item -> {
            blazeTreatStacks.add(new ItemStack(item));
        });
        
        if (requiredHeat.name() == "COOLED") {
            builder.addSlot(RecipeIngredientRole.CATALYST, x, y).addItemStack(DestroyBlocks.COOLER.asStack());
        } else if (requiredHeat != HeatCondition.NONE) { // This one is copied right from Create; it renders the Blaze Burner
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, x, y).addItemStack(AllBlocks.BLAZE_BURNER.asStack());
        };
        if (requiredHeat == HeatCondition.SUPERHEATED) { // Used to render all possible Blaze 'treats' rather than just the Blaze Cake
            builder.addSlot(RecipeIngredientRole.CATALYST, x + 19, y).addItemStacks(blazeTreatStacks);
        };
    };
};
