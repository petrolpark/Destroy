package com.petrolpark.destroy.block.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.petrolpark.destroy.behaviour.PollutingBehaviour;
import com.petrolpark.destroy.block.AgingBarrelBlock;
import com.petrolpark.destroy.recipe.AgingRecipe;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.utility.recipe.RecipeFinder;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandlerModifiable;

public class AgingBarrelBlockEntity extends SmartTileEntity implements IHaveGoggleInformation {

    private static final Object agingRecipeKey = new Object();
    private static final int TANK_CAPACITY = 1000;

    public SmartInventory inventory;
    protected SmartFluidTankBehaviour tank;

    protected LazyOptional<IFluidHandler> fluidCapability;
    public LazyOptional<IItemHandlerModifiable> itemCapability;

    protected DirectBeltInputBehaviour beltBehaviour;
    protected PollutingBehaviour pollutingBehaviour;

    private int timer; // -1 = open, 0 = done processing but closed
    private int totalTime;

    public AgingBarrelBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        inventory = new SmartInventory(2, this, 1, false)
            .whenContentsChanged($ -> checkRecipe())
            .forbidExtraction();
        itemCapability = LazyOptional.of(() -> inventory);

        timer = -1;
    };

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        tank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, TANK_CAPACITY, true)
            .whenFluidUpdates(this::sendData);
        behaviours.add(tank);
        fluidCapability = LazyOptional.of(() -> {
			return new CombinedTankWrapper(tank.getCapability().orElse(null));
		});

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
        if (!hasLevel() || getLevel().isClientSide()) return;
        List<Recipe<?>> allRecipes = RecipeFinder.get(agingRecipeKey, level, r -> r.getType() == DestroyRecipeTypes.AGING.getType());
        List<Recipe<?>> possibleRecipes = allRecipes.stream().filter(r -> {
            AgingRecipe recipe = (AgingRecipe) r;
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
            AgingRecipe recipe = (AgingRecipe) possibleRecipes.get(0);
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
        if (timer == -1 && !getTank().isEmpty()) { // If Barrel is open
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
        // Storage of what's in the Tank is automatically covered in SmartTileEntity
    };

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.put("Inventory", inventory.serializeNBT());
        compound.putInt("Timer", timer);
        compound.putInt("TotalTime", totalTime);
        // Retrieval of what's in the Tank is automatically covered in SmartTileEntity
    };

    @Nonnull
    @Override
    @SuppressWarnings("null")
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidCapability.cast();
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

    @SuppressWarnings("null") // It's not null I checked
    public void onTimerChange() {
        if (!hasLevel()) return;
        BlockState oldState = getBlockState();
        BlockState newState = getBlockState();
        newState = oldState.setValue(AgingBarrelBlock.IS_OPEN, timer < 0);
        if (timer <= 0) {
            tank.allowExtraction();
            tank.allowInsertion();
        };
        if (timer == -1) {
            newState = newState.setValue(AgingBarrelBlock.PROGRESS, 0);
        };
        newState = newState.setValue(AgingBarrelBlock.PROGRESS, 0);
        newState = newState.setValue(AgingBarrelBlock.PROGRESS, totalTime != 0 ? 4 - (int)(timer / (float)totalTime * 4) : 0);
        if (newState != oldState) {
            getLevel().setBlockAndUpdate(getBlockPos(), newState); // This is the bit it thinks might be null
            sendData();
        };
    };

    /**
     * Attempts to open the Aging Barrel.
     * @return Whether opening the Barrel was successful
     */
    @SuppressWarnings("null")
    public boolean tryOpen() {
        if (!hasLevel() || getLevel().isClientSide()) return false;
        if (timer == 0) {
            timer = -1;
            onTimerChange();
            return true;
        };
        return false;
    };

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (timer != -1) {
            DestroyLang.translate("tooltip.fluidcontraption.contents")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
            DestroyLang.tankContentsTooltip(tooltip, DestroyLang.translate("tooltip.aging_barrel.aging_tank"), getTank());
            DestroyLang.translate("tooltip.aging_barrel.progress")
                .style(ChatFormatting.GRAY)
                .space()
                .add(Component.literal((int)((1 - (timer/(float)totalTime)) * 100)+"%"))
                .forGoggles(tooltip);
            return true;
        };
        return false;
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
