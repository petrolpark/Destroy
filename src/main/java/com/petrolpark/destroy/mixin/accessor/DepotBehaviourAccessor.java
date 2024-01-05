package com.petrolpark.destroy.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;

@Mixin(DepotBehaviour.class)
public interface DepotBehaviourAccessor {
    
    @Accessor(
        value = "heldItem",
        remap = false
    )
    public TransportedItemStack getHeldItem();
};
