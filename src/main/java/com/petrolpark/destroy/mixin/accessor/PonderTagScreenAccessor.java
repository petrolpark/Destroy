package com.petrolpark.destroy.mixin.accessor;

import net.createmod.ponder.foundation.PonderTag;
import net.createmod.ponder.foundation.ui.PonderTagScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


import net.minecraft.world.item.ItemStack;

@Mixin(PonderTagScreen.class)
public interface PonderTagScreenAccessor {
    
    @Accessor(
        value = "tag",
        remap = false
    )
    public PonderTag getTag();

    @Accessor(
        value = "hoveredItem",
        remap = false
    )
    public ItemStack getHoveredItem();
};
