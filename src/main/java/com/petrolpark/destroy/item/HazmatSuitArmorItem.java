package com.petrolpark.destroy.item;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.item.armorMaterial.DestroyArmorMaterials;
import com.petrolpark.destroy.util.ChemistryDamageHelper;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HazmatSuitArmorItem extends BaseArmorItem {

    public HazmatSuitArmorItem(Type type, Properties properties) {
        super(DestroyArmorMaterials.HAZMAT, type, properties, Destroy.asResource("hazmat"));
    };

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (entity instanceof LivingEntity livingEntity) {
            if (!ItemStack.matches(livingEntity.getItemBySlot(this.getEquipmentSlot()), stack))
            ChemistryDamageHelper.decontaminate(stack);
        };
    };
    
};
