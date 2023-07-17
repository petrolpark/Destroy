package com.petrolpark.destroy.item;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.item.armorMaterial.DestroyArmorMaterials;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class ZirconiumPantsItem extends ArmorItem {

    private static final String TEXTURE = Destroy.asResource("textures/entity/armor/zirconium.png").toString();

    public ZirconiumPantsItem(Properties properties) {
        super(DestroyArmorMaterials.ZIRCONIUM, Type.LEGGINGS, properties);
    };

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return TEXTURE;
    };
    
};
