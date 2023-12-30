package com.petrolpark.destroy.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.foundation.gui.element.ScreenElement;
import com.simibubi.create.foundation.gui.element.RenderElement.SimpleRenderElement;

@Mixin(SimpleRenderElement.class)
public interface SimpleRenderElementAccessor {
    
    @Accessor(
        value = "renderable",
        remap = false
    )
    public ScreenElement getRenderable();
};
