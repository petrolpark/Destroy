package com.petrolpark.destroy.events;

import java.util.ArrayList;
import java.util.List;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.capability.player.previousposition.PlayerPreviousPositions;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.item.renderer.SeismometerItemRenderer;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Components;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(Dist.CLIENT)
public class DestroyClientEvents {

    static int clientTicks = 0;

    // Copied from Create source code because Create only applies its sick-ass tooltips to Items in Mods with IDs that start with "create" for some godforsaken reason
    @SubscribeEvent
    public static void addToItemTooltip(ItemTooltipEvent event) {
        if (!AllConfigs.CLIENT.tooltips.get())
			return;
		if (event.getEntity() == null)
			return;

        ItemStack stack = event.getItemStack();
        String translationKey = stack.getItem().getDescriptionId(stack);
        if (translationKey.startsWith("item."+Destroy.MOD_ID) || translationKey.startsWith("block."+Destroy.MOD_ID)) {
            if (TooltipHelper.hasTooltip(stack, event.getEntity())) {
				List<Component> itemTooltip = event.getToolTip();
				List<Component> toolTip = new ArrayList<>();
				toolTip.add(itemTooltip.remove(0));
				ItemDescription itemDesc = TooltipHelper.getTooltip(stack); // Generate the Item description

                if (DestroyItems.CHORUS_WINE_BOTTLE.isIn(stack)) { // Chorus Wine has a dynamic description
                    String tooltipTranslationKey = TooltipHelper.getTooltipTranslationKey(stack);
                    TooltipHelper.cachedTooltips.remove(tooltipTranslationKey); // Remove the existing Tooltip so we don't add an inifite number of paragraphs to it
                    TooltipHelper.hasTooltip(stack, event.getEntity()); // Re-add the Tooltip
                    itemDesc = TooltipHelper.getTooltip(stack); // Regenerate the Item Description
                    itemDesc.getLinesOnShift().add(Components.immutableEmpty()); // Add the spacer
                    itemDesc.withBehaviour(
                        Component.translatable("item.destroy.chorus_wine_bottle.dynamic_tooltip.condition").getString(),
                        Component.translatable("item.destroy.chorus_wine_bottle.dynamic_tooltip.behaviour", PlayerPreviousPositions.getQueueSize()).getString()
                    );
                };

                itemDesc.addInformation(toolTip); // Add the Item description to the Item Stack
				itemTooltip.addAll(0, toolTip);
			};
        };
    };

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) SeismometerItemRenderer.tick();
    };
};
