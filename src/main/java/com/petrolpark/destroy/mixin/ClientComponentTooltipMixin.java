package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.petrolpark.destroy.item.renderer.DestroyTooltipComponent;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

@Mixin(ClientTooltipComponent.class)
public interface ClientComponentTooltipMixin {

    /**
     * Copied from {@link net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent#create(TooltipComponent) Minecraft source code},
     * as Injecting into interfaces doesn't appear to be possible.
     * @param tooltipComponent
     */
    @Overwrite
    public static ClientTooltipComponent create(TooltipComponent tooltipComponent) {
        if (tooltipComponent instanceof DestroyTooltipComponent<?, ?> destroyTooltipComponent) {
            return destroyTooltipComponent.getClientTooltipComponent();
        } else if (tooltipComponent instanceof BundleTooltip bundleTooltip) {
            return new ClientBundleTooltip(bundleTooltip);
        } else {
            ClientTooltipComponent result = net.minecraftforge.client.gui.ClientTooltipComponentManager.createClientTooltipComponent(tooltipComponent);
            if (result != null) return result;
            throw new IllegalArgumentException("Unknown TooltipComponent");
        }
    };
};
