package com.petrolpark.destroy.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.content.contraptions.processing.BasinRecipe;
import com.simibubi.create.content.contraptions.processing.HeatCondition;
import com.simibubi.create.foundation.utility.Lang;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

@Mixin(BasinCategory.class)
public class BasinCategoryMixin {
    
    /**
     * Injection into {@link com.simibubi.create.compat.jei.category.BasinCategory#setRecipe BasinCategory}.
     * Replaces the Blaze Burner icon with a Cooler if needs be, and replaces the Blaze Cake with the cycling {@link com.simibubi.create.AllTags.AllItemTags#BLAZE_BURNER_FUEL_SPECIAL Blaze Burner special fuel tag}.
     */
    @Inject(method = "setRecipe", at = @At(value = "INVOKE", target = "getRequiredHeat"), cancellable = true)
    protected void setRecipeInjection(IRecipeLayoutBuilder builder, BasinRecipe recipe, IFocusGroup focuses, CallbackInfo ci) { // Used to render a Breeze Burner (rather than a Blaze Burner) in the Recipe screen
        
        List<ItemStack> blazeTreatStacks = new ArrayList<>();
        ForgeRegistries.ITEMS.tags().getTag(AllTags.AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.tag).forEach(item -> {
            blazeTreatStacks.add(new ItemStack(item));
        });
        
        if (recipe.getRequiredHeat().name() == "COOLED") {
            builder.addSlot(RecipeIngredientRole.CATALYST, 134, 81).addItemStack(DestroyBlocks.COOLER.asStack());
        } else if (recipe.getRequiredHeat() != HeatCondition.NONE) { // This one is copied right from Create; it renders the Blaze Burner
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 134, 81).addItemStack(AllBlocks.BLAZE_BURNER.asStack());
        };
        if (recipe.getRequiredHeat() == HeatCondition.SUPERHEATED) { // Used to render all possible Blaze 'treats' rather than just the Blaze Cake
            builder.addSlot(RecipeIngredientRole.CATALYST, 153, 81).addItemStacks(blazeTreatStacks);
        };
        ci.cancel(); // Don't execute the rest of the method
    };

    /**
     * Injection into {@link com.simibubi.create.compat.jei.category.BasinCategory#draw BasinCategory}.
     * Replaces the rendered Blaze Burner with a Cooler and sets the text to 'COOLED' if required.
     */
    @SuppressWarnings("resource")
    @Inject(method = "draw", at = @At(value = "INVOKE", target = "getInstance"), cancellable = true) // Injects when it is writing "Heated", "Superheated", etc at the bottom of the screen
    protected void drawInjection(BasinRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY, CallbackInfo ci) {

        HeatCondition requiredHeat = recipe.getRequiredHeat();
        MutableComponent name = Component.empty();
        if (requiredHeat.name() == "COOLED") { // Scuffed but okay keep your opinions to yourself
            name = DestroyLang.translate(requiredHeat.getTranslationKey()).component();
        } else {
            name = Lang.translate(requiredHeat.getTranslationKey()).component();
        };

        Minecraft.getInstance().font.draw(matrixStack, name, 9, 86, requiredHeat.getColor()); // This is equivalent of the line being overwritten

        ci.cancel(); // Don't execute the rest of the method
    };
}
