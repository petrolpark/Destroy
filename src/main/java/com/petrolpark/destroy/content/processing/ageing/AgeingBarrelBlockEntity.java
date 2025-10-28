package com.petrolpark.destroy.content.processing.ageing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import org.jetbrains.annotations.NotNull;

import com.petrolpark.destroy.DestroyRecipeTypes;
import com.petrolpark.destroy.DestroySoundEvents;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.core.pollution.PollutingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.RecipeFinder;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandlerModifiable;

public class AgeingBarrelBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    private static final Object agingRecipeKey = new Object();
    private static final int TANK_CAPACITY = 1000;

    public SmartInventory inventory;
    protected SmartFluidTankBehaviour tank;

    public LazyOptional<IItemHandlerModifiable> itemCapability;

    protected DirectBeltInputBehaviour beltBehaviour;
    protected PollutingBehaviour pollutingBehaviour;

    private int timer; // -1 = finished, 0 = progress is at 100%
    private int totalTime;

    public AgeingBarrelBlockEntity(BlockEntityType<?> pType, BlockPos pos, BlockState pBlockState) {
        super(pType, pos, pBlockState);
        inventory = new SmartInventory(2, this, 1, false)
            .whenContentsChanged($ -> checkRecipe())
            .forbidExtraction();
        itemCapability = LazyOptional.of(() -> inventory);

        timer = -1;
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.TYPE, this, 1, TANK_CAPACITY, true)
            .whenFluidUpdates(() -> {
                checkRecipe();
                sendData();
                if (timer == 0) timer = -1;
                onTimerChange();
            });
        behaviours.add(tank);

        beltBehaviour = new DirectBeltInputBehaviour(this);
        behaviours.add(beltBehaviour);

        pollutingBehaviour = new PollutingBehaviour(this);
        behaviours.add(pollutingBehaviour);
    };

    /**
     * Searches for a valid Recipe given the current contents of the Barrel and start processing if there is one.
     */
    @SuppressWarnings("null")
    public void checkRecipe() {
        if (!hasLevel() || getLevel().isClientSide()) return; // It thinks getLevel() might be null (it's not)
        List<Recipe<?>> allRecipes = RecipeFinder.get(agingRecipeKey, level, r -> r.getType() == DestroyRecipeTypes.AGING.getType());
        List<Recipe<?>> possibleRecipes = allRecipes.stream().filter(r -> {
            AgeingRecipe recipe = (AgeingRecipe) r;
            if (!recipe.getFluidIngredients().get(0).test(getTank().getFluid())) {
                return false;
            };
            List<ItemStack> availableItems = new ArrayList<>(); // Check the Fluid Ingredient is present
            availableItems.addAll(List.of(this.inventory.getItem(0), this.inventory.getItem(1)));
            boolean[] ingredientsMatched = new boolean[recipe.getIngredients().size()];
            for (int i = 0; i < recipe.getIngredients().size(); i++) { // Check that each Item Ingredient is present
                Ingredient ingredient = recipe.getIngredients().get(i);
                boolean ingredientMatched = false;
                ItemStack extractedStack = ItemStack.EMPTY;
                checkEachItemStack: for (ItemStack stack : availableItems) { // Check each Item Stack to see if it matches the Ingredient
                    if (ingredient.test(stack)) {
                        ingredientMatched = true;
                        extractedStack = stack;
                        break checkEachItemStack;
                    };
                };
                ingredientsMatched[i] = ingredientMatched;
                if (ingredientMatched) {
                    availableItems.remove(extractedStack); // If an Item Stack matches an Ingredient, remove it as it cannot be used for another Ingredient
                } else {
                    return false; // This Ingredient was never found
                };
            };
            return true;
        }).collect(Collectors.toList());

        if (possibleRecipes.size() >= 1) { // If a Recipe is found
            AgeingRecipe recipe = (AgeingRecipe) possibleRecipes.get(0);
            onTimerChange(); // Update how the Barrel looks before any Fluids are changed
            getTank().drain(TANK_CAPACITY, FluidAction.EXECUTE); // Drain input
            inventory.clearContent(); // Empty Inventory
            getTank().fill(recipe.getFluidResults().get(0), FluidAction.EXECUTE); // Fill output
            totalTime = recipe.getProcessingDuration();
            timer = recipe.getProcessingDuration();
            tank.forbidExtraction();
            tank.forbidInsertion();
        };
    };

    public int getLuminosity() {
        if (getBlockState().getValue(AgingBarrelBlock.IS_OPEN) && !getTank().isEmpty()) { // If Barrel is open
            FluidStack fluidStack = getTank().getFluid();
            return fluidStack.getRawFluid().getFluidType().getLightLevel(fluidStack);
        } else {
            return 0;
        }
    };

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        inventory.deserializeNBT(compound.getCompound("Inventory"));
        timer = compound.getInt("Timer");
        totalTime = compound.getInt("TotalTime");
        // Storage of what's in the Tank is automatically covered in SmartBlockEntity
    };

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.put("Inventory", inventory.serializeNBT());
        compound.putInt("Timer", timer);
        compound.putInt("TotalTime", totalTime);
        // Retrieval of what's in the Tank is automatically covered in SmartBlockEntity
    };

    @Nonnull
    @Override
    @SuppressWarnings("null")
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return tank.getCapability().cast();
        } else if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemCapability.cast();
        };
        return super.getCapability(cap, side);
    };

    @Override
    public void tick() {
        if (timer > 0) {
            timer--;
            onTimerChange();
        };
        super.tick();
    };

    @SuppressWarnings("null")
    public void onTimerChange() {
        if (!hasLevel()) return;
        BlockState oldState = getBlockState();
        BlockState newState = oldState.setValue(AgingBarrelBlock.IS_OPEN, timer >= 0 ? false : oldState.getValue(AgingBarrelBlock.IS_OPEN));
        if (timer <= 0) {
            tank.allowExtraction();
            tank.allowInsertion();
        } else {
            tank.forbidExtraction();
            tank.forbidInsertion();
        };
        int progress;
        if (timer < 0) {
            progress = 0;
        } else if (timer == 0) {
            progress = 4;
        } else {
            progress = (int)Mth.clamp(5 * (0.995f - (float)timer / (float)totalTime), 0, 4.9f);
        };
        newState = newState.setValue(AgingBarrelBlock.PROGRESS, progress);
        if (newState != oldState) {
            getLevel().setBlockAndUpdate(getBlockPos(), newState); // This is the bit it thinks might be null
            sendData();
            if (newState.getValue(AgingBarrelBlock.PROGRESS) != 0) DestroySoundEvents.AGING_BARREL_BALLOON.playOnServer(level, getBlockPos());
            if (timer < 0) DestroySoundEvents.AGING_BARREL_OPEN.playOnServer(level, getBlockPos()); // If the Barrel has been opened
        };
    };

    /**
     * Attempts to open the Aging Barrel.
     * @return Whether opening the Barrel was successful
     */
    @SuppressWarnings("null")
    public boolean tryOpen() {
        if (!hasLevel() || getLevel().isClientSide()) return false; // It thinks getLevel() might be null (it's not)
        if (timer == 0) {
            timer = -1;
            getLevel().setBlockAndUpdate(getBlockPos(), getBlockState().setValue(AgingBarrelBlock.IS_OPEN, true));
            onTimerChange();
            return true;
        };
        return false;
    };

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        containedFluidTooltip(tooltip, isPlayerSneaking, tank.getCapability().cast());
        if (timer >= 0) {
            DestroyLang.translate("tooltip.aging_barrel.progress", (timer == 0 ? 100 : (int)((0.995 - (timer/(float)totalTime)) * 100)) + "%")
                .style(ChatFormatting.WHITE)
                .forGoggles(tooltip);
        };
        return true;
    };

    public SmartFluidTank getTank(){
        return tank.getPrimaryHandler();
    };

    /**
     * Get the Fluid to render in the world when the Barrel is open.
     */
    public TankSegment getTankToRender() {
        return tank.getPrimaryTank();
    };
    
}
