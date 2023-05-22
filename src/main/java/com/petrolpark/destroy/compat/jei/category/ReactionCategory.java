package com.petrolpark.destroy.compat.jei.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.client.gui.stackedTextBox.AbstractStackedTextBox;
import com.petrolpark.destroy.client.gui.stackedTextBox.StackedTextBox;
import com.petrolpark.destroy.client.gui.stackedTextBox.StackedTextBox.Area;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.recipe.ReactionRecipe;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.utility.Pair;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ReactionCategory extends DestroyRecipeCategory<ReactionRecipe> implements ITickableCategory {

    public static RecipeType<ReactionRecipe> TYPE;

    public static final Map<Reaction, ReactionRecipe> RECIPES = new HashMap<>();

    private AbstractStackedTextBox textBoxStack;
    private List<Pair<Area, String>> activationAreas;

    /**
     * Generate every Reaction's recipe to go in JEI.
     */
    static {
        for (Reaction reaction : Reaction.REACTIONS) {
            RECIPES.put(reaction, ReactionRecipe.create(reaction));
        };
    };

    public ReactionCategory(Info<ReactionRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
        TYPE = info.recipeType();
        textBoxStack = AbstractStackedTextBox.NOTHING;
        activationAreas = new ArrayList<>();
    };

    @Override
    public @NotNull RecipeType<ReactionRecipe> getRecipeType() {
        return super.getRecipeType();
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ReactionRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, 0, 20).addItemStack(DestroyItems.ABS.asStack());
        activationAreas.add(Pair.of(new Area(0, 0, 16, 16), "pee"));
    };

    @Override
    public void draw(ReactionRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, stack, mouseX, mouseY);

        float partialTicks = AnimationTickHolder.getPartialTicks();
        Reaction reaction = recipe.getReaction();
        Minecraft minecraft = Minecraft.getInstance();

        if (textBoxStack == AbstractStackedTextBox.NOTHING) {
            for (Pair<Area, String> pair : activationAreas) {
                if (pair.getFirst().isIn((int)mouseX, (int)mouseY)) {
                    textBoxStack = new StackedTextBox(minecraft, (int)mouseX, (int)mouseY, AbstractStackedTextBox.NOTHING)
                        .withActivationArea(pair.getFirst())
                        .withLines(Component.literal("I am henry i am henry i am henry"));
                    break;
                };
            };
        };

        stack.pushPose();
        stack.translate(10, 10, 0);
        textBoxStack.render(stack, (int)mouseX, (int)mouseY, partialTicks);
        stack.popPose();

        minecraft.font.draw(stack, DestroyLang.translate("reaction."+reaction.getTranslationKey()).component(), 0, 0, 0xFFFFFF);
    }

    @Override
    public void tick() {
        textBoxStack.tick();
    };
    
};
