package com.petrolpark.destroy.mixin.accessor;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.infrastructure.gui.OpenCreateMenuButton.MenuRows;

@Mixin(MenuRows.class)
public interface MenuRowsAccessor {
    
    @Accessor(
        value = "leftButtons",
        remap = false
    )
    public List<String> getLeftButtons();

    @Accessor(
        value = "rightButtons",
        remap = false
    )
    public List<String> getRightButtons();
};
