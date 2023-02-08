package com.petrolpark.destroy.recipe;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.util.CropMutation;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder.ProcessingRecipeParams;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class MutationRecipe extends ProcessingRecipe<RecipeWrapper> {

    private static int counter = 0;

    private CropMutation mutation;

    public static MutationRecipe create(CropMutation mutation) {
        MutationRecipe recipe = new ProcessingRecipeBuilder<>(MutationRecipe::new, Destroy.asResource("mutation_" + counter++))
            .require(Fluids.WATER, 1)
            .build();
        recipe.mutation = mutation;
        return recipe;
    };

    public MutationRecipe(ProcessingRecipeParams params) {
        super(DestroyRecipeTypes.MUTATION, params);
    };

    public CropMutation getMutation() {
        return this.mutation;
    }

    @Override
    public boolean matches(RecipeWrapper container, Level level) {
        return false;
    };

    @Override
    protected int getMaxInputCount() {
        return 2;
    };

    @Override
    protected int getMaxOutputCount() {
        return 2;
    };
};
