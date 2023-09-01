package com.petrolpark.destroy.chemistry;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IItemReactant {
    
    /**
     * This should return {@code true} if the Item Stack is a valid reactant for this {@link Reaction}.
     * @param stack
     */
    public boolean isItemValid(ItemStack stack);

    /**
     * Deal with 'consuming' this Item Stack - usually, this will involve shrinking the Stack by one,
     * but might also involve draining the Item Stacks durability. It should not leave the Item Stack
     * untouched. If this is a {@link IItemReactant#isCatalyst catalyst}, this method is not called.
     * @param stack
     */
    public default void consume(ItemStack stack) {
        stack.shrink(1);
    };

    /**
     * Whether this Item Reactant is a catalyst. If so, it will not be consumed, but just required
     * for the {@link Reaction} to occur.
     */
    public default boolean isCatalyst() {
        return false;
    };

    public class SimpleItemReactant implements IItemReactant {

        protected final Item item;

        public SimpleItemReactant(Item item) {
            this.item = item;
        };

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack.is(item);
        };

    };

    public class SimpleItemTagReactant implements IItemReactant {
        
        protected final TagKey<Item> tag;

        public SimpleItemTagReactant(TagKey<Item> tag) {
            this.tag = tag;
        };

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack.is(tag);
        };

    };
};
