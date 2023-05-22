package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.petrolpark.destroy.item.tooltip.DestroyTooltipComponent;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

@Mixin(ClientTooltipComponent.class)
public interface ClientComponentTooltipMixin {

    /**
     * Overwritten but mostly copied from {@link net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent#create(TooltipComponent) Minecraft source code},
     * as Injecting into interfaces doesn't appear to be possible.
     * <p>When registering special tooltips for an Item Stack (i.e. those which produce images, like the {@link net.minecraft.world.item.BundleItem Bundle}, this searches
     * for and automatically handles {@link com.petrolpark.destroy.item.tooltip.DestroyTooltipComponent Destroy Tooltip Components}.</p>
     * @param tooltipComponent
     */
    @Overwrite
    public static ClientTooltipComponent create(TooltipComponent tooltipComponent) {
        if (tooltipComponent instanceof DestroyTooltipComponent<?, ?> destroyTooltipComponent) {
            return destroyTooltipComponent.getClientTooltipComponent();

        // All as in the Minecraft source code from here
        } else if (tooltipComponent instanceof BundleTooltip bundleTooltip) {
            return new ClientBundleTooltip(bundleTooltip);
        } else {
            ClientTooltipComponent result = net.minecraftforge.client.gui.ClientTooltipComponentManager.createClientTooltipComponent(tooltipComponent);
            if (result != null) return result;
            throw new IllegalArgumentException("Unknown TooltipComponent");
        }
    };
};
