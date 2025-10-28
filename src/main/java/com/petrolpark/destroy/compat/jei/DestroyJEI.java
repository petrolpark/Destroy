package com.petrolpark.destroy.compat.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.petrolpark.compat.jei.category.builder.PetrolparkCategoryBuilder;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyArmorMaterials;
import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.DestroyFluids;
import com.petrolpark.destroy.DestroyItems;
import com.petrolpark.destroy.DestroyRecipeTypes;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.compat.jei.animation.ArcFurnaceIcon;
import com.petrolpark.destroy.compat.jei.category.AgingCategory;
import com.petrolpark.destroy.compat.jei.category.ArcFurnaceCategory;
import com.petrolpark.destroy.compat.jei.category.CartographyTableCategory;
import com.petrolpark.destroy.compat.jei.category.CartographyTableCategory.CartographyTableRecipe;
import com.petrolpark.destroy.compat.jei.category.CentrifugationCategory;
import com.petrolpark.destroy.compat.jei.category.ChargingCategory;
import com.petrolpark.destroy.compat.jei.category.DistillationCategory;
import com.petrolpark.destroy.compat.jei.category.ElectrolysisCategory;
import com.petrolpark.destroy.compat.jei.category.ElementTankFillingCategory;
import com.petrolpark.destroy.compat.jei.category.ExtrusionCategory;
import com.petrolpark.destroy.compat.jei.category.FlameRetardantApplicationCategory;
import com.petrolpark.destroy.compat.jei.category.GenericReactionCategory;
import com.petrolpark.destroy.compat.jei.category.GlassblowingCategory;
import com.petrolpark.destroy.compat.jei.category.MixableExplosiveCategory;
import com.petrolpark.destroy.compat.jei.category.MixableExplosiveCategory.MixableExplosiveRecipe;
import com.petrolpark.destroy.compat.jei.category.MixtureConversionCategory;
import com.petrolpark.destroy.compat.jei.category.MutationCategory;
import com.petrolpark.destroy.compat.jei.category.ObliterationCategory;
import com.petrolpark.destroy.compat.jei.category.ReactionCategory;
import com.petrolpark.destroy.compat.jei.category.SievingCategory;
import com.petrolpark.destroy.compat.jei.category.TappingCategory;
import com.petrolpark.destroy.compat.jei.category.VatMaterialCategory;
import com.petrolpark.destroy.compat.jei.category.VatMaterialCategory.VatMaterialRecipe;
import com.petrolpark.destroy.compat.jei.recipemanager.ChemicalSpeciesRecipeManagerPlugin;
import com.petrolpark.destroy.compat.jei.recipemanager.FireproofingRecipeManagerPlugin;
import com.petrolpark.destroy.compat.jei.recipemanager.ItemReverseReactionRecipeManagerPlugin;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.content.processing.ageing.AgeingRecipe;
import com.petrolpark.destroy.content.processing.centrifuge.CentrifugationRecipe;
import com.petrolpark.destroy.content.processing.centrifuge.potion.PotionSeparationRecipes;
import com.petrolpark.destroy.content.processing.distillation.DistillationRecipe;
import com.petrolpark.destroy.content.processing.dynamo.ChargingRecipe;
import com.petrolpark.destroy.content.processing.dynamo.ElectrolysisRecipe;
import com.petrolpark.destroy.content.processing.dynamo.arcfurnace.ArcFurnaceRecipe;
import com.petrolpark.destroy.content.processing.extrusion.ExtrusionRecipe;
import com.petrolpark.destroy.content.processing.glassblowing.GlassblowingRecipe;
import com.petrolpark.destroy.content.processing.phytomining.PhytominingRecipe;
import com.petrolpark.destroy.content.processing.sieve.SievingRecipe;
import com.petrolpark.destroy.content.processing.treetap.TappingRecipe;
import com.petrolpark.destroy.content.product.fireretardant.FlameRetardantApplicationRecipe;
import com.petrolpark.destroy.content.product.periodictable.ElementTankFillingRecipe;
import com.petrolpark.destroy.content.redstone.programmer.RedstoneProgrammerScreen;
import com.petrolpark.destroy.core.chemistry.recipe.MixtureConversionRecipe;
import com.petrolpark.destroy.core.chemistry.recipe.ReactionRecipe;
import com.petrolpark.destroy.core.chemistry.recipe.ReactionRecipe.GenericReactionRecipe;
import com.petrolpark.destroy.core.explosion.ExtendedDurationFireworkRocketRecipe;
import com.petrolpark.destroy.core.explosion.ObliterationRecipe;
import com.petrolpark.destroy.core.explosion.mixedexplosive.MixedExplosiveBlockItem;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.createmod.catnip.data.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

@JeiPlugin
public class DestroyJEI implements IModPlugin {

    public static Optional<IJeiRuntime> jeiRuntime = Optional.empty();

    /**
     * All Create and Destroy {@link mezz.jei.api.recipe.RecipeType Recipe Types} which can produce or consume Mixtures, mapped to the class of Recipe which those Recipe Types describe.
     * Create's Recipe Types are not exposed by default, meaning we have to access them through a {@link com.petrolpark.destroy.mixin.compat.jei.CreateRecipeCategoryMixin mixin} and store them here.
     */ 
    public static final Map<mezz.jei.api.recipe.RecipeType<?>, Class<? extends Recipe<?>>> MIXTURE_APPLICABLE_RECIPE_TYPES = new HashMap<>();
    /**
     * A map of Molecules to the Recipes in which they are inputs.
     * This does not include {@link com.petrolpark.destroy.chemistry.legacy.LegacyReaction Reactions}.
     */
    public static final Map<LegacySpecies, List<Recipe<?>>> MOLECULES_INPUT = new HashMap<>();
    /**
     * A map of Molecules to the Recipes in which they are outputs.u
     * This does not include {@link com.petrolpark.destroy.chemistry.legacy.LegacyReaction Reactions}.
     */
    public static final Map<LegacySpecies, List<Recipe<?>>> MOLECULES_OUTPUT = new HashMap<>();
    /**
     * Whether Recipes have not yet been added to {@link DestroyJEI#MOLECULES_INPUT} and {@link DestroyJEI#MOLECULES_OUTPUT}.
     */
    public static boolean MOLECULE_RECIPES_NEED_PROCESSING = true;

    private static final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();

    public static CreateRecipeCategory<?> fireproofing;

    @SuppressWarnings("unused")
    private void loadCategories() {
        allCategories.clear();

        CreateRecipeCategory<?>

        aging = builder(AgeingRecipe.class)
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

        potion_centrifugation = builder(CentrifugationRecipe.class)
            .addRecipes(() -> PotionSeparationRecipes.ALL.values())
            .enableIfCreateConfig(c -> c.allowBrewingInMixer)
            .catalyst(DestroyBlocks.CENTRIFUGE::get)
            .doubleItemIcon(DestroyBlocks.CENTRIFUGE.get(), Items.BREWING_STAND)
            .emptyBackground(120, 115)
            .build("potion_centrifugation", CentrifugationCategory::new),

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
        
        mutation = builder(PhytominingRecipe.class)
            .addRecipes(() -> MutationCategory.RECIPES)
            .catalyst(DestroyItems.HYPERACCUMULATING_FERTILIZER::get)
            .itemIcon(DestroyItems.HYPERACCUMULATING_FERTILIZER.get())
            .emptyBackground(120, 125)
            .build("mutation", MutationCategory::new),

        obliteration = builder(ObliterationRecipe.class)
            .addTypedRecipes(DestroyRecipeTypes.OBLITERATION)
            .itemIcon(MixedExplosiveBlockItem::getExampleItemStack)
            .emptyBackground(177, 70)
            .build("obliteration", ObliterationCategory::new),

        reaction = builder(ReactionRecipe.class)
            .addRecipes(ReactionCategory.RECIPES::values)
            // Doesn't accept Mixtures as Reactions involve Molecules, not Mixtures.
            .reactionCatalysts()
            .itemIcon(DestroyItems.MOLECULE_DISPLAY.get())
            .emptyBackground(180, 125)
            .build("reaction", ReactionCategory::new),

        genericReaction = builder(GenericReactionRecipe.class)
            .addRecipes(GenericReactionCategory.RECIPES::values)
            // Doesn't accept Mixtures as Generic Reactions involve Molecules, not Mixtures.
            .reactionCatalysts()
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
            .build("electrolysis", (info, helpers) -> new ElectrolysisCategory(info)),

        tapping = builder(TappingRecipe.class)
            .addRecipes(() -> TappingCategory.RECIPES)
            .acceptsMixtures()
            .catalyst(DestroyBlocks.TREE_TAP::get)
            .itemIcon(DestroyBlocks.TREE_TAP.get())
            .emptyBackground(177, 70)
            .build("tapping", TappingCategory::new),

        mixture_conversion = builder(MixtureConversionRecipe.class)
            .addTypedRecipes(DestroyRecipeTypes.MIXTURE_CONVERSION)
            .reactionCatalysts()
            .doubleItemIcon(DestroyItems.TEST_TUBE.get(), Items.WATER_BUCKET)
            .emptyBackground(125, 20)
            .build("mixture_conversion", MixtureConversionCategory::new),

        cartography_table = builder(CartographyTableRecipe.class)
            .addRecipes(CartographyTableCategory::getAllRecipes)
            .catalyst(() -> Items.CARTOGRAPHY_TABLE)
            .itemIcon(Items.CARTOGRAPHY_TABLE)
            .emptyBackground(125, 20)
            .build("cartography_table", CartographyTableCategory::new),

        vat_material = builder(VatMaterialRecipe.class)
            .addRecipes(VatMaterialCategory::getAllRecipes)
            .catalyst(DestroyBlocks.VAT_CONTROLLER::get)
            .itemIcon(DestroyBlocks.VAT_CONTROLLER.get())
            .emptyBackground(180, 83)
            .build("vat_material", VatMaterialCategory::new),

        mixable_explosive = builder(MixableExplosiveRecipe.class)
            .addRecipes(MixableExplosiveCategory::getAllRecipes)
            .catalysts(DestroyJEISetup.CUSTOM_MIX_EXPLOSIVES)
            .doubleItemIcon(MixedExplosiveBlockItem::getExampleItemStack, () -> new ItemStack(Items.GUNPOWDER))
            .emptyBackground(180, 121)
            .build("mixable_explosive", MixableExplosiveCategory::new),

        sieving = builder(SievingRecipe.class)
            .addTypedRecipes(DestroyRecipeTypes.SIEVING)
            .catalyst(DestroyBlocks.MECHANICAL_SIEVE::get)
            .itemIcon(DestroyBlocks.MECHANICAL_SIEVE)
            .emptyBackground(150, 90)
            .build("sieving", SievingCategory::new),

        glassblowing = builder(GlassblowingRecipe.class)
            .addTypedRecipes(DestroyRecipeTypes.GLASSBLOWING)
            .catalyst(DestroyBlocks.BLOWPIPE::get)
            .itemIcon(DestroyBlocks.BLOWPIPE)
            .emptyBackground(125, 50)
            .build("glassblowing", GlassblowingCategory::new),

        arc_furnace = builder(BasinRecipe.class)
            .addTypedRecipes(DestroyRecipeTypes.ARC_FURNACE)
            .acceptsMixtures(ArcFurnaceRecipe.class)
            .catalyst(DestroyBlocks.DYNAMO::get)
            .catalyst(DestroyBlocks.CARBON_FIBER_BLOCK::get)
            .catalyst(AllBlocks.BASIN::get)
            .arcFurnaceIcon(() -> ItemStack.EMPTY)
            .emptyBackground(177, 85)
            .build("arc_furnace", (info, helpers) -> new ArcFurnaceCategory(info)),

        arc_furnace_blasting = builder(BasinRecipe.class)
            .addTypedRecipesIf(() -> RecipeType.BLASTING, ArcFurnaceCategory::toBasinRecipe, r -> DestroyAllConfigs.SERVER.blocks.arcFurnaceAllowsBlasting.get())
            .catalyst(DestroyBlocks.DYNAMO::get)
            .catalyst(DestroyBlocks.CARBON_FIBER_BLOCK::get)
            .catalyst(AllBlocks.BASIN::get)
            .arcFurnaceIcon(() -> new ItemStack(Items.BLAST_FURNACE))
            .emptyBackground(177, 85)
            .build("arc_furnace_blasting", (info, helpers) -> new ArcFurnaceCategory(info)),

        arc_furnace_smelting = builder(BasinRecipe.class)
            .addTypedRecipesIf(() -> RecipeType.SMELTING, ArcFurnaceCategory::toBasinRecipe, r -> DestroyAllConfigs.SERVER.blocks.arcFurnaceAllowsSmelting.get())
            .catalyst(DestroyBlocks.DYNAMO::get)
            .catalyst(DestroyBlocks.CARBON_FIBER_BLOCK::get)
            .catalyst(AllBlocks.BASIN::get)
            .arcFurnaceIcon(() -> new ItemStack(Items.FURNACE))
            .emptyBackground(177, 85)
            .build("arc_furnace_smelting", (info, helpers) -> new ArcFurnaceCategory(info)),

        element_tank_filling = builder(ElementTankFillingRecipe.class)
            .addTypedRecipes(DestroyRecipeTypes.ELEMENT_TANK_FILLING)
            .itemIcon(DestroyBlocks.ELEMENT_TANK)
            .emptyBackground(125, 40)
            .build("element_tank_filling", ElementTankFillingCategory::new);

        fireproofing = builder(FlameRetardantApplicationRecipe.class)
            .addTypedRecipes(DestroyRecipeTypes.FLAME_RETARDANT_APPLICATION)
            .doubleItemIcon(AllBlocks.SPOUT::asStack, () -> new ItemStack(Items.NETHERITE_INGOT))
            .catalyst(AllBlocks.SPOUT::get)
            .emptyBackground(177, 70)
			.build("fireproofing", FlameRetardantApplicationCategory::new);

        DestroyJEI.MOLECULE_RECIPES_NEED_PROCESSING = false;
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

        // Example crafts
        registration.addRecipes(RecipeTypes.CRAFTING, ExtendedDurationFireworkRocketRecipe.exampleRecipes());

        // Anvil repairs
        registration.addRecipes(RecipeTypes.ANVIL, getAnvilRepairs(registration));
	};

    @Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		allCategories.forEach(c -> c.registerCatalysts(registration));
	};
    
    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(MoleculeJEIIngredient.TYPE, LegacySpecies.MOLECULES.values(), MoleculeJEIIngredient.HELPER, MoleculeJEIIngredient.RENDERER);
    };

    @Override
    public <T> void registerFluidSubtypes(ISubtypeRegistration registration, IPlatformFluidHelper<T> platformFluidHelper) {
        registration.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, DestroyFluids.MIXTURE.get().getSource(), new MixtureFluidSubtypeInterpreter());
        registration.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, DestroyFluids.MIXTURE.get().getFlowing(), new MixtureFluidSubtypeInterpreter());
    };

    @SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGhostIngredientHandler(RedstoneProgrammerScreen.class, new DestroyGhostIngredientHandler());
        registration.addGlobalGuiHandler(new ExtendedInventoryGuiHandler());
	};

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        registration.addRecipeManagerPlugin(new ChemicalSpeciesRecipeManagerPlugin(registration.getJeiHelpers()));
        registration.addRecipeManagerPlugin(new FireproofingRecipeManagerPlugin());
        registration.addRecipeManagerPlugin(new ItemReverseReactionRecipeManagerPlugin());
    };

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        jeiRuntime = Optional.of(runtime);
    };

    private <T extends Recipe<?>> DestroyCategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new DestroyCategoryBuilder<>(recipeClass);
    };

    public static class DestroyCategoryBuilder<R extends Recipe<?>> extends PetrolparkCategoryBuilder<R, DestroyCategoryBuilder<R>> {

        private Class<? extends R> recipeClassForMixtures;
        
        public DestroyCategoryBuilder(Class<? extends R> recipeClass) {
            super(Destroy.MOD_ID, recipeClass, allCategories::add);
        };

        /**
         * Adds all the Items which can be used to do chemical Reactions as catalysts for this Category.
         * @return This Category Builder
         */
        public DestroyCategoryBuilder<R> reactionCatalysts() {
            return this
                .catalyst(AllBlocks.MECHANICAL_MIXER::get)
                .catalyst(AllBlocks.BASIN::get)
                .catalyst(DestroyBlocks.VAT_CONTROLLER::get);
        };

        /**
         * Set an Arc Furnace as the icon for this Category
         * @param item Small Item rendered to the bottom right of the Arc Furnace
         * @return This Category Builder
         */
        public DestroyCategoryBuilder<R> arcFurnaceIcon(Supplier<ItemStack> item) {
            icon = new ArcFurnaceIcon(item);
            return this;
        };

        /**
         * Marks this Category as being able to have <em>Mixtures</em> as in its outputs and/or inputs.
         * This should essentially be all Categories for Fluid-accepting Recipes.
         * If this is not flagged, Recipes can still include Mixtures, but they will not show up
         * when searching Recipes for/including Molecules.
         * @return This Category Builder
         */
        public DestroyCategoryBuilder<R> acceptsMixtures() {
            this.recipeClassForMixtures = recipeClass;
            return this;
        };

        /**
         * Marks this Category as being able to have <em>Mixtures</em> as in its outputs and/or inputs.
         * This should essentially be all Categories for Fluid-accepting Recipes.
         * If this is not flagged, Recipes can still include Mixtures, but they will not show up
         * when searching Recipes for/including Molecules.
         * @param actualRecipeClass The class of Recipes which this Category actually describes (this is
         * not necessarily the same as the given {@link TempCategoryBuilder#recipeClass Recipe Class}, for
         * example if this Category extends {@link com.simibubi.create.compat.jei.category.BasinCategory BasinCategory})
         * @return This Category Builder
         */
        public DestroyCategoryBuilder<R> acceptsMixtures(Class<? extends R> actualRecipeClass) {
            this.recipeClassForMixtures = actualRecipeClass;
            return this;
        };

        @Override
        protected void finalizeBuilding(mezz.jei.api.recipe.RecipeType<R> type, CreateRecipeCategory<R> category, Class<? extends R> trueClass) {
            if (recipeClassForMixtures != null) {
                MIXTURE_APPLICABLE_RECIPE_TYPES.put(type, recipeClassForMixtures);
            };
        };

    };

    private static List<IJeiAnvilRecipe> getAnvilRepairs(IRecipeRegistration registration) {
        List<Pair<Item, Ingredient>> repairables = List.of(
            Pair.of(DestroyItems.HAZMAT_SUIT.get(), DestroyArmorMaterials.HAZMAT.getRepairIngredient()),
            Pair.of(DestroyItems.HAZMAT_LEGGINGS.get(), DestroyArmorMaterials.HAZMAT.getRepairIngredient()),
            Pair.of(DestroyItems.WELLINGTON_BOOTS.get(), DestroyArmorMaterials.HAZMAT.getRepairIngredient()),

            Pair.of(DestroyItems.GAS_MASK.get(), DestroyItems.GAS_MASK.get().getRepairIngredient()),
            Pair.of(DestroyItems.PAPER_MASK.get(), DestroyItems.PAPER_MASK.get().getRepairIngredient()),
            Pair.of(DestroyItems.LABORATORY_GOGGLES.get(), DestroyItems.LABORATORY_GOGGLES.get().getRepairIngredient()),
            Pair.of(DestroyItems.GOLD_LABORATORY_GOGGLES.get(), DestroyItems.GOLD_LABORATORY_GOGGLES.get().getRepairIngredient())
        );

        return repairables.stream().map(pair -> makeRepairRecipe(registration, new ItemStack(pair.getFirst()), pair.getSecond())).toList();
    };

    static int anvilRecipeId = 0;

    public static IJeiAnvilRecipe makeRepairRecipe(IRecipeRegistration registration, ItemStack input, Ingredient repairItem) {
        ItemStack halfDurability = input.copy();
        halfDurability.setDamageValue(halfDurability.getMaxDamage() / 2);
        ItemStack threeQuarterDurability = input.copy();
        threeQuarterDurability.setDamageValue(threeQuarterDurability.getMaxDamage() * 3 / 4);

        return registration.getVanillaRecipeFactory().createAnvilRecipe(
            Collections.singletonList(halfDurability),
            Arrays.asList(repairItem.getItems()),
            Collections.singletonList(threeQuarterDurability),
            Destroy.asResource(input.getDescriptionId()+"_repair_"+anvilRecipeId++)
        );
    };
};
