package com.petrolpark.destroy.core.chemistry.hazard;

import com.petrolpark.destroy.client.DestroyLang;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper.Palette;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidStack;

public class ContaminatedItemTooltipModifier implements TooltipModifier {

    public static final Palette DARK_GRAY_AND_WHITE = Palette.ofColors(ChatFormatting.DARK_GRAY, ChatFormatting.WHITE);

    @Override
    public void modify(ItemTooltipEvent context) {
        if (!context.getItemStack().hasTag()) return;
        CompoundTag tag = context.getItemStack().getOrCreateTag();

        if (tag.contains("ContaminatingFluid", Tag.TAG_COMPOUND)) {
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(tag.getCompound("ContaminatingFluid"));
            context.getToolTip().addAll(1, TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.contamination_description").component(), Screen.hasAltDown() ? DARK_GRAY_AND_WHITE: Palette.GRAY));
            if (!fluid.isEmpty()) {
                if (Screen.hasAltDown()) {
                    context.getToolTip().add(2, Component.literal(" "));
                    context.getToolTip().add(3, Component.literal(" "));
                    context.getToolTip().addAll(3, TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.contamination", fluid.getDisplayName()).component(), Palette.RED));
                };
            }
        };
    };
    
};
