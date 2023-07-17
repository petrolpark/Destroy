package com.petrolpark.destroy.item;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.item.armorMaterial.DestroyArmorMaterials;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class HazmatSuitArmorItem extends ArmorItem {

    public HazmatSuitArmorItem(Type type, Properties properties) {
        super(DestroyArmorMaterials.HAZMAT, type, properties);
    };

    private static String TEXTURE = Destroy.asResource("textures/models/armor/hazmat.png").toString();

    @Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return TEXTURE;
	};
    
};
