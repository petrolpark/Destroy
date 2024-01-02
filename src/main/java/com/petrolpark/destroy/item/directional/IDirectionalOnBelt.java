package com.petrolpark.destroy.item.directional;

import net.minecraft.world.item.ItemStack;

public interface IDirectionalOnBelt {
    
    public static boolean isDirectional(ItemStack stack) {
        return stack.getItem() instanceof IDirectionalOnBelt;
    };
};
