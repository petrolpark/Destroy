package com.petrolpark.destroy.core.block.entity;

import com.petrolpark.destroy.DestroyItems;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import net.minecraft.world.item.ItemStack;

public interface IHaveLabGoggleInformation extends IHaveGoggleInformation {

    @Override
    default ItemStack getIcon(boolean isPlayerSneaking) {
        return DestroyItems.LABORATORY_GOGGLES.asStack();
    };

};
