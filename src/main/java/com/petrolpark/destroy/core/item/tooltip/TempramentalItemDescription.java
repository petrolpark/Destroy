package com.petrolpark.destroy.core.item.tooltip;

import com.petrolpark.destroy.DestroyTags.Items;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper.Palette;

import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class TempramentalItemDescription implements TooltipModifier {

	@Override
	public void modify(ItemTooltipEvent context) {
		if (DestroyAllConfigs.CLIENT.tempramentalItemDescriptions.get() && Items.LIABLE_TO_CHANGE.matches(context.getItemStack().getItem())) {
			context.getToolTip().add(Component.literal(" "));
			context.getToolTip().addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.liable_to_change").component(), Palette.RED));
		};
	};


};
