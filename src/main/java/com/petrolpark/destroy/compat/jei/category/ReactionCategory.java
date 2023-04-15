package com.petrolpark.destroy.compat.jei.category;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.recipe.ReactionRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;

public class ReactionCategory extends CreateRecipeCategory<ReactionRecipe> {

    public static RecipeType<ReactionRecipe> TYPE;

    public static final Map<Reaction, ReactionRecipe> RECIPES = new HashMap<>();

    /**
     * Generate every Reaction's recipe to go in JEI.
     */
    static {
        for (Reaction reaction : Reaction.REACTIONS) {
            RECIPES.put(reaction, ReactionRecipe.create(reaction));
        };
    };

    @Override
    public @NotNull RecipeType<ReactionRecipe> getRecipeType() {
        return super.getRecipeType();
    };

    public ReactionCategory(Info<ReactionRecipe> info) {
        super(info);
        TYPE = info.recipeType();
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ReactionRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, 0, 0).addItemStack(DestroyItems.ABS.asStack());
    };

    @Override
    public void draw(ReactionRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, stack, mouseX, mouseY);
    };

    
};
