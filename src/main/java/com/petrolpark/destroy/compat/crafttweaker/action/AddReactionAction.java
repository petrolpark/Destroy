package com.petrolpark.destroy.compat.crafttweaker.action;

import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.compat.jei.category.ReactionCategory;
import com.petrolpark.destroy.recipe.ReactionRecipe;

public class AddReactionAction extends DestroyAction {
    private final Reaction reaction;

    public AddReactionAction(Reaction reaction) {
        this.reaction = reaction;
    }
    @Override
    public void undo() {
        Reaction.REACTIONS.remove(reaction.getFullID());
        ReactionCategory.RECIPES.remove(reaction);
    }

    @Override
    public String describe() {
        return "Adds a reaction to the registry and makes it available to do in a vat";
    }

    @Override
    public String describeUndo() {
        return "Unregisters the reaction and makes it impossible to make in a vat";
    }

    @Override
    public void apply() {
        if (reaction.includeInJei()) ReactionCategory.RECIPES.put(reaction, ReactionRecipe.create("crafttweaker", reaction));
    }
}
