package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.destroy.block.entity.CoolerBlockEntity.ColdnessLevel;
import com.petrolpark.destroy.compat.jei.animation.AnimatedCooler;
import com.simibubi.create.compat.jei.category.MixingCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedMixer;
import com.simibubi.create.content.processing.basin.BasinRecipe;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(MixingCategory.class)
public class MixingCategoryMixin {

    private static final AnimatedMixer newMixer = new AnimatedMixer();
    private static final AnimatedCooler cooler = new AnimatedCooler();

    /**
     * Injection into {@link com.simibubi.create.compat.jei.category.MixingCategory#draw MixingCategory}.
     * This renders the Cooler instead of the Blaze Burner and sets the text, if required.
     */
    @Inject(
        method = "Lcom/simibubi/create/compat/jei/category/MixingCategory;draw(Lcom/simibubi/create/content/processing/basin/BasinRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphics;DD)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;getRequiredHeat()Lcom/simibubi/create/content/processing/recipe/HeatCondition;"
        ),
        cancellable = true,
        remap = false
    )
    private void inDraw(BasinRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY, CallbackInfo ci) {
        if (recipe.getRequiredHeat().name() == "COOLED") {
            cooler.withColdness(ColdnessLevel.FROSTING)
                .draw(graphics, 177 / 2 + 3, 55); // I have replaced the dynamic access getBackground() with just a constant hopefully that shouldn't matter too much
            newMixer.draw(graphics, 177 / 2 + 3, 34); // We also need to render the Press here seeing as that gets cancelled
            ci.cancel();
        };
    };
};
