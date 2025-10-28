package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.DestroyTags.Items;
import com.simibubi.create.content.schematics.cannon.SchematicannonInventory;

import net.minecraft.world.item.ItemStack;

@MoveToPetrolparkLibrary
@Mixin(SchematicannonInventory.class)
public class SchematicannonInventoryMixin {
    
    /**
     * Injection into {@link com.simibubi.create.content.schematics.block.SchematicannonInventory#isItemValid SchematicannonInventory}.
     * This allows any {@link com.petrolpark.destroy.DestroyTags.Items#SCHEMATICANNON_FUEL explosive} (not just gunpowder)
     * to be used as fuel for the {@link com.simibubi.create.content.schematics.block.SchematicannonBlockEntity Schematicannon}.
     */
    @Inject(
        method = "Lcom/simibubi/create/content/schematics/cannon/SchematicannonInventory;isItemValid(ILnet/minecraft/world/item/ItemStack;)Z",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    public void isItemValid(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
        if (slot == 4) ci.setReturnValue(Items.SCHEMATICANNON_FUELS.matches(stack.getItem()));
    };
};
