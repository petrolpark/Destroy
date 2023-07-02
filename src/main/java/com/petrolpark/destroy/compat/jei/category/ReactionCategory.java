package com.petrolpark.destroy.compat.jei.category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.client.gui.stackedTextBox.AbstractStackedTextBox;
import com.petrolpark.destroy.client.gui.stackedTextBox.AbstractStackedTextBox.LinesAndActivationAreas;
import com.petrolpark.destroy.compat.jei.HoverableTextCategory;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.recipe.ReactionRecipe;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ReactionCategory extends HoverableTextCategory<ReactionRecipe> {

    public static final Palette WHITE_AND_BLUE = Palette.ofColors(ChatFormatting.WHITE, ChatFormatting.AQUA);

    public static RecipeType<ReactionRecipe> TYPE;

    public static final Map<Reaction, ReactionRecipe> RECIPES = new HashMap<>();

    public ReactionCategory(Info<ReactionRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
        TYPE = info.recipeType();
    };

    /**
     * Generate every Reaction's recipe to go in JEI.
     */
    static {
        for (Reaction reaction : Reaction.REACTIONS) {
            RECIPES.put(reaction, ReactionRecipe.create(reaction));
        };
    };

    @Override
    public Collection<LinesAndActivationAreas> getHoverableTexts(ReactionRecipe recipe) {
        Reaction reaction = recipe.getReaction();
        if (reaction.getTranslationKey() == null) return List.of();

        Minecraft minecraft = Minecraft.getInstance();
        List<LinesAndActivationAreas> paragraphs = new ArrayList<>(2);
        paragraphs.add(AbstractStackedTextBox.getTextAndActivationAreas(Component.translatable(reaction.getNameSpace() + ".reaction." + reaction.getTranslationKey()).getString(), 2, 2, 176, minecraft.screen, minecraft.font, WHITE_AND_BLUE));
        paragraphs.add(AbstractStackedTextBox.getTextAndActivationAreas(Component.translatable(reaction.getNameSpace() + ".reaction." + reaction.getTranslationKey() + ".description").getString(), 2, 84, 176, minecraft.screen, minecraft.font, WHITE_AND_BLUE));
        return paragraphs;
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ReactionRecipe recipe, IFocusGroup focuses) {
        super.setRecipe(builder, recipe, focuses);
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 20)
            .addItemStack(DestroyItems.ABS.asStack());
    };
    
};
