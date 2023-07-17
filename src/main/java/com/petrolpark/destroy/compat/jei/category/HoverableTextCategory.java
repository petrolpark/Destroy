package com.petrolpark.destroy.compat.jei.category;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.client.gui.stackedTextBox.AbstractStackedTextBox;
import com.petrolpark.destroy.client.gui.stackedTextBox.AbstractStackedTextBox.Area;
import com.petrolpark.destroy.client.gui.stackedTextBox.AbstractStackedTextBox.LinesAndActivationAreas;
import com.petrolpark.destroy.client.gui.stackedTextBox.StackedTextBox;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;
import com.simibubi.create.foundation.utility.Pair;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.Recipe;

public abstract class HoverableTextCategory<T extends Recipe<?>> extends DestroyRecipeCategory<T> implements ITickableCategory {

    private static final Map<Recipe<?>, Collection<LinesAndActivationAreas>> PARAGRAPHS = new HashMap<>();

    protected static AbstractStackedTextBox textBoxStack = AbstractStackedTextBox.NOTHING;;

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
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
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

        float partialTicks = AnimationTickHolder.getPartialTicks();
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        // Render hoverable paragraphs
        for (LinesAndActivationAreas paragraph : paragraphs) {
            for (int i = 0; i < paragraph.lines().size(); i++) {
                graphics.drawString(font, paragraph.lines().get(i), paragraph.startX(), paragraph.startY() + (i * font.lineHeight), 0xFFFFFF);
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
                        break checkParagraphs;
                    };
                };
            };
        };

        // Render the current stack of text boxes
        stack.pushPose();
        stack.translate(10, 10, 0);
        textBoxStack.render(graphics, (int)mouseX, (int)mouseY, partialTicks);
        stack.popPose();
    };
};
