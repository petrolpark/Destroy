package com.petrolpark.destroy.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

public class AlcoholicDrinkItem extends Item {

    private int strength;

    /**
     * @param pProperties
     * @param strength How many levels of the Inebriation effect this item adds
     */
    public AlcoholicDrinkItem(Properties pProperties, int strength) {
        super(pProperties);
        this.strength = strength;
    };

    public int getStrength() {
        return this.strength;
    };
    
    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }
}
