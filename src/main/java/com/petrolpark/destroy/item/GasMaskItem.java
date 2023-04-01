package com.petrolpark.destroy.item;

import com.simibubi.create.content.contraptions.goggles.GogglesItem;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

public class GasMaskItem extends Item {

    static {
        GogglesItem.addIsWearingPredicate(player -> DestroyItems.GAS_MASK.isIn(player.getItemBySlot(EquipmentSlot.HEAD)));
    };

    public GasMaskItem(Properties properties) {
        super(properties);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    };

    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    };
    
};
