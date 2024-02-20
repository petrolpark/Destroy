package com.petrolpark.destroy.compat.crafttweaker.action;

import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.compat.jei.category.ReactionCategory;
import com.petrolpark.destroy.recipe.ReactionRecipe;

public class RemoveReactionAction extends DestroyAction {
    private final Reaction reaction;
    private final ReactionRecipe reactionRecipe;

    public RemoveReactionAction(Reaction reaction) {
        this.reaction = reaction;
        this.reactionRecipe = ReactionCategory.RECIPES.get(reaction);
    }

    @Override
    public void undo() {
        Reaction.REACTIONS.put(reaction.getFullID(), reaction);
        if(reactionRecipe != null) {
            ReactionCategory.RECIPES.put(reaction, reactionRecipe);
        }
    }

    @Override
    public String describe() {
        return "Removes the reaction from the registry";
    }

    @Override
    public String describeUndo() {
        return "Adds the reaction back into the registry";
    }

    @Override
    public void apply() {
        Reaction.REACTIONS.remove(reaction.getFullID());
        ReactionCategory.RECIPES.remove(reaction);
    }
}
