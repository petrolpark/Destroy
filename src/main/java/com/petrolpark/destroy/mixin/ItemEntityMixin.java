package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.destroy.util.FireproofingHelper;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    @Shadow
    public abstract ItemStack getItem();

    public ItemEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        throw new AssertionError();
    };

    @Inject(
        method = "Lnet/minecraft/world/entity/item/ItemEntity;fireImmune()Z",
        at = @At("HEAD"),
        cancellable = true
    )
    public void inFireImmune(CallbackInfoReturnable<Boolean> cir) {
        if (FireproofingHelper.isFireproof(level().registryAccess(), getItem())) cir.setReturnValue(true);
    };
    

};
