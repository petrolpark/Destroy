package com.petrolpark.destroy.compat.jei.category;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReaction;
import com.petrolpark.destroy.recipe.ReactionRecipe;

import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;

public class GenericReactionCategory extends ReactionCategory {

    public static RecipeType<ReactionRecipe> TYPE;

    /**
     * Each {@link com.petrolpark.destroy.chemistry.GroupType Group Type} mapped to the Set of {@link com.petrolpark.destroy.chemistry.genericreaction.GenericReaction Generic Reactions} that can produce it.
     * The {@link com.petrolpark.destroy.chemistry.Molecule#isNovel novel} results of {@link com.petrolpark.destroy.chemistry.genericreaction.GenericReaction#getExampleReaction this Reaction} are used to
     * determine if a Generic Reaction can produce a Group.
     */
    public static Map<GroupType<?>, Set<GenericReaction>> GROUP_RECIPES = new HashMap<>();

    /**
     * The set of all {@link com.petrolpark.destroy.chemistry.genericreaction.GenericReaction#getExampleReaction example} {@link com.petrolpark.destroy.chemistry.Reaction Reactions}, mapped to
     * the {@link com.petrolpark.destroy.chemistry.genericreaction.GenericReaction Generic Reactions} of which they are the example.
     */
    public static Map<GenericReaction, ReactionRecipe> RECIPES = new HashMap<>();

    public GenericReactionCategory(Info<ReactionRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
        TYPE = info.recipeType();
    };

    /**
     * Generate every Generic Reaction's recipe to go in JEI.
     */
    static {
        for (GenericReaction genericReaction : GenericReaction.GENERIC_REACTIONS) {
            Reaction reaction = genericReaction.getExampleReaction();
            if (reaction != null) {
                // Add the Generic Reaction to JEI.
                RECIPES.put(genericReaction, ReactionRecipe.create(reaction));
                for (Molecule product : reaction.getProducts()) {
                    if (product.isNovel()) {
                        // Determine what functional groups this Generic Reaction can produce
                        for (Group<?> functionalGroup : product.getFunctionalGroups()) {
                            GROUP_RECIPES.merge(functionalGroup.getType(), Set.of(genericReaction), Sets::union);
                        };
                    };
                };
            };
        };
    };
    
};
