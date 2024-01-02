package com.petrolpark.destroy.item;

import com.petrolpark.destroy.item.directional.IDirectionalOnBelt;

import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CircuitMaskItem extends Item implements IDirectionalOnBelt {

    /*
     *  y x0 1 2 3
     *  0  a b c d
     *  1  e f g h
     *  2  i j k l
     *  3  m n o p
     */

    public CircuitMaskItem(Properties properties) {
        super(properties);
    };

    /**
     * Punch a hole in a pattern.
     * @param pattern The pattern to punch
     * @param x x-coordinate of the hole to punch
     * @param y y-coordinate of the hole to punch
     * @return A new pattern with the punched hole
     */
    public static int punch(int pattern, int x, int y) {
        return pattern | 1 << getIndex(x, y);
    };

    public static int getIndex(int x, int y) {
        return x + (4 * y);
    };

    public int getPattern(ItemStack stack) {
        if (!stack.getOrCreateTag().contains("Pattern", Tag.TAG_SHORT)) return 0;
        return (int)Short.MAX_VALUE + (int)stack.getOrCreateTag().getShort("Pattern");
    };

    public void putPattern(ItemStack stack, int pattern) {
        stack.getOrCreateTag().putShort("Pattern", (short)(pattern - Short.MAX_VALUE));
    };
    
};
