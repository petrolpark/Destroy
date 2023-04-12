package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.block.entity.CoolerBlockEntity.ColdnessLevel;
import com.petrolpark.destroy.compat.jei.animation.AnimatedCooler;
import com.simibubi.create.compat.jei.category.PackingCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedPress;
import com.simibubi.create.content.contraptions.processing.BasinRecipe;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;

@Mixin(PackingCategory.class)
public class PackingCategoryMixin {

    private static final AnimatedPress newPress = new AnimatedPress(true);
    private static final AnimatedCooler cooler = new AnimatedCooler();
    
    @Inject(method = "draw", at = @At(value = "INVOKE", target = "getRequiredHeat"), cancellable = true)
    private void inDraw(BasinRecipe recipe, IRecipeSlotsView iRecipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY, CallbackInfo ci) {
        if (recipe.getRequiredHeat().name() == "COOLED") {
            cooler.withColdness(ColdnessLevel.FROSTING)
                .draw(matrixStack, 177 / 2 + 3, 55); // I have replaced the dynamic access getBackground() with just a constant hopefully that shouldn't matter too much
            newPress.draw(matrixStack, 177 / 2 + 3, 34); // We also need to render the Press here seeing as that gets cancelled
            ci.cancel();
        };
    };
};
