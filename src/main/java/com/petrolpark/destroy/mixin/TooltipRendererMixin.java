package com.petrolpark.destroy.mixin;

import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.common.gui.TooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.item.MoleculeDisplayItem;
import com.petrolpark.destroy.mixin.accessor.TooltipRendererAccessor;

@Mixin(TooltipRenderer.class)
public class TooltipRendererMixin {
    
    /**
     * Injection into JEI's {@link mezz.jei.common.gui.TooltipRenderer#drawHoveringText TooltipRenderer}.
     * This allows {@link com.petrolpark.destroy.item.MoleculeDisplayItem Molecule tooltips} to be rendered - usually tooltips are only rendered for Item Stack ingredients.
     */
    @Inject(method = "drawHoveringText(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;IILjava/lang/Object;Lmezz/jei/api/ingredients/IIngredientRenderer;)V", at = @At(value = "HEAD"), cancellable = true)
    private static <T> void inDrawHoveringText(PoseStack poseStack, List<Component> textLines, int x, int y, T ingredient, IIngredientRenderer<T> ingredientRenderer, CallbackInfo ci) {
        if (ingredient instanceof Molecule molecule) {
            Minecraft minecraft = Minecraft.getInstance();
            Font font = ingredientRenderer.getFontRenderer(minecraft, ingredient);
            TooltipRendererAccessor.invokeDrawHoveringText(poseStack, textLines, x, y, MoleculeDisplayItem.with(molecule), font);
            ci.cancel();
        };
    };
};
