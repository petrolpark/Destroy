package com.petrolpark.destroy.recipe;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReaction;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;

import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class ReactionRecipe extends ProcessingRecipe<RecipeWrapper> {

    private static int counter;

    protected Reaction reaction; // The Reaction associated with this Recipe

    public ReactionRecipe(ProcessingRecipeParams params) {
        super(DestroyRecipeTypes.REACTION, params);
    };

    public static ReactionRecipe create(Reaction reaction) {
        ReactionRecipe recipe = new ProcessingRecipeBuilder<>(ReactionRecipe::new, Destroy.asResource("reaction_"+counter++)).build();
        recipe.reaction = reaction;
        return recipe;
    };

    public Reaction getReaction() {
        return reaction;
    };

    @Override
    public boolean matches(RecipeWrapper pContainer, Level level) {
        return false;
    };

    @Override
    protected int getMaxInputCount() {
        return 0;
    };

    @Override
    protected int getMaxOutputCount() {
        return 0;
    };

    public static class GenericReactionRecipe extends ReactionRecipe {

        protected GenericReaction genericReaction;

        public GenericReactionRecipe(ProcessingRecipeParams params) {
            super(params);
        };

        public static GenericReactionRecipe create(GenericReaction genericReaction) {
            GenericReactionRecipe recipe = new ProcessingRecipeBuilder<>(GenericReactionRecipe::new, Destroy.asResource("generic_reaction_"+counter++)).build();
            recipe.reaction = genericReaction.getExampleReaction();
            recipe.genericReaction = genericReaction;
            return recipe;
        };

        public GenericReaction getGenericReaction() {
            return genericReaction;
        };
    };
     
};
