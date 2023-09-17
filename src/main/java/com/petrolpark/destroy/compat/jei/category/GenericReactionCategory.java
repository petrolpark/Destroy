package com.petrolpark.destroy.compat.jei.category;

import java.util.HashMap;
import java.util.Map;

import com.petrolpark.destroy.chemistry.genericreaction.DoubleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReaction;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.recipe.ReactionRecipe;

import mezz.jei.api.helpers.IJeiHelpers;

public class GenericReactionCategory extends ReactionCategory {

    public static Map<GenericReaction, ReactionRecipe> RECIPES = new HashMap<>();

    public GenericReactionCategory(Info<ReactionRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    /**
     * Generate every Generic Reaction's recipe to go in JEI.
     */
    static {
        for (GenericReaction genericReaction : GenericReaction.GENERIC_REACTIONS) {
            if (genericReaction instanceof SingleGroupGenericReaction sggr) {
                RECIPES.put(sggr, ReactionRecipe.create(sggr.getExampleReaction()));
            } else if (genericReaction instanceof DoubleGroupGenericReaction dggr) {
                //TODO
            };
        };
    };
    
};
