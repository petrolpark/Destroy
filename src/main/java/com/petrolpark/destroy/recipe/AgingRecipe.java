package com.petrolpark.destroy.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;

import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class AgingRecipe extends SingleFluidRecipe {

    public AgingRecipe(ProcessingRecipeParams params) {
        super(DestroyRecipeTypes.AGING, params);
    };

    public FluidStack getResult() {
        return fluidResults.get(0);
    }

    @Override
    protected int getMaxInputCount() {
        return 2;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 1;
    };

    @Override
    public boolean matches(RecipeWrapper pContainer, Level level) {
        return false;
    };

    @Override
    public String getRecipeTypeName() {
        return "aging";
    };
}
