package com.petrolpark.destroy.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class DestroyCreativeModeTabs {
    public static final CreativeModeTab TAB_DESTROY = new CreativeModeTab("destroy_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(DestroyItems.ABS.get());
        }
    };
}
