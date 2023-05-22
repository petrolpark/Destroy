package com.petrolpark.destroy.item.armorMaterial;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.petrolpark.destroy.item.DestroyItems;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public enum DestroyArmorMaterials implements ArmorMaterial {
    
    HAZMAT("hazmat", 2, new int[] {1, 1, 1, 1}, 0, () -> SoundEvents.ARMOR_EQUIP_LEATHER, 0f, 0f, () -> Ingredient.EMPTY),
    ZIRCONIUM("zirconium", 35, new int[] {3, 6, 8, 3}, 20, () -> SoundEvents.ARMOR_EQUIP_GOLD, 1f, 0f, () -> Ingredient.of(DestroyItems.ZIRCONIUM_INGOT.get()));

    private static final int[] MAX_DAMAGE = new int[] { 13, 15, 16, 11 };
	private final String name;
	private final int maxDamageFactor;
	private final int[] damageReductionAmountArray;
	private final int enchantability;
	private final Supplier<SoundEvent> soundEvent;
	private final float toughness;
	private final float knockbackResistance;
	private final Supplier<Ingredient> repairMaterial;

    DestroyArmorMaterials(String name, int maxDamageFactor, int[] damageReductionAmountArray, int enchantability, Supplier<SoundEvent> soundEvent, float toughness, float knockbackResistance, Supplier<Ingredient> repairMaterial) {
        this.name = name;
		this.maxDamageFactor = maxDamageFactor;
		this.damageReductionAmountArray = damageReductionAmountArray;
		this.enchantability = enchantability;
		this.soundEvent = soundEvent;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
		this.repairMaterial = Suppliers.memoize(repairMaterial::get);
    };

    @Override
	public int getDurabilityForSlot(EquipmentSlot slot) {
		return MAX_DAMAGE[slot.getIndex()] * maxDamageFactor;
	}

	@Override
	public int getDefenseForSlot(EquipmentSlot slot) {
		return damageReductionAmountArray[slot.getIndex()];
	}

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    };

    @Override
    public SoundEvent getEquipSound() {
        return soundEvent.get();
    };

    @Override
    public Ingredient getRepairIngredient() {
        return repairMaterial.get();
    };

    @Override
    public String getName() {
        return name;
    };

    @Override
    public float getToughness() {
        return toughness;
    };

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    };
    
};
