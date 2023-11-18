package com.petrolpark.destroy.effect;

import java.util.ArrayList;
import java.util.List;

import com.petrolpark.destroy.item.DestroyItems;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.item.ItemStack;

public class HangoverMobEffect extends DestroyMobEffect {
    public HangoverMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x59390B);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "31875c8a-f500-477c-ac52-70355c6adc12", (double)-0.10F, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        List<ItemStack> curativeItems = new ArrayList<ItemStack>();
        curativeItems.add(new ItemStack(DestroyItems.ASPIRIN_SYRINGE.get()));
        return curativeItems;
    };
}
