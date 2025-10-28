package com.petrolpark.destroy.mixin.compat.jei.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.simibubi.create.compat.jei.GhostIngredientHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.Slot;

@Mixin(GhostIngredientHandler.class)
@MoveToPetrolparkLibrary
public class GhostIngredientHandlerMixin {
    
    @Redirect(
        method = "Lcom/simibubi/create/compat/jei/GhostIngredientHandler;getTargetsTyped",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/Slot;isActive()Z"
        )
    )
    public boolean checkSlot(Slot slot) {
        Minecraft mc = Minecraft.getInstance();
        return slot.isActive() && slot.container != mc.player.getInventory();
    };
};
