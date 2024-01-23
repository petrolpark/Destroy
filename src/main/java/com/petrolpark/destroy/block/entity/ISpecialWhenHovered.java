package com.petrolpark.destroy.block.entity;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.BlockHitResult;

public interface ISpecialWhenHovered {
    
    public void whenLookedAt(LocalPlayer player, BlockHitResult result);
};
