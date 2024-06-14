package com.petrolpark.destroy.chemistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

public interface IItemReactant {
    
    /**
     * This should return {@code true} if the Item Stack is a valid reactant for this {@link Reaction}.
     * @param stack
     */
    boolean isItemValid(ItemStack stack);

    /**
     * Deal with 'consuming' this Item Stack - usually, this will involve shrinking the Stack by one,
     * but might also involve draining the Item Stacks durability. It should not leave the Item Stack
     * untouched. If this is a {@link IItemReactant#isCatalyst catalyst}, this method is not called.
     * @param stack
     */
    default void consume(ItemStack stack) {
        stack.shrink(1);
    };

    /**
     * Get the List of Item Stacks through which the JEI slot should cycle. For Tag Reactants for 
     * example, this should be a List of all Item Stacks in that Tag.
     */
    List<ItemStack> getDisplayedItemStacks();

    /**
     * Whether this Item Reactant is a catalyst. If so, it will not be consumed, but just required
     * for the {@link Reaction} to occur.
     */
    default boolean isCatalyst() {
        return false;
    };

    class SimpleItemReactant implements IItemReactant {

        protected final Supplier<Item> item;

        public SimpleItemReactant(Supplier<Item> item) {
            this.item = item;
        };

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack.is(item.get());
        };

        @Override
        public List<ItemStack> getDisplayedItemStacks() {
            return List.of(new ItemStack(item.get()));
        };

    };

    class SimpleItemCatalyst extends SimpleItemReactant {

        public SimpleItemCatalyst(Supplier<Item> item) {
            super(item);
        };

        @Override
        public boolean isCatalyst() {
            return true;
        };

        @Override
        public void consume(ItemStack stack) {};

    };
 
    class SimpleItemTagReactant implements IItemReactant {
        
        protected final TagKey<Item> tag;

        public SimpleItemTagReactant(TagKey<Item> tag) {
            this.tag = tag;
        };

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack.is(tag);
        };

        @Override
        public List<ItemStack> getDisplayedItemStacks() {
            ITag<Item> tagIterable = ForgeRegistries.ITEMS.tags().getTag(tag);
            List<ItemStack> displayedStacks = new ArrayList<>(tagIterable.size());
            tagIterable.forEach(item -> displayedStacks.add(new ItemStack(item)));
            return displayedStacks;
        };

    };

    class SimpleItemTagCatalyst extends SimpleItemTagReactant {

        public SimpleItemTagCatalyst(TagKey<Item> tag) {
            super(tag);
        };

        @Override
        public boolean isCatalyst() {
            return true;
        };

        @Override
        public void consume(ItemStack stack) {};

    };
};
