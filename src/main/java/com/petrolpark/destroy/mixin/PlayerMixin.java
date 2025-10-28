package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.core.extendedinventory.ExtendedInventory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
@MoveToPetrolparkLibrary
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        throw new AssertionError();
    };

    @Shadow
    private Inventory inventory;
     
    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    public void inInit(Level level, BlockPos pos, float yRot, GameProfile gameProfile, CallbackInfo ci) {
        ExtendedInventory extendedInv = new ExtendedInventory((Player)(Object)this);
        inventory = extendedInv;
        ExtendedInventory.refreshPlayerInventoryMenu((Player)(Object)this);
    };

    @Inject(
        method = "setItemSlot",
        at = @At("HEAD"),
        cancellable = true
    )
    public void inSetItemSlot(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        verifyEquippedItem(stack);
        if (slot == EquipmentSlot.MAINHAND) {
            ExtendedInventory inv = ExtendedInventory.get((Player)(Object)this);
            ItemStack oldStack = inv.getItem(inv.selected);
            inv.setItem(inv.selected, stack);
            onEquipItem(slot, oldStack, stack);
            ci.cancel();
        };
    };
};
