package com.petrolpark.destroy.mixin.client;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.core.mobeffect.DestroyMobEffect.DestroyMobEffectExtensions;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.client.event.ScreenEvent.RenderInventoryMobEffects;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

@MoveToPetrolparkLibrary //native neoforge feature now
@Mixin(EffectRenderingInventoryScreen.class)
public class EffectRenderingInventoryScreenMixin {
    
    /**
     * Insert descriptions into the Potion effect tooltips that show in the inventory.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    @Inject(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    public void renderEffects(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci, int i, int j, Collection<MobEffectInstance> collection, boolean flag, RenderInventoryMobEffects event, int k, Iterable<MobEffectInstance> iterable, int l, MobEffectInstance mobeffectinstance) {
        if (IClientMobEffectExtensions.of(mobeffectinstance) instanceof DestroyMobEffectExtensions destroyMobEffectExtensions) {
            destroyMobEffectExtensions.renderTooltip((EffectRenderingInventoryScreen)(Object)this, guiGraphics, mouseX, mouseY, mobeffectinstance);
            ci.cancel();
        };
    };
}; 