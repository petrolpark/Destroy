package com.petrolpark.destroy.recipe;

import java.util.ArrayList;
import java.util.List;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.util.BlockExtrusion;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class ExtrusionRecipe extends ProcessingRecipe<RecipeWrapper> {

    private static int counter = 0;

    private BlockExtrusion extrusion;

    public static final List<ExtrusionRecipe> RECIPES = new ArrayList<>(BlockExtrusion.EXTRUSIONS.size());

    static {
        for (BlockExtrusion extrusion : BlockExtrusion.EXTRUSIONS.values()) {
            RECIPES.add(create(extrusion));
        };
    };

    public ExtrusionRecipe(ProcessingRecipeParams params) {
        super(DestroyRecipeTypes.EXTRUSION, params);
    };

    public static ExtrusionRecipe create(BlockExtrusion extrusion) {
        Block ingredient = BlockExtrusion.EXTRUSIONS.inverse().get(extrusion);
        ExtrusionRecipe recipe = new ProcessingRecipeBuilder<>(ExtrusionRecipe::new, Destroy.asResource("extrusion" + counter++))
            .withItemIngredients(Ingredient.of(new ItemStack(ingredient.asItem(), 1)))
            .withItemOutputs(new ProcessingOutput(new ItemStack(extrusion.getExtruded(ingredient.defaultBlockState(), Direction.NORTH).getBlock().asItem(), 1), 1.0f))
            .build();
        recipe.extrusion = extrusion;
        return recipe;
    };

    public BlockExtrusion getExtrusion() {
        return extrusion;
    };

    @Override
    public boolean matches(RecipeWrapper pContainer, Level pLevel) {
        return false;
    };

    @Override
    protected int getMaxInputCount() {
        return 1;
    };

    @Override
    protected int getMaxOutputCount() {
        return 1;
    };
    
};
