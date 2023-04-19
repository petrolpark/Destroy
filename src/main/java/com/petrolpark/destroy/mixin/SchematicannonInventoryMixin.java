package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.destroy.util.DestroyTags.DestroyItemTags;
import com.simibubi.create.content.schematics.block.SchematicannonInventory;

import net.minecraft.world.item.ItemStack;

@Mixin(SchematicannonInventory.class)
public class SchematicannonInventoryMixin {
    
    /**
     * Injection into {@link com.simibubi.create.content.schematics.block.SchematicannonInventory#isItemValid SchematicannonInventory}.
     * This allows any {@link com.petrolpark.destroy.util.DestroyTags.DestroyItemTags#SCHEMATICANNON_FUEL explosive} (not just gunpowder)
     * to be used as fuel for the {@link com.simibubi.create.content.schematics.block.SchematicannonTileEntity Schematicannon}.
     * //TODO fix lang
     */
    @Inject(method = "isItemValid", at = @At(value = "HEAD"), cancellable = true)
    public void isItemValid(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
        if (slot == 4) ci.setReturnValue(DestroyItemTags.SCHEMATICANNON_FUEL.matches(stack.getItem()));
    };
};
