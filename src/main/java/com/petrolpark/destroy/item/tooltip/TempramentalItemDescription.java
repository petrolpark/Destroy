package com.petrolpark.destroy.item.tooltip;

import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.util.DestroyLang;
import com.petrolpark.destroy.util.DestroyTags.DestroyItemTags;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;

import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class TempramentalItemDescription implements TooltipModifier {

	@Override
	public void modify(ItemTooltipEvent context) {
		if (DestroyAllConfigs.CLIENT.tempramentalItemDescriptions.get() && DestroyItemTags.LIABLE_TO_CHANGE.matches(context.getItemStack().getItem())) {
			context.getToolTip().add(Component.literal(" "));
			context.getToolTip().addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.liable_to_change").component(), Palette.RED));
		};
	};


};
