package com.petrolpark.destroy.compat.jei;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.compat.jei.category.AgingCategory;
import com.petrolpark.destroy.compat.jei.category.CentrifugationCategory;
import com.petrolpark.destroy.compat.jei.category.ChargingCategory;
import com.petrolpark.destroy.compat.jei.category.DestroyRecipeCategory;
import com.petrolpark.destroy.compat.jei.category.DistillationCategory;
import com.petrolpark.destroy.compat.jei.category.ElectrolysisCategory;
import com.petrolpark.destroy.compat.jei.category.ExtrusionCategory;
import com.petrolpark.destroy.compat.jei.category.GenericReactionCategory;
import com.petrolpark.destroy.compat.jei.category.ITickableCategory;
import com.petrolpark.destroy.compat.jei.category.MutationCategory;
import com.petrolpark.destroy.compat.jei.category.ObliterationCategory;
import com.petrolpark.destroy.compat.jei.category.ReactionCategory;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.recipe.AgingRecipe;
import com.petrolpark.destroy.recipe.CentrifugationRecipe;
import com.petrolpark.destroy.recipe.ChargingRecipe;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.petrolpark.destroy.recipe.DistillationRecipe;
import com.petrolpark.destroy.recipe.ElectrolysisRecipe;
import com.petrolpark.destroy.recipe.ExtendedDurationFireworkRocketRecipe;
import com.petrolpark.destroy.recipe.ExtrusionRecipe;
import com.petrolpark.destroy.recipe.MutationRecipe;
import com.petrolpark.destroy.recipe.ObliterationRecipe;
import com.petrolpark.destroy.recipe.ReactionRecipe;
import com.petrolpark.destroy.recipe.ReactionRecipe.GenericReactionRecipe;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory.Info;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@JeiPlugin
public class DestroyJEI implements IModPlugin {

    public static Optional<IJeiRuntime> jeiRuntime = Optional.empty();

    /**
     * All Create and Destroy {@link mezz.jei.api.recipe.RecipeType Recipe Types} which can produce or consume Mixtures, mapped to the class of Recipe which those Recipe Types describe.
     * Create's Recipe Types are not exposed by default, meaning we have to access them through a {@link com.petrolpark.destroy.mixin.CreateRecipeCategoryMixin mixin} and store them here.
     */ 
    public static final Map<RecipeType<?>, Class<? extends Recipe<?>>> RECIPE_TYPES = new HashMap<>();
    /**
     * A map of Molecules to the Recipes in which they are inputs.
     * This does not include {@link com.petrolpark.destroy.chemistry.Reaction Reactions}.
     */
    public static final Map<Molecule, Set<Recipe<?>>> MOLECULES_INPUT = new HashMap<>();
    /**
     * A map of Molecules to the Recipes in which they are outputs.
     * This does not include {@link com.petrolpark.destroy.chemistry.Reaction Reactions}.
     */
    public static final Map<Molecule, Set<Recipe<?>>> MOLECULES_OUTPUT = new HashMap<>();
    /**
     * Whether Recipes have not yet been added to {@link DestroyJEI#MOLECULES_INPUT} and {@link DestroyJEI#MOLECULES_OUTPUT}.
     */
    public static boolean MOLECULE_RECIPES_NEED_PROCESSING = true;

    private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();
    private static final List<ITickableCategory> tickingCategories = new ArrayList<>();

    @SuppressWarnings("unused")
    private void loadCategories() {
        allCategories.clear();

        CreateRecipeCategory<?>

        aging = builder(AgingRecipe.class)
            .addTypedRecipes(DestroyRecipeTypes.AGING)
            .acceptsMixtures()
            .catalyst(DestroyBlocks.AGING_BARREL::get)
            .itemIcon(DestroyBlocks.AGING_BARREL.get())
            .emptyBackground(177, 86)
            .build("aging", AgingCategory::new),

        centrifugation = builder(CentrifugationRecipe.class)
            .addTypedRecipes(DestroyRecipeTypes.CENTRIFUGATION)
            .acceptsMixtures()
            .catalyst(DestroyBlocks.CENTRIFUGE::get)
            .itemIcon(DestroyBlocks.CENTRIFUGE.get())
            .emptyBackground(120, 115)
            .build("centrifugation", CentrifugationCategory::new),

        charging = builder(ChargingRecipe.class)
			.addTypedRecipes(DestroyRecipeTypes.CHARGING)
			.catalyst(DestroyBlocks.DYNAMO::get)
			.itemIcon(DestroyBlocks.DYNAMO)
			.emptyBackground(177, 70)
			.build("charging", ChargingCategory::new),

        distillation = builder(DistillationRecipe.class)
            .addTypedRecipes(DestroyRecipeTypes.DISTILLATION)
            .acceptsMixtures()
            .catalyst(DestroyBlocks.BUBBLE_CAP::get)
            .itemIcon(DestroyBlocks.BUBBLE_CAP.get())
            .emptyBackground(123, 125)
            .build("distillation", DistillationCategory::new),

        extrusion = builder(ExtrusionRecipe.class)
            .addRecipes(() -> ExtrusionRecipe.RECIPES)
            .catalyst(DestroyBlocks.EXTRUSION_DIE::get)
            .itemIcon(DestroyBlocks.EXTRUSION_DIE.get())
            .emptyBackground(177, 55)
            .build("extrusion", ExtrusionCategory::new),
        
        mutation = builder(MutationRecipe.class)
            .addRecipes(() -> MutationCategory.RECIPES)
            .catalyst(DestroyItems.HYPERACCUMULATING_FERTILIZER::get)
            .itemIcon(DestroyItems.HYPERACCUMULATING_FERTILIZER.get())
            .emptyBackground(120, 125)
            .build("mutation", MutationCategory::new),

        obliteration = builder(ObliterationRecipe.class)
            .addTypedRecipes(DestroyRecipeTypes.OBLITERATION)
            .itemIcon(DestroyBlocks.NITROCELLULOSE_BLOCK.get())
            .emptyBackground(177, 70)
            .build("obliteration", ObliterationCategory::new),

        reaction = builder(ReactionRecipe.class)
            .addRecipes(ReactionCategory.RECIPES::values)
            // Doesn't accept Mixtures as Reactions involve Molecules, not Mixtures.
            .catalyst(AllBlocks.MECHANICAL_MIXER::get)
            .catalyst(AllBlocks.BASIN::get)
            .catalyst(DestroyBlocks.VAT_CONTROLLER::get)
            .itemIcon(DestroyItems.MOLECULE_DISPLAY.get())
            .emptyBackground(180, 125)
            .build("reaction", ReactionCategory::new),

        genericReaction = builder(GenericReactionRecipe.class)
            .addRecipes(GenericReactionCategory.RECIPES::values)
            // Doesn't accept Mixtures as Generic Reactions involve Molecules, not Mixtures.
            .catalyst(AllBlocks.MECHANICAL_MIXER::get)
            .catalyst(AllBlocks.BASIN::get)
            .catalyst(DestroyBlocks.VAT_CONTROLLER::get)
            .itemIcon(DestroyItems.MOLECULE_DISPLAY.get())
            .emptyBackground(180, 125)
            .build("generic_reaction", GenericReactionCategory::new),

        electrolysis = builder(BasinRecipe.class)
            .addTypedRecipes(DestroyRecipeTypes.ELECTROLYSIS)
            .acceptsMixtures(ElectrolysisRecipe.class)
            .catalyst(DestroyBlocks.DYNAMO::get)
            .catalyst(AllBlocks.BASIN::get)
            .doubleItemIcon(DestroyBlocks.DYNAMO.get(), AllBlocks.BASIN.get())
            .emptyBackground(177, 85)
            .build("electrolysis", (info, helpers) -> new ElectrolysisCategory(info));

        DestroyJEI.MOLECULE_RECIPES_NEED_PROCESSING = false;
    };

    @Override
    public ResourceLocation getPluginUid() {
      return Destroy.asResource("jei_plugin");
    };

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories();
        CategoryBuilder.helpers = registration.getJeiHelpers();
        registration.addRecipeCategories(allCategories.toArray(IRecipeCategory[]::new));
    };

    @Override
	public void registerRecipes(IRecipeRegistration registration) {
        allCategories.forEach(c -> c.registerRecipes(registration));

        registration.addRecipes(RecipeTypes.CRAFTING, ExtendedDurationFireworkRocketRecipe.exampleRecipes());
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
    public <T> void registerFluidSubtypes(ISubtypeRegistration registration, IPlatformFluidHelper<T> platformFluidHelper) {
        registration.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, DestroyFluids.MIXTURE.get().getSource(), new MixtureFluidSubtypeInterpreter());
        registration.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, DestroyFluids.MIXTURE.get().getFlowing(), new MixtureFluidSubtypeInterpreter());
    };

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        registration.addRecipeManagerPlugin(new DestroyRecipeManagerPlugin(registration.getJeiHelpers()));
    };

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        jeiRuntime = Optional.of(runtime);
    };

    public static void tick() {
        tickingCategories.forEach(ITickableCategory::tick);
    };

    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    };
    
    /**
     * Used to generate JEI Categories for Destroy.
     * Basically all copied from the {@link com.simibubi.create.compat.jei.CreateJEI.CategoryBuilder Create source code}.
     */
    private class CategoryBuilder<T extends Recipe<?>> {
        private static IJeiHelpers helpers;

        private Class<? extends T> recipeClass;

        private IDrawable background;
		private IDrawable icon;
        private Class<? extends T> recipeClassForMixtures;

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
         * Marks this Category as being able to have <em>Mixtures</em> as in its outputs and/or inputs.
         * This should essentially be all Categories for Fluid-accepting Recipes.
         * If this is not flagged, Recipes can still include Mixtures, but they will not show up
         * when searching Recipes for/including Molecules.
         * @return This Category Builder
         */
        public CategoryBuilder<T> acceptsMixtures() {
            this.recipeClassForMixtures = recipeClass;
            return this;
        };

        /**
         * Marks this Category as being able to have <em>Mixtures</em> as in its outputs and/or inputs.
         * This should essentially be all Categories for Fluid-accepting Recipes.
         * If this is not flagged, Recipes can still include Mixtures, but they will not show up
         * when searching Recipes for/including Molecules.
         * @param actualRecipeClass The class of Recipes which this Category actually describes (this is
         * not necessarily the same as the given {@link CategoryBuilder#recipeClass Recipe Class}, for
         * example if this Category extends {@link com.simibubi.create.compat.jei.category.BasinCategory BasinCategory})
         * @return This Category Builder
         */
        public CategoryBuilder<T> acceptsMixtures(Class<? extends T> actualRecipeClass) {
            this.recipeClassForMixtures = actualRecipeClass;
            return this;
        };

        /**
         * Builds this Category.
         * @param name The Resource Location (e.g. for use in language file)
         * @param factory Initializer of the Category class
         * @return This Category
         */
        public CreateRecipeCategory<T> build(String name, DestroyRecipeCategory.Factory<T> factory) {
            Supplier<List<T>> recipesSupplier = () -> {
                List<T> recipes = new ArrayList<>();
                for (Consumer<List<T>> consumer : recipeListConsumers) {
                    consumer.accept(recipes);
                };
                return recipes;
            };

            mezz.jei.api.recipe.RecipeType<T> type = new mezz.jei.api.recipe.RecipeType<>(Destroy.asResource(name), recipeClass);

            Info<T> info = new Info<T>(
                type,
                DestroyLang.translate("recipe."+name).component(),
                background,
                icon,
                recipesSupplier,
                catalysts
            );

            CreateRecipeCategory<T> category = factory.create(info, helpers);
            allCategories.add(category);

            if (category instanceof ITickableCategory tickableCategory) tickingCategories.add(tickableCategory);

            if (recipeClassForMixtures != null) {
                RECIPE_TYPES.put(type, recipeClassForMixtures);
            };

            return category;
        };
    };

    public class ClientEvents {

        @SubscribeEvent
        public static void onTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                tick();
            };
        };
    };
};
