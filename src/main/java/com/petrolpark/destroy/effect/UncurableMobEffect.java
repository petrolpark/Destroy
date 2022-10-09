package com.petrolpark.destroy.effect;

import java.util.List;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;

public class UncurableMobEffect extends MobEffect {
    public UncurableMobEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    };
    
    @Override
    public List<ItemStack> getCurativeItems() {
        return List.of();
    }
}
