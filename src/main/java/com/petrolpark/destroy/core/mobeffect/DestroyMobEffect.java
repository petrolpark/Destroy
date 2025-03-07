package com.petrolpark.destroy.core.mobeffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.simibubi.create.foundation.item.TooltipHelper;
import net.createmod.catnip.lang.FontHelper.Palette;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

@MoveToPetrolparkLibrary
public class DestroyMobEffect extends MobEffect {
    
    public DestroyMobEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    };

    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new DestroyMobEffectExtensions());
    };

    public static class DestroyMobEffectExtensions implements IClientMobEffectExtensions {

        public void addToTooltip(List<Component> tooltip, MobEffectInstance instance) {
            String descriptionId = instance.getDescriptionId() + ".description";
            if (I18n.exists(descriptionId)) {
                tooltip.add(Component.literal(" "));
                tooltip.addAll(TooltipHelper.cutTextComponent(Component.translatable(descriptionId), Palette.GRAY));
            };
        };

        public void renderTooltip(EffectRenderingInventoryScreen<? extends AbstractContainerMenu> screen, GuiGraphics guiGraphics, int mouseX, int mouseY, MobEffectInstance instance) {
            Minecraft minecraft = Minecraft.getInstance();
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(screen.getEffectName(instance));
            tooltip.add(MobEffectUtil.formatDuration(instance, 1f));
            addToTooltip(tooltip, instance);
            guiGraphics.renderTooltip(minecraft.font, tooltip, Optional.empty(), mouseX, mouseY);
        };
    };
};
