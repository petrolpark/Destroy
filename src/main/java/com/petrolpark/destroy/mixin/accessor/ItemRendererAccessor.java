package com.petrolpark.destroy.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.entity.ItemRenderer;

@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {
    
    @Accessor("itemColors")
    public ItemColors getItemColors();
};
