package com.petrolpark.destroy.mixin.compat.jei;

import java.util.List;
import java.util.function.Supplier;

import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CreateRecipeCategory.class)
public interface CreateRecipeCategoryAccessor {

    @Accessor(
        value = "recipes",
        remap = false
    )
    public Supplier<List<? extends Recipe<?>>> getRecipes();

    @Accessor(
        value = "recipes",
        remap = false
    )
    public void setRecipes(Supplier<List<? extends Recipe<?>>> recipes);

    @Invoker(
        value = "addPotionTooltip",
        remap = false
    )
    public static void invokeAddPotionTooltip(IRecipeSlotView view, List<Component> tooltip) {
        throw new AssertionError();
    }
};
