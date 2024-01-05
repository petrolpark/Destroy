package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.destroy.item.directional.DirectionalTransportedItemStack;
import com.petrolpark.destroy.item.directional.IDirectionalOnBelt;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;

import net.minecraft.world.item.ItemStack;

@Mixin(DepotBehaviour.class)
public abstract class DepotBehaviourMixin {
    
    @Inject(
        method = "Lcom/simibubi/create/content/logistics/depot/DepotBehaviour;insert(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Z)Lnet/minecraft/world/item/ItemStack;",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    public void inInsert(TransportedItemStack heldItem, boolean simulate, CallbackInfoReturnable<ItemStack> cir) {
        if (!(heldItem instanceof DirectionalTransportedItemStack) && IDirectionalOnBelt.isDirectional(heldItem.stack)) {
            cir.setReturnValue(((DepotBehaviour)(Object)this).insert(DirectionalTransportedItemStack.copyFully(heldItem), simulate));
            cir.cancel();
        };
    };

    @Inject(
        method = "Lcom/simibubi/create/content/logistics/depot/DepotBehaviour;tick(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;)Z",
        at = @At("RETURN"),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    public void inTick(TransportedItemStack heldItem, CallbackInfoReturnable<Boolean> ci, float diff) {
        if (heldItem instanceof DirectionalTransportedItemStack directionalStack) directionalStack.refreshAngle();
    };

    @Accessor("heldItem")
    public abstract TransportedItemStack getHeldItem();
};
