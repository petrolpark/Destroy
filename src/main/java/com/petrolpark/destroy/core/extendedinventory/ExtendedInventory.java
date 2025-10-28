package com.petrolpark.destroy.core.extendedinventory;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.petrolpark.PetrolparkTags;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyAttributes;
import com.petrolpark.destroy.DestroyMessages;
import com.petrolpark.destroy.config.DestroyAllConfigs;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Destroy.MOD_ID)
public class ExtendedInventory extends Inventory {

    public NonNullList<ItemStack> extraItems = NonNullList.of(ItemStack.EMPTY);
    private int extraHotbarSlots = 0;

    public ExtendedInventory(Player player) {
        super(player);
        updateSize();
    };

    /**
     * @param player
     * @return The Player's Extended Inventory
     */
    public static ExtendedInventory get(Player player) {
        return (ExtendedInventory)player.getInventory();
    };

    public void updateSize() {
        updateSize(false);
    };

    public void updateSize(boolean forceSync) {
        int sizeBefore = extraItems.size();
        int hotbarBefore = extraHotbarSlots;
        if (player.getAttributes().hasAttribute(DestroyAttributes.EXTRA_HOTBAR_SLOTS.get())) setExtraHotbarSlots((int)player.getAttributeValue(DestroyAttributes.EXTRA_HOTBAR_SLOTS.get()));
        if (player.getAttributes().hasAttribute(DestroyAttributes.EXTRA_INVENTORY_SIZE.get())) setExtraInventorySize((int)player.getAttributeValue(DestroyAttributes.EXTRA_INVENTORY_SIZE.get()));
        if ((forceSync || sizeBefore != extraItems.size() || hotbarBefore != extraHotbarSlots) && !player.level().isClientSide() && player instanceof ServerPlayer sp && sp.connection != null) {
            DestroyMessages.sendToClient(new ExtraInventorySizeChangeS2CPacket(extraItems.size(), extraHotbarSlots, false), sp);
        };
    };

    public static void refreshPlayerInventoryMenu(Player player, int columns, int invX, int invY, int leftHotbarSlots, int leftHotbarX, int leftHotbarY, int rightHotbarX, int rightHotbarY) {
        player.inventoryMenu = new InventoryMenu(player.getInventory(), !player.level().isClientSide(), player); // Usually this field would be final; don't tell anybody I did this
        get(player).addExtraInventorySlotsToMenu(player.inventoryMenu, columns, invX, invY, leftHotbarSlots, leftHotbarX, leftHotbarY, rightHotbarX, rightHotbarY);
        player.containerMenu = player.inventoryMenu;
        if (player instanceof ServerPlayer sp && sp.containerSynchronizer != null && sp.containerListener != null) sp.initInventoryMenu();
    };

    public static void refreshPlayerInventoryMenu(Player player) {
        refreshPlayerInventoryMenu(player, 5, 0, 0, 0, 0, 0, 0, 0);
    };

    public void setExtraInventorySize(int size) {
        size = Math.max(size, 0);
        if (size == extraItems.size()) return;
        if (size < extraItems.size()) {
            for (int stack = size; stack < extraItems.size(); stack++) {
                player.drop(extraItems.get(stack), false);
            };
        };
        NonNullList<ItemStack> newExtraItems = NonNullList.withSize(size, ItemStack.EMPTY);
        for (int i = 0; i < size && i < extraItems.size(); i++) newExtraItems.set(i, extraItems.get(i));
        extraItems = newExtraItems;
        setChanged();
    };

    public void setExtraHotbarSlots(int extraSlots) {
        extraSlots = Math.max(extraSlots, 0);
        if (extraSlots == extraHotbarSlots) return;
        extraHotbarSlots = extraSlots;
        setChanged();
    };

    public int getExtraHotbarSlots() {
        return Math.min(extraItems.size(), extraHotbarSlots);
    };

    public int getExtraInventoryStartSlotIndex() {
        return super.getContainerSize();
    };
    /**
     * Whether the given slot index is in the vanilla or extended hotbar
     * @param index
     */
    public boolean isExtendedHotbarSlot(int index) {
        int extraInventoryStart = getExtraInventoryStartSlotIndex();
        return isHotbarSlot(index) || (index >= extraInventoryStart && index - extraInventoryStart < getExtraHotbarSlots());
    };

    public int getHotbarSize() {
        return getSelectionSize() + getExtraHotbarSlots();
    };

    /**
     * Get the slot index of the given index in the hotbar (how far right the selected slot is, considerate of the side on which the extra slots are)
     * @param hotbarIndex A number from {@code 0} to {@code 8 + getExtraHotbarSlots()}
     */
    protected int getSlotIndex(int hotbarIndex) {
        if (hotbarIndex < 0 || hotbarIndex >= getHotbarSize()) return -1;
        if (hotbarIndex < 9) return hotbarIndex;
        return getExtraInventoryStartSlotIndex() + hotbarIndex - getSelectionSize();
    };

    public int getSelectedHotbarIndex() {
        if (isHotbarSlot(selected)) return selected;
        return selected - getExtraInventoryStartSlotIndex() + getSelectionSize();
    };

    @SubscribeEvent
    public static void onPlayerJoinsWorld(PlayerEvent.PlayerLoggedInEvent event) {
        refreshPlayerInventoryMenu(event.getEntity());
        ExtendedInventory inv = get(event.getEntity());
        if (event.getEntity() instanceof ServerPlayer player) {
            DestroyMessages.sendToClient(new ExtraInventorySizeChangeS2CPacket(inv.extraItems.size(), inv.extraHotbarSlots, true), player);
        };
    };

    @SubscribeEvent
    public static void onOpenContainer(PlayerContainerEvent.Open event) {
        AbstractContainerMenu menu = event.getContainer();
        if (!supportsExtraInventory(menu) || menu instanceof IExtendedInventoryMenu) return;
        get(event.getEntity()).addExtraInventorySlotsToMenu(menu, 5, 0, 0, 0, 0, 0, 0, 0);
    };

    public static boolean supportsExtraInventory(AbstractContainerMenu menu) {
        if (menu instanceof IExtendedInventoryMenu) return true;
        try {
            MenuType<?> menuType = menu.getType();
            if (menuType == null) return false;
            if (DestroyAllConfigs.SERVER.extendedInventorySafeMode.get()) {
                return PetrolparkTags.MenuTypes.ALWAYS_SHOWS_EXTENDED_INVENTORY.matches(menuType);
            } else {
                return !PetrolparkTags.MenuTypes.NEVER_SHOWS_EXTENDED_INVENTORY.matches(menuType);
            }
        } catch (UnsupportedOperationException e) {
            return false;
        }
    };

    public void addExtraInventorySlotsToMenu(AbstractContainerMenu menu, int columns, int invX, int invY, int leftHotbarSlots, int leftHotbarX, int leftHotbarY, int rightHotbarX, int rightHotbarY) {
        addExtraInventorySlotsToMenu(menu::addSlot, Slot::new, columns, invX, invY, leftHotbarSlots, leftHotbarX, leftHotbarY, rightHotbarX, rightHotbarY);
    };

    public void addExtraInventorySlotsToMenu(Consumer<Slot> slotAdder, SlotFactory slotFactory, int columns, int invX, int invY, int leftHotbarSlots, int leftHotbarX, int leftHotbarY, int rightHotbarX, int rightHotbarY) {
        int extraItemsStart = getExtraInventoryStartSlotIndex();

        // Add right hotbar slots
        for (int i = 0; i < getExtraHotbarSlots() - leftHotbarSlots; i++) {
            slotAdder.accept(slotFactory.create(this, extraItemsStart + i, rightHotbarX + i * 18, rightHotbarY));
        };
        
        // Add left hotbar slots
        int j = 0;
        for (int i = getExtraHotbarSlots() - leftHotbarSlots; i < getExtraHotbarSlots(); i++) {
            slotAdder.accept(slotFactory.create(this, extraItemsStart + i, leftHotbarX + j * 18, leftHotbarY));
            j++;
        };

        // Add non-hotbar slots
        j = 0;
        for (int i = getExtraHotbarSlots(); i < extraItems.size(); i++) {
            slotAdder.accept(slotFactory.create(this, extraItemsStart + i, invX + 18 * (j % columns), invY + 18 * (j / columns)));
            j++;
        };
    };

    @FunctionalInterface
    public static interface SlotFactory {
        public Slot create(Container container, int slotIndex, int x, int y);
    };

    public void forEach(Consumer<? super ItemStack> action) {
        items.forEach(action);
        armor.forEach(action);
        offhand.forEach(action);
        extraItems.forEach(action);
    };

    public Stream<ItemStack> stream() {
        return Stream.concat(Stream.concat(items.stream(), armor.stream()), Stream.concat(offhand.stream(), extraItems.stream()));
    };

    @Override
    public ItemStack getSelected() {
        int extraInventoryStart = getExtraInventoryStartSlotIndex();
        if (selected >= extraInventoryStart) {
            int selectedExtra = selected - extraInventoryStart;
            if (selectedExtra < getExtraHotbarSlots()) return extraItems.get(selectedExtra);
        };
        return super.getSelected();
    };

    @Override
    public int getFreeSlot() {
        int freeSlot = super.getFreeSlot();
        if (freeSlot == -1) {
            for (int i = 0; i < extraItems.size(); i++) {
                if (extraItems.get(i).isEmpty()) return getExtraInventoryStartSlotIndex() + i;
            };
            return -1;
        } else {
            return freeSlot;
        }
    };

    /**
     * Pick an Item in Creative mode
     */
    @Override
    public void setPickedItem(ItemStack stack) {
        int matchingSlot = findSlotMatchingItem(stack);
        if (isExtendedHotbarSlot(matchingSlot)) {
            selected = matchingSlot;
        } else if (matchingSlot != -1) {
            pickSlot(matchingSlot);
        } else {
            selected = getSuitableHotbarSlot(); // Switch to a new or replaceable hotbar slot
            if (!getItem(selected).isEmpty()) { // Find a place to put the old Item which was selected
                int freeSlot = getFreeSlot();
                if (freeSlot != -1) setItem(freeSlot, stack);
            };
            setItem(selected, stack); // Give the Item
        };
    };

    /**
     * Stick an Item from the Inventory in the hotbar
     */
    @Override
    public void pickSlot(int index) {
        selected = getSuitableHotbarSlot();
        ItemStack oldSelectedStack = getItem(selected);
        setItem(selected, getItem(index)); // Swap the two Items
        setItem(index, oldSelectedStack);
    };

    @Override
    public int findSlotMatchingItem(ItemStack stack) {
        return findSlot(s -> !s.isEmpty() && ItemStack.isSameItemSameTags(s, stack));
    };

    @Override
    public int findSlotMatchingUnusedItem(ItemStack stack) {
        return findSlot(s -> !s.isEmpty() && ItemStack.isSameItemSameTags(s, stack) && !s.isDamaged() && !s.isEnchanted() && !s.hasCustomHoverName());
    };

    /**
     * Search for a slot in the vanilla and extended Inventories (i.e. not armor or offhand)
     * @param stackPredicate
     */
    public int findSlot(Predicate<ItemStack> stackPredicate) {
        for (int i = 0; i < items.size(); i++) {
            if (stackPredicate.test(items.get(i))) return i;
        };
        for (int i = 0; i < extraItems.size(); i++) {
            if (stackPredicate.test(extraItems.get(i))) return getExtraInventoryStartSlotIndex() + i;
        };
        return -1;
    };

    @Override
    public int getSuitableHotbarSlot() {
        int selectedHotbarSlot = getSelectedHotbarIndex();
        for (int i = 0; i < getHotbarSize(); i++) {
            int nextSlot = getSlotIndex((selectedHotbarSlot + i) % getHotbarSize());
            if (getItem(nextSlot).isEmpty()) return nextSlot;
        };
        for (int i = 0; i < getHotbarSize(); i++) {
            int nextSlot = getSlotIndex((selectedHotbarSlot + i) % getHotbarSize());
            if (!getItem(nextSlot).isNotReplaceableByPickAction(player, nextSlot)) return nextSlot;
        };
        return -1;
    };

    @Override
    public void swapPaint(double scroll) {
        int d = (int)Math.signum(scroll);
        int selectedHotbarSlot = getSelectedHotbarIndex();
        for (selectedHotbarSlot -= d; selectedHotbarSlot < 0; selectedHotbarSlot += getHotbarSize());
        while (selectedHotbarSlot >= getHotbarSize()) selectedHotbarSlot -= getHotbarSize();
        selected = getSlotIndex(selectedHotbarSlot);
    };

    @Override
    public int getSlotWithRemainingSpace(ItemStack stack) {
        int slot = super.getSlotWithRemainingSpace(stack);
        if (slot == -1) {
            for (int i = 0; i < extraItems.size(); i++) {
                if (hasRemainingSpaceForItem(extraItems.get(i), stack)) return getExtraInventoryStartSlotIndex() + i;
            };
        };
        return slot;
    };

    @Override
    public void tick() {
        updateSize();
        super.tick();
        for (int i = 0; i < extraItems.size(); i++) {
            int slot = getExtraInventoryStartSlotIndex() + i;
            extraItems.get(i).inventoryTick(player.level(), player, slot, selected == slot);
        };
    };

    /**
     * Copied from {@link Inventory#add(int, ItemStack) Minecraft source code}
     */
    @Override
    public boolean add(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        } else {
            try {
                if (stack.isDamaged()) {
                    if (slot == -1) slot = getFreeSlot();
                    if (slot >= getExtraInventoryStartSlotIndex()) {
                        int extraItemsIndex = slot - getExtraInventoryStartSlotIndex();
                        extraItems.set(extraItemsIndex, stack.copyAndClear());
                        extraItems.get(extraItemsIndex).setPopTime(5);
                        return true;
                    } else if (slot >= 0) {
                        items.set(slot, stack.copyAndClear());
                        items.get(slot).setPopTime(5);
                        return true;
                    } else if (player.getAbilities().instabuild) { // Creative players can delete Items
                        stack.setCount(0);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    int i;
                    do {
                        i = stack.getCount();
                        if (slot == -1) {
                            stack.setCount(addResource(stack));
                        } else {
                            stack.setCount(addResource(slot, stack));
                        };
                    } while (!stack.isEmpty() && stack.getCount() < i);

                    if (stack.getCount() == i && player.getAbilities().instabuild) {
                        stack.setCount(0);
                        return true;
                    } else {
                        return stack.getCount() < i;
                    }
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Item being added");
                crashreportcategory.setDetail("Registry Name", () -> String.valueOf(net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem())));
                crashreportcategory.setDetail("Item Class", () -> stack.getItem().getClass().getName());
                crashreportcategory.setDetail("Item ID", Item.getId(stack.getItem()));
                crashreportcategory.setDetail("Item data", stack.getDamageValue());
                crashreportcategory.setDetail("Item name", () -> {
                    return stack.getHoverName().getString();
                });
                throw new ReportedException(crashreport);
            }
        }
    };

    @Override
    public ItemStack removeItem(int slotIndex, int count) {
        if (slotIndex >= getExtraInventoryStartSlotIndex()) {
            slotIndex -= getExtraInventoryStartSlotIndex();
            if (slotIndex < extraItems.size()) {
                if (extraItems.get(slotIndex).isEmpty()) return ItemStack.EMPTY; 
                return ContainerHelper.removeItem(extraItems, slotIndex, count);
            };
        };
        return super.removeItem(slotIndex, count);
    };

    @Override
    public void removeItem(ItemStack stack) {
        if (!extraItems.removeIf(s -> s == stack)) super.removeItem(stack);
    };

    @Override
    public ItemStack removeItemNoUpdate(int slotIndex) {
        if (slotIndex >= getExtraInventoryStartSlotIndex()) {
            slotIndex -= getExtraInventoryStartSlotIndex();
            if (slotIndex < extraItems.size()) {
                ItemStack stack = extraItems.get(slotIndex);
                if (stack.isEmpty()) return ItemStack.EMPTY; 
                extraItems.set(slotIndex, ItemStack.EMPTY);
                return stack;
            };
        };
        return super.removeItemNoUpdate(slotIndex);
    };

    @Override
    public void setItem(int slotIndex, ItemStack stack) {
        if (slotIndex >= getExtraInventoryStartSlotIndex()) {
            slotIndex -= getExtraInventoryStartSlotIndex();
            if (slotIndex < extraItems.size()) {
                extraItems.set(slotIndex, stack);
                return;
            };
        };
        super.setItem(slotIndex, stack);
    };

    @Override
    public float getDestroySpeed(BlockState state) {
        return getItem(selected).getDestroySpeed(state);
    };

    @Override
    public ListTag save(ListTag listTag) {
        listTag = super.save(listTag);

        int extraInventoryStart = getExtraInventoryStartSlotIndex();
        for (int i = 0; i < extraItems.size(); i++) {
            ItemStack stack = extraItems.get(i);
            if (!stack.isEmpty()) {
               CompoundTag tag = new CompoundTag();
               tag.putInt("Slot", i + extraInventoryStart);
               stack.save(tag);
               listTag.add(tag);
            };
        };

        return listTag;
    };

    @Override
    public void load(ListTag listTag) {
        updateSize();
        super.load(listTag);
        int extraInventoryStart = getExtraInventoryStartSlotIndex();
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag tag = listTag.getCompound(i);
            if (tag.contains("Slot", Tag.TAG_INT)) {
                int slotIndex = tag.getInt("Slot");
                if (slotIndex >= extraInventoryStart) {
                    slotIndex -= extraInventoryStart;
                    if (slotIndex < extraItems.size()) extraItems.set(slotIndex, ItemStack.of(tag));
                };
            };
        };
    };

    @Override
    public int getContainerSize() {
        return super.getContainerSize() + extraItems.size();
    };

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && !extraItems.stream().anyMatch(stack -> !stack.isEmpty());
    };

    @Override
    public ItemStack getItem(int slotIndex) {
        int extraInventoryStart = getExtraInventoryStartSlotIndex();
        if (slotIndex >= extraInventoryStart) {
            slotIndex -= extraInventoryStart;
            if (slotIndex < extraItems.size()) return extraItems.get(slotIndex);
        };
        return super.getItem(slotIndex);
    };

    @Override
    public void dropAll() {
        super.dropAll();
        for (int i = 0; i < extraItems.size(); i++) {
            ItemStack stack = extraItems.get(i);
            if (stack.isEmpty()) continue;
            player.drop(stack, true, false);
            extraItems.set(i, ItemStack.EMPTY);
        };
    };

    @Override
    public boolean contains(ItemStack stack) {
        return findSlot(s -> ItemStack.isSameItemSameTags(s, stack)) != -1;
    };

    @Override
    public boolean contains(TagKey<Item> tag) {
        return findSlot(s -> s.is(tag)) != -1;
    };

    @Override
    public void replaceWith(Inventory playerInventory) {
        if (playerInventory instanceof ExtendedInventory extendedInv) {
            setExtraHotbarSlots(extendedInv.extraHotbarSlots);
            setExtraInventorySize(extendedInv.extraItems.size());
        };
        super.replaceWith(playerInventory);
    };

    @Override
    public void clearContent() {
        super.clearContent();
        extraItems.clear();
    };

    @Override
    public void fillStackedContents(StackedContents stackedContents) {
        super.fillStackedContents(stackedContents);
        extraItems.forEach(stackedContents::accountSimpleStack);
    };

    public static interface DelayedSlotPopulation {
        public void populateDelayedSlots();
    };
    
};
