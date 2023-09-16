package com.petrolpark.destroy.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;

import com.petrolpark.destroy.block.entity.behaviour.ExtendedBasinBehaviour;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.content.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.item.TooltipHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

@Mixin(BasinBlockEntity.class)
public class BasinBlockEntityMixin implements IHaveHoveringInformation {
    
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (((BasinBlockEntity)(Object)this).getBehaviour(ExtendedBasinBehaviour.TYPE).tooFullToReact) {
            DestroyLang.translate("tooltip.basin.too_full.title").style(ChatFormatting.GOLD).forGoggles(tooltip);
            TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.basin.too_full").component(), TooltipHelper.Palette.GRAY_AND_WHITE).forEach(component -> {
                DestroyLang.builder().add(component.copy()).forGoggles(tooltip);
            });
            return true;
        };
        tooltip.add(Component.literal(""));
        return false;
    };
};
