package com.petrolpark.destroy.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;

@Mixin(DepotBehaviour.class)
public interface DepotBehaviourAccessor {
    
    @Accessor("Lcom/simibubi/create/content/logistics/depot/DepotBehaviour;heldItem:Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;")
    public TransportedItemStack getHeldItem();
};
