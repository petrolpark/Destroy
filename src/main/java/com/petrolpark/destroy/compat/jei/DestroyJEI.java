package com.petrolpark.destroy.compat.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.compat.jei.category.AgingCategory;
import com.petrolpark.destroy.compat.jei.category.CentrifugationCategory;
import com.petrolpark.destroy.compat.jei.category.DistillationCategory;
import com.petrolpark.destroy.compat.jei.category.ElectrolysisCategory;
import com.petrolpark.destroy.compat.jei.category.MutationCategory;
import com.petrolpark.destroy.compat.jei.category.ReactionCategory;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.recipe.AgingRecipe;
import com.petrolpark.destroy.recipe.CentrifugationRecipe;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.petrolpark.destroy.recipe.DistillationRecipe;
import com.petrolpark.destroy.recipe.MutationRecipe;
import com.petrolpark.destroy.recipe.ReactionRecipe;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory.Info;
import com.simibubi.create.content.contraptions.processing.BasinRecipe;
import com.simibubi.create.foundation.utility.recipe.IRecipeTypeInfo;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

@JeiPlugin
public class DestroyJEI implements IModPlugin {

    /**
     * All Create and Destroy {@link mezz.jei.api.recipe.RecipeType Recipe Types}.
     * Create's Recipe Types are not exposed by default, meaning we have to access them through a {@link com.petrolpark.destroy.mixin.CreateRecipeCategoryMixin mixin} store them here.
     */ 
    public static final Set<RecipeType<?>> RECIPE_TYPES = new HashSet<>();
    /**
     * A map of Molecules to the Recipes in which they are inputs.
     * This does not include {@link com.petrolpark.destroy.chemistry.Reaction Reactions}.
     */
    public static final Map<Molecule, List<Recipe<?>>> MOLECULES_INPUT = new HashMap<>();
    /**
     * A map of Molecules to the Recipes in which they are outputs.
     * This does not include {@link com.petrolpark.destroy.chemistry.Reaction Reactions}.
     */
    public static final Map<Molecule, List<Recipe<?>>> MOLECULES_OUTPUT = new HashMap<>();

    private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();

    @SuppressWarnings("unused")
    private void loadCategories() {
        allCategories.clear();

        CreateRecipeCategory<?>

        aging = builder(AgingRecipe.class)
            .addTypedRecipes(DestroyRecipeTypes.AGING)
            .catalyst(DestroyBlocks.AGING_BARREL::get)
            .itemIcon(DestroyBlocks.AGING_BARREL.get())
            .emptyBackground(177, 69)
            .build("aging", AgingCategory::new),

        centrifugation = builder(CentrifugationRecipe.class)
            .addTypedRecipes(DestroyRecipeTypes.CENTRIFUGATION)
            .catalyst(DestroyBlocks.CENTRIFUGE::get)
            .itemIcon(DestroyBlocks.CENTRIFUGE.get())
            .emptyBackground(120, 115)
            .build("centrifugation", CentrifugationCategory::new),

        distillation = builder(DistillationRecipe.class)
            .addTypedRecipes(DestroyRecipeTypes.DISTILLATION)
            .catalyst(DestroyBlocks.BUBBLE_CAP::get)
            .itemIcon(DestroyBlocks.BUBBLE_CAP.get())
            .emptyBackground(100, 100)
            .build("distillation", DistillationCategory::new),

        electrolysis = builder(BasinRecipe.class)
            .addTypedRecipes(DestroyRecipeTypes.ELECTROLYSIS)
            .catalyst(DestroyBlocks.DYNAMO::get)
            .catalyst(AllBlocks.BASIN::get)
            .doubleItemIcon(DestroyBlocks.DYNAMO.get(), AllBlocks.BASIN.get())
            .emptyBackground(177, 85)
            .build("electrolysis", ElectrolysisCategory::new),
        
        mutation = builder(MutationRecipe.class)
            .addRecipes(() -> MutationCategory.RECIPES)
            .catalyst(DestroyItems.HYPERACCUMULATING_FERTILIZER::get)
            .itemIcon(DestroyItems.HYPERACCUMULATING_FERTILIZER.get())
            .emptyBackground(120, 125)
            .build("mutation", MutationCategory::new),

        reaction = builder(ReactionRecipe.class)
            .addRecipes(ReactionCategory.RECIPES::values)
            .catalyst(AllBlocks.MECHANICAL_MIXER::get)
            .catalyst(AllBlocks.BASIN::get)
            .itemIcon(DestroyItems.MOLECULE_DISPLAY.get())
            .emptyBackground(180, 125)
            .build("reaction", ReactionCategory::new);

    };

    @Override
    public ResourceLocation getPluginUid() {
      return Destroy.asResource("jei_plugin");
    };

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories();
        registration.addRecipeCategories(allCategories.toArray(IRecipeCategory[]::new));
    };

    @Override
	public void registerRecipes(IRecipeRegistration registration) {
        allCategories.forEach(c -> c.registerRecipes(registration));
	};

    @Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		allCategories.forEach(c -> c.registerCatalysts(registration));
	};
    
    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(MoleculeJEIIngredient.TYPE, Molecule.MOLECULES.values(), MoleculeJEIIngredient.HELPER, MoleculeJEIIngredient.RENDERER);
    };

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        registration.addRecipeManagerPlugin(DestroyRecipeManagerPlugins.MOLECULE_RECIPES_PLUGIN);
    };

    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    };
    
    /**
     * Used to generate JEI Categories for Destroy.
     * Basically all copied from the {@link com.simibubi.create.compat.jei.CreateJEI.CategoryBuilder Create source code}.
     */
    private class CategoryBuilder<T extends Recipe<?>> {
        private Class<? extends T> recipeClass;

        private IDrawable background;
		private IDrawable icon;

        private final List<Consumer<List<T>>> recipeListConsumers = new ArrayList<>();
		private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();

        public CategoryBuilder(Class<? extends T> recipeClass) {
			this.recipeClass = recipeClass;
		};

        /**
         * Adds a List of Recipes to this Category.
         * @param collection The List of Recipes
         * @return This Category Builder
         */
        public CategoryBuilder<T> addRecipes(Supplier<Collection<? extends T>> collection) {
			recipeListConsumers.add(recipes -> recipes.addAll(collection.get()));
            Destroy.LOGGER.info("Loaded " + collection.get().size()+ " recipes of type " + recipeClass.getSimpleName()+ ".");
            return this;
		};

        /**
         * Adds all Recipes of a given Recipe Type to this Category.
         * @param recipeTypeEntry The Recipe Type
         * @return This Category Builder
         */
        public CategoryBuilder<T> addTypedRecipes(IRecipeTypeInfo recipeTypeEntry) {
            recipeListConsumers.add(recipes -> CreateJEI.<T>consumeTypedRecipes(recipes::add, recipeTypeEntry.getType()));
            return this;
        };

        /**
         * Adds a given Item as a Catalyst for all Recipes of this Category.
         * Useful for adding the required machines for a Category of Recipe.
         * @param itemSupplier A Supplier of the catalyst Item
         * @return This Category Builder
         */
        public CategoryBuilder<T> catalyst(Supplier<ItemLike> itemSupplier) {
            catalysts.add(() -> new ItemStack(itemSupplier.get().asItem()));
            return this;
        };

        /**
         * Sets the given Item as the icon for this Category.
         * @param item Typically this will be the machine required for this Type of Recipe
         * @return This Category Builder
         */
        public CategoryBuilder<T> itemIcon(ItemLike item) {
			this.icon = new ItemIcon(() -> new ItemStack(item));
			return this;
		};

        /**
         * Sets the given pair of Items as the icon for this Category.
         * @param bigItem Typically this will be the machine required for this Type of Recipe
         * @param smallItem Typically this will be a way to differentiate this use from other uses of the same machine
         * @return This Category Builder
         */
        public CategoryBuilder<T> doubleItemIcon(ItemLike bigItem, ItemLike smallItem) {
            this.icon = new DoubleItemIcon(() -> new ItemStack(bigItem), () -> new ItemStack(smallItem));
            return this;
        };

        /**
         * Sets the size of the Background for this Category.
         * @param width
         * @param height
         * @return This Category Builder
         */
        public CategoryBuilder<T> emptyBackground(int width, int height) {
			this.background = new EmptyBackground(width, height);
			return this;
		};

        /**
         * Builds this Category.
         * @param name The Resource Location (e.g. for use in language file)
         * @param factory Initializer of the Category class
         * @return This Category
         */
        public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
            Supplier<List<T>> recipesSupplier = () -> {
                List<T> recipes = new ArrayList<>();
                for (Consumer<List<T>> consumer : recipeListConsumers) {
                    consumer.accept(recipes);
                };
                return recipes;
            };
            Info<T> info = new Info<T>(
                new mezz.jei.api.recipe.RecipeType<>(Destroy.asResource(name), recipeClass),
                DestroyLang.translate("recipe."+name).component(),
                background,
                icon,
                recipesSupplier,
                catalysts
            );

            CreateRecipeCategory<T> category = factory.create(info);
            allCategories.add(category);

            return category;
        };
    };
}
