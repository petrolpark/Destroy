package com.petrolpark.destroy.compat.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.compat.jei.category.CentrifugationCategory;
import com.petrolpark.destroy.recipe.CentrifugationRecipe;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory.Info;
import com.simibubi.create.foundation.utility.recipe.IRecipeTypeInfo;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

@JeiPlugin
public class DestroyJEI implements IModPlugin {

    private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();

    private void loadCategories() {
        allCategories.clear();

        CreateRecipeCategory<?>

        centrifugation = new CategoryBuilder<>(CentrifugationRecipe.class).create(
            CentrifugationCategory::new,
            "centrifugation",
            new EmptyBackground(120, 115),
            DestroyBlocks.CENTRIFUGE.get(),
            DestroyRecipeTypes.CENTRIFUGATION
        );
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
    
    private class CategoryBuilder<T extends Recipe<?>> {

        private Class<? extends T> recipeClass;

        public CategoryBuilder(Class<? extends T> recipeClass) {
			this.recipeClass = recipeClass;
		};
        
        /**
         * Creates a Recipe Category
         * @param factory "TypeOfRecipe"Category::new
         * @param name
         * @param background
         * @param craftingItem Item/Block which is used to do the crafting, e.g. Centrifuge, Vaporizer, etc
         * @param recipeTypeEntry The Recipes which this Category contains
         * @return
         */
        public CreateRecipeCategory<T> create(CreateRecipeCategory.Factory<T> factory, String name, IDrawable background, ItemLike craftingItem, IRecipeTypeInfo recipeTypeEntry) {
            Destroy.LOGGER.info("Added JEI Category: "+name);

            Supplier<RecipeType<? extends T>> recipeType = recipeTypeEntry::getType;

            Supplier<List<T>> recipeSupplier = () -> { //this all puts all the recipes in a big ol list
                List<T> recipesList = new ArrayList<>();
                Consumer<List<T>> consumer = (recipes -> CreateJEI.<T>consumeTypedRecipes(recipes::add, recipeType.get()));
                consumer.accept(recipesList);
                return recipesList;
            };

            Info<T> info = new Info<T>(new mezz.jei.api.recipe.RecipeType<>(Destroy.asResource(name), recipeClass), DestroyLang.translate("recipe."+name).component(), background, new ItemIcon(() -> new ItemStack(craftingItem)), recipeSupplier, List.of(() -> new ItemStack(craftingItem)));
            
            CreateRecipeCategory<T> category = factory.create(info);
            allCategories.add(category);

            return category;
        };
    };
}
