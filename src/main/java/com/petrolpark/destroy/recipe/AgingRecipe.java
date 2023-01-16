package com.petrolpark.destroy.recipe;

import com.simibubi.create.content.contraptions.processing.ProcessingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder.ProcessingRecipeParams;

import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class AgingRecipe extends ProcessingRecipe<RecipeWrapper> {

    public AgingRecipe(ProcessingRecipeParams params) {
        super(DestroyRecipeTypes.AGING, params);
    };

    @Override
    protected int getMaxInputCount() {
        return 1;
    };

    @Override
    protected int getMaxOutputCount() {
        return 0;
    };

    @Override
    protected int getMaxFluidInputCount() {
        return 0;
    };

    @Override
    protected int getMaxFluidOutputCount() {
        return 1;
    };

    @Override
    public boolean matches(RecipeWrapper pContainer, Level pLevel) {
        return true;
    };
    
}
