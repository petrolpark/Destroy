package com.petrolpark.destroy.block.entity;

import java.util.List;
import java.util.Optional;

import com.petrolpark.destroy.advancement.DestroyAdvancements;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.entity.behaviour.ChargingBehaviour;
import com.petrolpark.destroy.block.entity.behaviour.DestroyAdvancementBehaviour;
import com.petrolpark.destroy.block.entity.behaviour.ChargingBehaviour.ChargingBehaviourSpecifics;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.recipe.ChargingRecipe;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class DynamoBlockEntity extends BasinOperatingBlockEntity implements ChargingBehaviourSpecifics {

    private static final Object electrolysisRecipeKey = new Object();

    public ChargingBehaviour chargingBehaviour;
    protected DestroyAdvancementBehaviour advancementBehaviour;

    private int soundDuration;

    public DynamoBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        soundDuration = 0;
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        chargingBehaviour = new ChargingBehaviour(this);
        behaviours.add(chargingBehaviour);

        advancementBehaviour = new DestroyAdvancementBehaviour(this);
        behaviours.add(advancementBehaviour);
    };

    public void onItemCharged(ItemStack stack) {
        advancementBehaviour.awardDestroyAdvancement(DestroyAdvancements.CHARGE_WITH_DYNAMO);
    };

    @Override
    public void tick() {
        super.tick();
        if (soundDuration > 0) {
            soundDuration--;
        } else if (isRunning()) {
            //DestroySoundEvents.DYNAMO_CRACKLE.playOnServer(level, getBlockPos()); //TODO uncommment once sound is sorted out
            soundDuration = 80;
        };
    };

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        soundDuration = compound.getInt("SoundDuration");
    };

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("SoundDuration", soundDuration);
    };

    @Override
    public void onSpeedChanged(float prevSpeed) {
        if (hasLevel()) {
            getLevel().updateNeighborsAt(getBlockPos(), DestroyBlocks.DYNAMO.get()); // It thinks getLevel() can be null (it can't at this point)
        };
        super.onSpeedChanged(prevSpeed);
    };

    @Override
    public boolean tryProcessInBasin(boolean simulate) {
        applyBasinRecipe();
        return true;
    };

    @Override
    public boolean tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate) {
        Optional<ChargingRecipe> recipe = getChargingRecipe(input.stack);
		if (!recipe.isPresent()) return false;
		if (simulate) return true;
		List<ItemStack> outputs = RecipeApplier.applyRecipeOn(getLevel(), canProcessInBulk() ? input.stack : ItemHandlerHelper.copyStackWithSize(input.stack, 1), recipe.get());

		for (ItemStack createdItemStack : outputs) {
			if (!createdItemStack.isEmpty()) {
				onItemCharged(createdItemStack);
				break;
			};
		};

		outputList.addAll(outputs);
		return true;
    };

    @Override
    @SuppressWarnings("resource")
    public boolean tryProcessInWorld(ItemEntity itemEntity, boolean simulate) {
        if (!hasLevel()) return false;
        ItemStack itemStack = itemEntity.getItem();
		Optional<ChargingRecipe> recipe = getChargingRecipe(itemStack);
		if (!recipe.isPresent()) return false;
		if (simulate) return true; // If we're simulating, we only need to check that the Recipe exists
        
        ItemStack itemStackCreated = ItemStack.EMPTY;
		if (canProcessInBulk() || itemStack.getCount() == 1) { // If this is the last or all Items in the Stack
			RecipeApplier.applyRecipeOn(itemEntity, recipe.get()); // Apply the charging Recipe
			itemStackCreated = itemEntity.getItem().copy();
		} else {
			for (ItemStack result : RecipeApplier.applyRecipeOn(getLevel(), ItemHandlerHelper.copyStackWithSize(itemStack, 1), recipe.get())) { // Apply the Charging Recipe
				if (itemStackCreated.isEmpty()) {
					itemStackCreated = result.copy();
                };
				ItemEntity createdItemEntity = new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result);
				createdItemEntity.setDefaultPickUpDelay();
				createdItemEntity.setDeltaMovement(VecHelper.offsetRandomly(Vec3.ZERO, getLevel().random, .05f)); // It thinks getLevel() might be null (it can't be at this point)
				getLevel().addFreshEntity(createdItemEntity); // It thinks getLevel() might be null (it can't be at this point)
			};
			itemStack.shrink(1);
		};

		if (!itemStackCreated.isEmpty()) onItemCharged(itemStackCreated);
		return true;
    };

    @Override
    public boolean canProcessInBulk() {
        return DestroyAllConfigs.SERVER.contraptions.dynamoBulkCharging.get();
    };

    @Override
    public void onChargingCompleted() {
        if (chargingBehaviour.mode == ChargingBehaviour.Mode.BASIN && matchBasinRecipe(currentRecipe) && getBasin().filter(BasinBlockEntity::canContinueProcessing).isPresent()) {
			startProcessingBasin();
		} else {
			basinChecker.scheduleUpdate();
        }
    };

    @Override
	public void startProcessingBasin() {
		if (chargingBehaviour.running && chargingBehaviour.runningTicks <= ChargingBehaviour.CHARGING_TIME) return; // If this isn't the right time to process
		super.startProcessingBasin();

        advancementBehaviour.awardDestroyAdvancement(DestroyAdvancements.ELECTROLYZE_WITH_DYNAMO);

		chargingBehaviour.start(ChargingBehaviour.Mode.BASIN, Vec3.atBottomCenterOf(getBlockPos().below(2)).add(0f, (2 / 16f) + getBasin().map(basin -> {
            IFluidHandler fluidHandler = basin.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
            if (fluidHandler == null) return 0f;
            int totalFluid = 0;
            for (int i = 0; i < fluidHandler.getTanks(); i++) {
                totalFluid += fluidHandler.getFluidInTank(i).getAmount();
            };
            return (12 / 16f) * Mth.clamp(totalFluid / 2000f, 0, 1);
        }).orElse(0f), 0f)); // Get the Fluid level of the basin
	};

    private static final RecipeWrapper chargingInventory = new RecipeWrapper(new ItemStackHandler(1));

    public Optional<ChargingRecipe> getChargingRecipe(ItemStack itemStack) {
		Optional<ChargingRecipe> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(getLevel(), itemStack, DestroyRecipeTypes.CHARGING.getType(), ChargingRecipe.class);
		if (assemblyRecipe.isPresent()) return assemblyRecipe;
		chargingInventory.setItem(0, itemStack);
		return DestroyRecipeTypes.CHARGING.find(chargingInventory, getLevel());
	};

    @Override
    public float getKineticSpeed() {
        return getSpeed();
    };

    @Override
    public boolean isRunning() {
        return chargingBehaviour.running;
    };

    public Vec3 getLightningTargetPosition() {
        return chargingBehaviour.targetPosition;
    };

    @Override
    protected void onBasinRemoved() {
        chargingBehaviour.running = false;
        chargingBehaviour.runningTicks = 0;
        sendData();
    };

    public int getRedstoneSignal() {
        return (int) Mth.lerp(Mth.clamp(Math.abs(getSpeed()) / 256f, 0, 1), 0, 15);
    };

    @Override
    protected <C extends Container> boolean matchStaticFilters(Recipe<C> recipe) {
        return recipe.getType() == DestroyRecipeTypes.ELECTROLYSIS.getType();
    };

    @Override
    protected Object getRecipeCacheKey() {
        return electrolysisRecipeKey;
    };
    
};
