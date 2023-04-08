package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.npc.AbstractVillager;

@Mixin(AbstractVillager.class)
public interface AbstractVillagerAccessor {

    @Invoker("getInventory")
    public SimpleContainer invokeGetInventory();
};
