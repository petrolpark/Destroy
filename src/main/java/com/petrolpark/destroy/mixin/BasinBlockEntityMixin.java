package com.petrolpark.destroy.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.destroy.block.entity.behaviour.BasinTooFullBehaviour;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.item.TooltipHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

@Mixin(BasinBlockEntity.class)
public class BasinBlockEntityMixin {
    
    @Inject(method = "addToGoggleTooltip", at = @At(value = "RETURN"), cancellable = true)
    public void inAddToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking, CallbackInfoReturnable<Boolean> ci) {
        if (((BasinBlockEntity)(Object)this).getBehaviour(BasinTooFullBehaviour.TYPE).tooFullToReact) {
            DestroyLang.translate("tooltip.basin.too_full.title").style(ChatFormatting.GOLD).forGoggles(tooltip);
            TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.basin.too_full").string(), TooltipHelper.Palette.GRAY_AND_WHITE).forEach(component -> {
                DestroyLang.builder().add(component.copy()).forGoggles(tooltip);
            });
        };
    };
};
