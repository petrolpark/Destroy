package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.compat.jei.animation.HeatConditionRenderer;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.Minecraft;

@Mixin(BasinCategory.class)
public class BasinCategoryMixin {
    
    /**
     * Injection into {@link com.simibubi.create.compat.jei.category.BasinCategory#setRecipe BasinCategory}.
     * Replaces the Blaze Burner icon with a Cooler if needs be, and replaces the Blaze Cake with the cycling {@link com.simibubi.create.AllTags.AllItemTags#BLAZE_BURNER_FUEL_SPECIAL Blaze Burner special fuel tag}.
     */
    @Inject(method = "setRecipe", at = @At(value = "INVOKE", target = "getRequiredHeat"), cancellable = true)
    protected void setRecipeInjection(IRecipeLayoutBuilder builder, BasinRecipe recipe, IFocusGroup focuses, CallbackInfo ci) { // Used to render a Breeze Burner (rather than a Blaze Burner) in the Recipe screen
        HeatConditionRenderer.addHeatConditionSlots(builder, 134, 81, recipe.getRequiredHeat());
        ci.cancel(); // Don't execute the rest of the method
    };

    /**
     * Injection into {@link com.simibubi.create.compat.jei.category.BasinCategory#draw BasinCategory}.
     * Replaces the rendered Blaze Burner with a Cooler and sets the text to 'COOLED' if required.
     */
    @SuppressWarnings("resource")
    @Inject(method = "draw", at = @At(value = "INVOKE", target = "getInstance"), cancellable = true) // Injects when it is writing "Heated", "Superheated", etc at the bottom of the screen
    protected void drawInjection(BasinRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY, CallbackInfo ci) {
        HeatConditionRenderer.drawHeatConditionName(Minecraft.getInstance().font, matrixStack, 9, 86, recipe.getRequiredHeat());
        ci.cancel(); // Don't execute the rest of the method
    };
}
