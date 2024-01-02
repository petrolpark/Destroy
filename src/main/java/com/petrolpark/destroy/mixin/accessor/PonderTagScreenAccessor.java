package com.petrolpark.destroy.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.foundation.ponder.PonderTag;
import com.simibubi.create.foundation.ponder.ui.PonderTagScreen;

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
