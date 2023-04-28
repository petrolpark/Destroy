package com.petrolpark.destroy.compat.jei;

import java.util.ArrayList;
import java.util.List;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.compat.jei.category.ReactionCategory;
import com.petrolpark.destroy.fluid.DestroyFluids;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
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
            // Mixtures (which contain a single Molecule)
            || (focus.checkedCast(ForgeTypes.FLUID_STACK).map(fluidFocus -> DestroyFluids.MIXTURE.get().isSame(fluidFocus.getTypedValue().getIngredient().getFluid())).orElse(false) && (focus.getRole() == RecipeIngredientRole.INPUT || focus.getRole() == RecipeIngredientRole.CATALYST))
        ) {
            recipeTypes.add(ReactionCategory.TYPE); // Add the Reaction Recipe type
            recipeTypes.addAll(DestroyJEI.RECIPE_TYPES); // Add all processing Recipes applicable to Mixtures
        };
        return recipeTypes;
    };

    @Override
    @SuppressWarnings("unchecked")
    public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
        List<T> recipes = new ArrayList<>();

        // Mixtures
        // For Mixture inputs (which have a single Molecule required), simply replace this lookup with the lookup for that Molecule
        focus.checkedCast(ForgeTypes.FLUID_STACK).ifPresent(fluidFocus -> { // Check to see if it's a Fluid ingredient
            if (!(focus.getRole() == RecipeIngredientRole.INPUT || focus.getRole() == RecipeIngredientRole.CATALYST)) return; // Ignore this if it's not an input (as outputs can have multiple Molecules)
            FluidStack fluidStack = fluidFocus.getTypedValue().getIngredient();
            if (DestroyFluids.MIXTURE.get().isSame(fluidStack.getFluid())) { // Check to see if it's a Mixture
                Molecule molecule = Molecule.getMolecule(fluidStack.getOrCreateTag().getString("IngredientMolecule")); // Get the Molecule associated with this Mixture ingredient
                if (molecule != null) recipes.addAll(getRecipes(recipeCategory, helpers.getFocusFactory().createFocus(focus.getRole(), MoleculeJEIIngredient.TYPE, molecule)));
            };
        });

        // Molecules
        Molecule molecule = focus.checkedCast(MoleculeJEIIngredient.TYPE).map(moleculeIngredient -> moleculeIngredient.getTypedValue().getIngredient()).orElse(null);
        if (molecule != null) { // Ignore this if we're not dealing with a Molecule
            switch (focus.getRole()) {
                case INPUT: {
                    // Add Reaction Recipes
                    if (recipeCategory instanceof ReactionCategory) molecule.getReactantReactions().forEach(reaction -> recipes.add((T)(ReactionCategory.RECIPES.get(reaction)))); // This is an unchecked conversion but I think it's fine
                    
                    // Add non-Reaction Recipes
                    Destroy.LOGGER.info("Searching for "+molecule.getName(false).getString());
                    List<Recipe<?>> recipeUses = DestroyJEI.MOLECULES_INPUT.get(molecule); // Recipes in which a Mixture containing this Molecule is required
                    if (recipeUses != null) Destroy.LOGGER.info("There are "+recipeUses.size()+" for this");
                    if (recipeUses != null) recipes.addAll(recipeUses.stream()
                        .filter(recipe -> recipe.getClass().equals(recipeCategory.getRecipeType().getRecipeClass())) // Check for Recipes that match this category
                        .map(recipe -> (T) recipe) // Cast these Recipes (unchecked conversion, but should be okay as we just checked the class)
                        .toList()
                    );
                }
                case OUTPUT: {
                    // Add Reaction Recipes
                    if (recipeCategory instanceof ReactionCategory) molecule.getProductReactions().forEach(reaction -> recipes.add((T)(ReactionCategory.RECIPES.get(reaction)))); // This is an unchecked conversion but I think it's fine
                
                    // Add non-Reaction Recipes
                    List<Recipe<?>> recipeProductions = DestroyJEI.MOLECULES_OUTPUT.get(molecule); // Recipes in which a Mixture containing this Molecule is produced
                    if (recipeProductions != null) recipes.addAll(recipeProductions.stream()
                        .filter(recipe -> recipe.getClass().equals(recipeCategory.getRecipeType().getRecipeClass())) // Check for Recipes that match this category
                        .map(recipe -> (T) recipe) // Cast these Recipes (unchecked conversion, but should be okay as we just checked the class)
                        .toList()
                    );
                }
                case CATALYST: {
                    
                }
                case RENDER_ONLY: {

                };
            };
        };
        return recipes;
    };

    @Override
    public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
        return List.of();
        
    };
};
