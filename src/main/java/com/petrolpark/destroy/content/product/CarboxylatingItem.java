package com.petrolpark.destroy.content.product;

import java.util.function.Supplier;


import com.petrolpark.item.decay.ConfiguredDecayingItem;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.world.item.ItemStack;

public class CarboxylatingItem extends ConfiguredDecayingItem {

    public CarboxylatingItem(Properties properties, Supplier<ItemStack> decayProduct, Supplier<ConfigBase.ConfigInt> lifetime) {
        super(properties, decayProduct, lifetime);
    };

    @Override
    public String getDecayTimeTranslationKey(ItemStack stack) {
        return "item.destroy.carboxylating_item.remaining";
    };

    @Override
    public long getLifetime(ItemStack stack) {
        // It would be cool if this was affected by the Greenhouse Gas level of the world but that is going to be a bit complicated to implement right now
        return super.getLifetime(stack);
    };
    
};
