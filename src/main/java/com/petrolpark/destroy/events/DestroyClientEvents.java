package com.petrolpark.destroy.events;

import java.util.ArrayList;
import java.util.List;

import com.petrolpark.destroy.Destroy;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.item.TooltipHelper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(Dist.CLIENT)
public class DestroyClientEvents {

    // Copied from Create source code because Create only applies its sick tooltips to Items in Mods with IDs that start with "create" for some godforsaken reason
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
				TooltipHelper.getTooltip(stack).addInformation(toolTip);
				itemTooltip.addAll(0, toolTip);
			}
        };
    };
};
