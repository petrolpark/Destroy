package com.petrolpark.destroy.compat.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.compat.jei.category.GenericReactionCategory;
import com.petrolpark.destroy.compat.jei.category.ReactionCategory;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.fluid.ingredient.MixtureFluidIngredient;
import com.petrolpark.destroy.fluid.ingredient.mixturesubtype.MixtureFluidIngredientSubType;
import com.petrolpark.destroy.recipe.ReactionRecipe;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;

public class DestroyRecipeManagerPlugin implements IRecipeManagerPlugin {

    private final IJeiHelpers helpers;

    public DestroyRecipeManagerPlugin(IJeiHelpers helpers) {
        this.helpers = helpers;
    };

    @Override
    public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> focus) {
        List<RecipeType<?>> recipeTypes = new ArrayList<RecipeType<?>>();
        if (
            // Molecules
            focus.getTypedValue().getType() == MoleculeJEIIngredient.TYPE 
            // Mixture ingredients (which contain a single Molecule)
            || (focus.checkedCast(ForgeTypes.FLUID_STACK).map(fluidFocus -> {
                FluidStack fluidStack = fluidFocus.getTypedValue().getIngredient();
                return DestroyFluids.isMixture(fluidStack);
            }).orElse(false))
        ) {
            recipeTypes.add(ReactionCategory.TYPE); // Add the Reaction Recipe type
            recipeTypes.add(GenericReactionCategory.TYPE); // Add the generic Reaction Recipe type
            recipeTypes.addAll(DestroyJEI.RECIPE_TYPES.keySet()); // Add all processing Recipes applicable to Mixtures
        };
        return recipeTypes;
    };

    @Override
    @SuppressWarnings("unchecked")
    public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
        List<T> recipes = new ArrayList<>();

        // Mixture ingredients and outputs
        // For Mixture inputs and outputs simply replace this lookup with the lookup for the Molecules the Mixture can/does contain
        focus.checkedCast(ForgeTypes.FLUID_STACK).ifPresent(fluidFocus -> { // Check to see if it's a Fluid ingredient
            FluidStack fluidStack = fluidFocus.getTypedValue().getIngredient();
            if (!DestroyFluids.isMixture(fluidStack)) return;
            MixtureFluidIngredientSubType<?> ingredientType = MixtureFluidIngredient.MIXTURE_FLUID_INGREDIENT_SUBTYPES.get(fluidStack.getOrCreateTag().getString("MixtureFluidIngredientSubtype"));
            
            Collection<Molecule> molecules = List.of();

            //TODO replace with disambiguation page of all contained Molecules, rather than just include the uses/recipes of all component Molecules in one
            if (ingredientType != null) { // Mixture ingredients
                molecules = ingredientType.getContainedMolecules(fluidStack.getOrCreateTag());
            } else if (fluidStack.getOrCreateTag().contains("Mixture")) { // Mixture outputs
                molecules = ReadOnlyMixture.readNBT(ReadOnlyMixture::new, fluidStack.getOrCreateChildTag("Mixture")).getContents(true);
            };

            molecules.forEach(molecule -> {
                recipes.addAll(getRecipes(recipeCategory, helpers.getFocusFactory().createFocus(focus.getRole(), MoleculeJEIIngredient.TYPE, molecule)));
            });

        });

        // Molecules
        Molecule molecule = focus.checkedCast(MoleculeJEIIngredient.TYPE).map(moleculeIngredient -> moleculeIngredient.getTypedValue().getIngredient()).orElse(null);
        if (molecule != null) { // Ignore this if we're not dealing with a Molecule
            switch (focus.getRole()) {
                case INPUT: {

                    // Add Generic Reaction Recipes
                    if (recipeCategory instanceof GenericReactionCategory) {
                        molecule.getFunctionalGroups().forEach(group -> {
                            Optional.ofNullable((Group.groupTypesAndReactions.get(group.getType()))).ifPresent(set -> set.forEach(genericReaction -> {
                                ReactionRecipe recipe = GenericReactionCategory.RECIPES.get(genericReaction);
                                if (recipe != null) recipes.add((T)recipe);
                            }));
                        });
                    }

                    // Add Reaction Recipes
                    else if (recipeCategory instanceof ReactionCategory) {
                        molecule.getReactantReactions().forEach(reaction -> {
                            Optional.ofNullable((T)(ReactionCategory.RECIPES.get(reaction))).ifPresent(recipes::add);
                        }); // This is an unchecked conversion but I think it's fine
                    };
                    
                    // Add non-Reaction Recipes
                    List<Recipe<?>> recipeUses = DestroyJEI.MOLECULES_INPUT.get(molecule); // Recipes in which a Mixture containing this Molecule is required
                    if (recipeUses != null) {
                        recipes.addAll(recipeUses.stream()
                            .filter(recipe -> recipe.getClass().equals(DestroyJEI.RECIPE_TYPES.get(recipeCategory.getRecipeType()))) // Check for Recipes that match this category
                            .map(recipe -> (T) recipe) // Cast these Recipes (unchecked conversion, but should be okay as we just checked the class)
                            .toList()
                        );
                    };
                    break;
                }
                case OUTPUT: {

                    // Add Generic Reaction Recipes
                    if (recipeCategory instanceof GenericReactionCategory) {
                        molecule.getFunctionalGroups().forEach(group -> {
                            Optional.ofNullable((GenericReactionCategory.GROUP_RECIPES.get(group.getType()))).ifPresent(set -> set.forEach(genericReaction -> {
                                ReactionRecipe recipe = GenericReactionCategory.RECIPES.get(genericReaction);
                                if (recipe != null) recipes.add((T)recipe);
                            }));
                        });
                    }

                    // Add Reaction Recipes
                    else if (recipeCategory instanceof ReactionCategory) molecule.getProductReactions().forEach(reaction -> {
                        Optional.ofNullable((T)(ReactionCategory.RECIPES.get(reaction))).ifPresent(recipes::add);
                    }); // This is an unchecked conversion but I think it's fine
                
                    // Add non-Reaction Recipes
                    List<Recipe<?>> recipeProductions = DestroyJEI.MOLECULES_OUTPUT.get(molecule); // Recipes in which a Mixture containing this Molecule is produced
                    if (recipeProductions != null) recipes.addAll(recipeProductions.stream()
                        .filter(recipe -> recipe.getClass().equals(DestroyJEI.RECIPE_TYPES.get(recipeCategory.getRecipeType()))) // Check for Recipes that match this category
                        .map(recipe -> (T) recipe) // Cast these Recipes (unchecked conversion, but should be okay as we just checked the class)
                        .toList()
                    );
                    break;
                }
                case CATALYST: {
                    break;
                }
                case RENDER_ONLY: {
                    break;
                }
            };
        };
        return recipes;
    };

    @Override
    public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
        return List.of();
        
    };
};
