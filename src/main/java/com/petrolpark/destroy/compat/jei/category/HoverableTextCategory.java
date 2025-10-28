package com.petrolpark.destroy.compat.jei.category;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.compat.jei.category.ITickableCategory;
import com.petrolpark.compat.jei.category.PetrolparkRecipeCategory;
import com.petrolpark.destroy.client.stackedtextbox.AbstractStackedTextBox;
import com.petrolpark.destroy.client.stackedtextbox.StackedTextBox;
import com.petrolpark.destroy.client.stackedtextbox.AbstractStackedTextBox.Area;
import com.petrolpark.destroy.client.stackedtextbox.AbstractStackedTextBox.LinesAndActivationAreas;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.lang.FontHelper.Palette;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.Recipe;

public abstract class HoverableTextCategory<T extends Recipe<?>> extends PetrolparkRecipeCategory<T> implements ITickableCategory {

    private static final Map<Recipe<?>, Collection<LinesAndActivationAreas>> PARAGRAPHS = new HashMap<>();

    protected AbstractStackedTextBox textBoxStack = AbstractStackedTextBox.NOTHING;
    protected static Recipe<?> textBoxActivatingRecipe = null; // Which recipe's text we are hovering over, if any

    public HoverableTextCategory(Info<T> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    /**
     * Use {@link AbstractStackedTextBox#getTextAndActivationAreas() this method} to generate paragraphs with hoverable definitions.
     * Any text boxes registered here will be automatically rendered, and their activation areas will be checked.
     */
    public abstract Collection<LinesAndActivationAreas> getHoverableTexts(T recipe);

    public Palette getPaletteForBoxes() {
        return Palette.GRAY_AND_WHITE;
    };

    @Override
    public void tick() {
        textBoxStack.tick();
        if (!textBoxStack.isActive()) {
            textBoxStack = AbstractStackedTextBox.NOTHING;
        };
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        textBoxStack = AbstractStackedTextBox.NOTHING;
        PARAGRAPHS.put(recipe, getHoverableTexts(recipe));
    };

    /**
     * @throws NullPointerException if the super method {@link HoverableTextCategory#setRecipe} is not called by a child.
     */
    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);
        PoseStack stack = graphics.pose();

        Collection<LinesAndActivationAreas> paragraphs = PARAGRAPHS.get(recipe);
        if (paragraphs == null) return;

        float partialTicks = AnimationTickHolder.getPartialTicks();
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        // Render hoverable paragraphs
        for (LinesAndActivationAreas paragraph : paragraphs) {
            for (int i = 0; i < paragraph.lines().size(); i++) {
                graphics.drawString(font, paragraph.lines().get(i), paragraph.startX(), paragraph.startY() + (i * font.lineHeight), 0xFFFFFF, false);
            };
        };

        // Check if a text box should be opened
        if (!textBoxStack.isActive()) {
            checkParagraphs: for (LinesAndActivationAreas paragraph : paragraphs) {
                for (Pair<Area, String> pair : paragraph.areas()) {
                    if (pair.getFirst().isIn((int)mouseX, (int)mouseY)) {
                        textBoxStack = new StackedTextBox(minecraft, (int)mouseX, (int)mouseY, AbstractStackedTextBox.NOTHING)
                            .withActivationArea(pair.getFirst())
                            .withPalette(getPaletteForBoxes())
                            .withText(pair.getSecond());
                        textBoxActivatingRecipe = recipe;
                        break checkParagraphs;
                    };
                };
            };
        };

        // Render the current stack of text boxes
        if (textBoxActivatingRecipe == recipe) {
            stack.pushPose();
            stack.translate(10, 0, 0);
            textBoxStack.render(graphics, (int)mouseX, (int)mouseY, partialTicks);
            stack.popPose();
        };
    };
};
