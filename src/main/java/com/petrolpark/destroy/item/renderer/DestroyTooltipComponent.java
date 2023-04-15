package com.petrolpark.destroy.item.renderer;

import java.util.function.Function;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public abstract class DestroyTooltipComponent <T extends DestroyTooltipComponent<?,?>, C extends ClientTooltipComponent> implements TooltipComponent {

    private Function<T, C> clientTooltipComponentFactory;

    @SuppressWarnings(value = { "unchecked", "unused" })
    public DestroyTooltipComponent (Function<T, C> clientTooltipComponentFactory) {
        this.clientTooltipComponentFactory = clientTooltipComponentFactory;
        try {
            T obj = (T)this;
        } catch(ClassCastException e) {
            throw new ClassCastException("DestroyTooltipComponent subclasses must have themselves as type parameters, e.g. MyTooltipComponent extends DestroyTooltipComponent<MyTooltipComponent, MyClientTooltipComponent>");
        };
    };
    
    @SuppressWarnings("unchecked")
    public C getClientTooltipComponent() {
        return clientTooltipComponentFactory.apply((T)this);
    };
};
