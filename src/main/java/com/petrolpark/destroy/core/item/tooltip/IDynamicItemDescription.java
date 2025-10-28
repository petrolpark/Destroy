package com.petrolpark.destroy.core.item.tooltip;

import com.simibubi.create.foundation.item.ItemDescription;
import net.createmod.catnip.lang.FontHelper.Palette;

import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

/**
 * An Item which has a description (in the same style as all Create tooltips)
 * that can change based on config values.
 */
public interface IDynamicItemDescription {

    public static Modifier create(Item item) {
        if (item instanceof IDynamicItemDescription itemWithDescription) {
            return new Modifier(item, itemWithDescription);
        };
        return null;
    };

    ItemDescription getItemDescription();

    Palette getPalette();

    public static class Modifier extends ItemDescription.Modifier {

        private final IDynamicItemDescription itemWithDescription;

		public Modifier(Item item, IDynamicItemDescription itemWithDescription) {
			super(item, itemWithDescription.getPalette());
            this.itemWithDescription = itemWithDescription;
		};

		@Override
		public void modify(ItemTooltipEvent context) {
			if (checkLocale()) {
				description = itemWithDescription.getItemDescription();
			};
			if (description == null) {
				return;
			};
			context.getToolTip().addAll(1, description.getCurrentLines());
		}
    };
};
