package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.client.ponder.instruction.HighlightTagInstruction;
import com.petrolpark.destroy.mixin.accessor.SimpleRenderElementAccessor;
import com.simibubi.create.foundation.gui.element.RenderElement.SimpleRenderElement;
import com.simibubi.create.foundation.ponder.PonderTag;
import com.simibubi.create.foundation.ponder.ui.PonderButton;
import com.simibubi.create.foundation.ponder.ui.PonderUI;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;

import net.minecraft.client.gui.GuiGraphics;

@Mixin(PonderUI.class)
public class PonderUIMixin {
    
    @Inject(
        //method = "lambda$renderPonderTags$9(Lcom/mojang/blaze3d/vertex/PoseStack;IIZFFLnet/minecraft/client/gui/GuiGraphics;DI)V",
        method = "lambda$renderPonderTags$9",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/foundation/utility/animation/LerpedFloat;tickChaser()V"
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    public void inRenderPonderTags(PoseStack ms, int mouseX, int mouseY, boolean highlightAll, float fade, float partialTicks, GuiGraphics graphics, double guiScale, int height, CallbackInfo ci, LerpedFloat chase, PonderButton button) {
        if (button.getRenderElement() instanceof SimpleRenderElement element) {
            if (((SimpleRenderElementAccessor)element).getRenderable() instanceof PonderTag tag) {
                if (HighlightTagInstruction.highlightedTags.contains(tag)) chase.updateChaseTarget(1);    
            };
        };
    };
};
