package com.petrolpark.destroy.compat.jei.category;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.client.gui.DestroyGuiTextures;
import com.petrolpark.destroy.compat.jei.animation.GUIBlockRenderer;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.recipe.MutationRecipe;
import com.petrolpark.destroy.util.CropMutation;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class MutationCategory extends DestroyRecipeCategory<MutationRecipe> {

    public static final List<MutationRecipe> RECIPES = new ArrayList<>();

    private static final GUIBlockRenderer blockRenderer = new GUIBlockRenderer();

    private static final int BLOCK_SCALE = 23;
    private static final int LEFT_BLOCK_X = 11;
    private static final int RIGHT_BLOCK_X = 79; 
    private static final int BOTTOM_BLOCK_Y = 90;
    private static final BlockState farmlandBlock = Blocks.FARMLAND.defaultBlockState().setValue(BlockStateProperties.MOISTURE, 7);

    static {
        for (List<CropMutation> mutations : CropMutation.MUTATIONS.values()) {
            for (CropMutation mutation : mutations) {
                RECIPES.add(MutationRecipe.create(mutation));
            };
        };
    };

    public MutationCategory(Info<MutationRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MutationRecipe recipe, IFocusGroup focuses) {
        builder
            .addSlot(RecipeIngredientRole.INPUT, 52, 40)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(Ingredient.of(DestroyItems.HYPERACCUMULATING_FERTILIZER.get()));
        builder
            .addSlot(RecipeIngredientRole.INPUT, LEFT_BLOCK_X + 7, 3)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(Ingredient.of(recipe.getMutation().getStartCropSupplier().get().asItem()));
        builder
            .addSlot(RecipeIngredientRole.OUTPUT, RIGHT_BLOCK_X + 7, 3)
            .setBackground(getRenderedSlot(), -1, -1)
            .addItemStack(new ItemStack(recipe.getMutation().getResultantCropSupplier().get().getBlock().asItem(), 1));

        if (recipe.getMutation().isOreSpecific()) {
            Block oreBlock = recipe.getMutation().getOreSupplier().get();
            builder
                .addSlot(RecipeIngredientRole.INPUT, LEFT_BLOCK_X + 7, 106)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(Ingredient.of(oreBlock));
            builder
                .addSlot(RecipeIngredientRole.OUTPUT, RIGHT_BLOCK_X + 7, 106)
                .setBackground(getRenderedSlot(), -1, -1)
                .addItemStack(new ItemStack(recipe.getMutation().getResultantBlockUnder(oreBlock.defaultBlockState()).getBlock().asItem(), 1));
        };
    };

    @Override
    public void draw(MutationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);
        PoseStack stack = graphics.pose();

        BlockState startOreBlock = recipe.getMutation().isOreSpecific() ? recipe.getMutation().getOreSupplier().get().defaultBlockState() : Blocks.STONE.defaultBlockState();
        BlockState endOreBlock = recipe.getMutation().getResultantBlockUnder(startOreBlock);

        // Shadows
        AllGuiTextures.JEI_SHADOW.render(graphics, LEFT_BLOCK_X - 11, BOTTOM_BLOCK_Y - 2);
        AllGuiTextures.JEI_SHADOW.render(graphics, RIGHT_BLOCK_X - 11, BOTTOM_BLOCK_Y - 2);

        // Arrow
        DestroyGuiTextures.JEI_SHORT_RIGHT_ARROW.render(graphics, 52, 60);

        // Left pillar of Blocks
        stack.pushPose();
        stack.translate(LEFT_BLOCK_X, BOTTOM_BLOCK_Y, 200);
        blockRenderer.renderBlock(startOreBlock, graphics, BLOCK_SCALE); // Ore or Stone Block
        stack.translate(0, 1 - BLOCK_SCALE, BLOCK_SCALE / 2f);
        blockRenderer.renderBlock(farmlandBlock, graphics, BLOCK_SCALE); // Farmland Block
        stack.translate(0, 1 - BLOCK_SCALE, BLOCK_SCALE / 2f);
        blockRenderer.renderBlock(recipe.getMutation().getStartCropSupplier().get().defaultBlockState(), graphics, BLOCK_SCALE); // Crop
        stack.popPose();

        // Right pillar of Blocks
        stack.pushPose();
        stack.translate(RIGHT_BLOCK_X, BOTTOM_BLOCK_Y, 200);
        blockRenderer.renderBlock(endOreBlock, graphics, BLOCK_SCALE); // Stone Block
        stack.translate(0, 1 - BLOCK_SCALE, BLOCK_SCALE / 2f);
        blockRenderer.renderBlock(farmlandBlock, graphics, BLOCK_SCALE); // Farmland Block
        stack.translate(0, 1 - BLOCK_SCALE, BLOCK_SCALE / 2f);
        blockRenderer.renderBlock(recipe.getMutation().getResultantCropSupplier().get(), graphics, BLOCK_SCALE); // Resultant Crop
        stack.popPose();
    };
    
};
