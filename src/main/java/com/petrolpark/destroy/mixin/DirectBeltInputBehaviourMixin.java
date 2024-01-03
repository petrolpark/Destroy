package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.destroy.item.directional.DirectionalTransportedItemStack;
import com.petrolpark.destroy.item.directional.IDirectionalOnBelt;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

@Mixin(DirectBeltInputBehaviour.class)
public class DirectBeltInputBehaviourMixin {
    
    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/belt/behaviour/DirectBeltInputBehaviour;handleInsertion(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/item/ItemStack;",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    public void inHandleInsertion(TransportedItemStack stack, Direction side, boolean simulate, CallbackInfoReturnable<ItemStack> cir) {
        if (!(stack instanceof DirectionalTransportedItemStack) && IDirectionalOnBelt.isDirectional(stack.stack)) { // If not already cast to a Directional transported stack
            cir.setReturnValue(((DirectBeltInputBehaviour)(Object)this).handleInsertion(DirectionalTransportedItemStack.copyFully(stack), side, simulate));
        };
    };
};
