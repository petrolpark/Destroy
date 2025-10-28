package com.petrolpark.destroy.mixin.compat.jei;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.destroy.compat.jei.animation.HeatConditionRenderer;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(BasinCategory.class)
public class BasinCategoryMixin {
    
    /**
     * Injection into {@link com.simibubi.create.compat.jei.category.BasinCategory#setRecipe BasinCategory}.
     * Replaces the Blaze Burner icon with a Cooler if needs be, and replaces the Blaze Cake with the cycling {@link com.simibubi.create.AllTags.AllItemTags#BLAZE_BURNER_FUEL_SPECIAL Blaze Burner special fuel tag}.
     */
    @Inject(
        method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/basin/BasinRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;getRequiredHeat()Lcom/simibubi/create/content/processing/recipe/HeatCondition;"
        ),
        cancellable = true,
        remap = false
    )
    protected void inSetRecipe(IRecipeLayoutBuilder builder, BasinRecipe recipe, IFocusGroup focuses, CallbackInfo ci) { // Used to render a Breeze Burner (rather than a Blaze Burner) in the Recipe screen
        HeatConditionRenderer.addHeatConditionSlots(builder, 134, 81, recipe.getRequiredHeat());
        ci.cancel(); // Don't execute the rest of the method
    };

    /**
     * Injection into {@link com.simibubi.create.compat.jei.category.BasinCategory#draw BasinCategory}.
     * Replaces the rendered Blaze Burner with a Cooler and sets the text to 'COOLED' if required.
     */
    @SuppressWarnings("resource")
    @Inject(
        method = "draw(Lcom/simibubi/create/content/processing/basin/BasinRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphics;DD)V",
            at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;getInstance()Lnet/minecraft/client/Minecraft;" // Injects when it is writing "Heated", "Superheated", etc at the bottom of the screen
        ),
        cancellable = true
    ) 
    protected void inDraw(BasinRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY, CallbackInfo ci) {
        HeatConditionRenderer.drawHeatConditionName(Minecraft.getInstance().font, graphics, 9, 86, recipe.getRequiredHeat());
        ci.cancel(); // Don't execute the rest of the method
    };
}
