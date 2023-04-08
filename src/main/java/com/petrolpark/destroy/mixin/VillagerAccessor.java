package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;

@Mixin(Villager.class)
public interface VillagerAccessor {

    @Invoker("getVillagerData")
    public VillagerData invokeGetVillagerData();
};
