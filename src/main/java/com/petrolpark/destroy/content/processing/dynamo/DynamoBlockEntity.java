package com.petrolpark.destroy.content.processing.dynamo;

import java.util.List;
import java.util.Optional;

import com.petrolpark.destroy.DestroyAdvancementTrigger;
import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.DestroyRecipeTypes;
import com.petrolpark.destroy.DestroySoundEvents;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.content.processing.discstamping.DiscElectroplatingRecipe;
import com.petrolpark.destroy.content.processing.dynamo.ChargingBehaviour.ChargingBehaviourSpecifics;
import com.petrolpark.destroy.content.processing.dynamo.arcfurnace.ArcFurnaceRecipe;
import com.petrolpark.destroy.core.data.advancement.DestroyAdvancementBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.simibubi.create.foundation.recipe.RecipeFinder;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class DynamoBlockEntity extends BasinOperatingBlockEntity implements ChargingBehaviourSpecifics {

    private static final Object recipeCacheKey = new Object();
    private static final Object discElectroplatingRecipeKey = new Object();

    public ChargingBehaviour chargingBehaviour;
    protected DestroyAdvancementBehaviour advancementBehaviour;
    public Lazy<BlockState> arcFurnaceBlock = Lazy.of(Blocks.AIR::defaultBlockState);

    public int soundDuration;

    public DynamoBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        soundDuration = 0;
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        chargingBehaviour = new ChargingBehaviour(this);
        behaviours.add(chargingBehaviour);

        advancementBehaviour = new DestroyAdvancementBehaviour(this, DestroyAdvancementTrigger.ARC_FURNACE, DestroyAdvancementTrigger.CHARGE_WITH_DYNAMO, DestroyAdvancementTrigger.ELECTROLYZE_WITH_DYNAMO);
        behaviours.add(advancementBehaviour);
    };

    public void onItemCharged(ItemStack stack) {
        advancementBehaviour.awardDestroyAdvancement(DestroyAdvancementTrigger.CHARGE_WITH_DYNAMO);
    };

    @Override
    public float calculateStressApplied() {
        lastStressApplied = super.calculateAddedStressCapacity();
        if (getBlockState().getValue(DynamoBlock.ARC_FURNACE)) lastStressApplied *= DestroyAllConfigs.SERVER.blocks.arcFurnaceStressMultiplier.getF();
        return lastStressApplied;
    };

    @Override
    public void tick() {
        super.tick();
        if (soundDuration > 0) {
            soundDuration--;
        } else if (isRunning()) {
            DestroySoundEvents.DYNAMO_CRACKLE.playOnServer(level, getBlockPos());
            soundDuration = 80;
        };
    };

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        soundDuration = compound.getInt("SoundDuration");
        CompoundTag stateTag = compound.getCompound("ArcFurnaceBlock");
        if (compound.contains("ArcFurnaceBlock", Tag.TAG_COMPOUND)) arcFurnaceBlock = Lazy.of(() -> NbtUtils.readBlockState(getLevel().holderLookup(Registries.BLOCK), stateTag));
    };

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("SoundDuration", soundDuration);
        if (!arcFurnaceBlock.get().isAir()) compound.put("ArcFurnaceBlock", NbtUtils.writeBlockState(arcFurnaceBlock.get()));
    };

    @Override
    @SuppressWarnings("null")
    public void onSpeedChanged(float prevSpeed) {
        if (hasLevel()) {
            getLevel().updateNeighborsAt(getBlockPos(), DestroyBlocks.DYNAMO.get()); // It thinks getLevel() can be null (it can't at this point)
        };
        super.onSpeedChanged(prevSpeed);
    };

    @Override
    public Optional<Recipe<?>> tryProcessInBasin(boolean simulate) {
        applyBasinRecipe();
        return Optional.ofNullable(currentRecipe);
    };

    @Override
    public Optional<ChargingRecipe> tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate) {
        if (getBlockState().getValue(DynamoBlock.ARC_FURNACE)) return Optional.empty();
        Optional<ChargingRecipe> recipe = getChargingRecipe(input.stack);
		if (!recipe.isPresent() || simulate) return recipe;
		List<ItemStack> outputs = RecipeApplier.applyRecipeOn(getLevel(), canProcessInBulk() ? input.stack : ItemHandlerHelper.copyStackWithSize(input.stack, 1), recipe.get());

		for (ItemStack createdItemStack : outputs) {
			if (!createdItemStack.isEmpty()) {
				onItemCharged(createdItemStack);
				break;
			};
		};

		outputList.addAll(outputs);
		return recipe;
    };

    @Override
    protected void applyBasinRecipe() {
        if (currentRecipe == null) return;
        if (getBasin().isEmpty()) return;
        if (BasinRecipe.match(getBasin().get(), currentRecipe)) {
            if (currentRecipe.getType() == DestroyRecipeTypes.ELECTROLYSIS.getType()) {
                advancementBehaviour.awardDestroyAdvancement(DestroyAdvancementTrigger.ELECTROLYZE_WITH_DYNAMO);
            } else if (currentRecipe.getType() == DestroyRecipeTypes.ARC_FURNACE.getType() || currentRecipe instanceof AbstractCookingRecipe) {
                advancementBehaviour.awardDestroyAdvancement(DestroyAdvancementTrigger.ARC_FURNACE);
            };
        };
        super.applyBasinRecipe();
    };

    @Override
    @SuppressWarnings({"null", "resource"})
    public Optional<ChargingRecipe> tryProcessInWorld(ItemEntity itemEntity, boolean simulate) {
        if (!hasLevel() || getBlockState().getValue(DynamoBlock.ARC_FURNACE)) return Optional.empty();
        ItemStack itemStack = itemEntity.getItem();
		Optional<ChargingRecipe> recipe = getChargingRecipe(itemStack);
		if (!recipe.isPresent() || simulate) return recipe; // If we're simulating, we only need to check that the Recipe exists
        
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
		return recipe;
    };

    @Override
    public boolean canProcessInBulk() {
        return DestroyAllConfigs.SERVER.blocks.dynamoBulkCharging.get();
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
		if (chargingBehaviour.running && chargingBehaviour.ticksRemaining > 0) return; // If this isn't the right time to process
		super.startProcessingBasin();

		chargingBehaviour.start(ChargingBehaviour.Mode.BASIN, Vec3.atBottomCenterOf(getBlockPos().below(2)).add(0f, (2 / 16f) + getBasin().map(basin -> {
            IFluidHandler fluidHandler = basin.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
            if (fluidHandler == null) return 0f;
            int totalFluid = 0;
            for (int i = 0; i < fluidHandler.getTanks(); i++) {
                totalFluid += fluidHandler.getFluidInTank(i).getAmount();
            };
            return (12 / 16f) * Mth.clamp(totalFluid / 2000f, 0, 1);
        }).orElse(0f), 0f), getRecipeDuration(currentRecipe)); // Get the Fluid level of the basin
	};

    private static final RecipeWrapper recipeInventory = new RecipeWrapper(new ItemStackHandler(1));

    public Optional<ChargingRecipe> getChargingRecipe(ItemStack itemStack) {
		Optional<ChargingRecipe> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(getLevel(), itemStack, DestroyRecipeTypes.CHARGING.getType(), ChargingRecipe.class);
		if (assemblyRecipe.isPresent()) return assemblyRecipe;
		recipeInventory.setItem(0, itemStack);
		return DestroyRecipeTypes.CHARGING.find(recipeInventory, getLevel());
	};

    @Override
    public int getRecipeDuration(Recipe<?> recipe) {
        if (recipe instanceof ProcessingRecipe processingRecipe) {
            return processingRecipe.getProcessingDuration();
        } else if (recipe instanceof AbstractCookingRecipe cookingRecipe) {
            return cookingRecipe.getCookingTime();
        };
        return ChargingBehaviour.CHARGING_TIME;
    };

    public boolean shouldRenderArcs() {
        if (!chargingBehaviour.running) return false;
        if (currentRecipe instanceof ArcFurnaceRecipe || currentRecipe instanceof AbstractCookingRecipe) return false;
        return true;
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
        chargingBehaviour.ticksRemaining = 0;
        sendData();
    };

    public int getRedstoneSignal() {
        return (int) Mth.lerp(Mth.clamp(Math.abs(getSpeed()) / 256f, 0, 1), 0, 15);
    };

    @Override
    protected List<Recipe<?>> getMatchingRecipes() {
        List<Recipe<?>> recipes = super.getMatchingRecipes();

        getBasin().ifPresent(basin -> {
            IItemHandler availableItems = basin.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
            if (availableItems != null) {
                for (int slot = 0; slot < availableItems.getSlots(); slot++) {
                    ItemStack stack = availableItems.getStackInSlot(slot);
                    if (stack.is(ItemTags.MUSIC_DISCS)) RecipeFinder.get(discElectroplatingRecipeKey, level, r -> r.getType() == DestroyRecipeTypes.DISC_ELECTROPLATING.getType() && r instanceof DiscElectroplatingRecipe recipe && recipe.original).forEach(r -> {
                        if (r instanceof DiscElectroplatingRecipe recipe) recipes.add(recipe.copyWithDisc(stack)); // This cast check should never fail
                    });
                };
            };
        });

        return recipes;
    };

    @Override
    protected <C extends Container> boolean matchStaticFilters(Recipe<C> recipe) {
        return (recipe.getType() == DestroyRecipeTypes.ELECTROLYSIS.getType())
        || (recipe.getType() == DestroyRecipeTypes.ARC_FURNACE.getType())
        || (recipe.getType() == RecipeType.SMELTING && DestroyAllConfigs.SERVER.blocks.arcFurnaceAllowsSmelting.get())
        || (recipe.getType() == RecipeType.BLASTING && DestroyAllConfigs.SERVER.blocks.arcFurnaceAllowsBlasting.get());
    };

    @Override
    protected <C extends Container> boolean matchBasinRecipe(Recipe<C> recipe) {
        if (recipe == null) return false;
        if ((recipe.getType() == DestroyRecipeTypes.ELECTROLYSIS.getType()) == getBlockState().getValue(DynamoBlock.ARC_FURNACE)) return false;
        return super.matchBasinRecipe(recipe);
    };

    @Override
    protected Object getRecipeCacheKey() {
        return recipeCacheKey;
    };

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(worldPosition).expandTowards(0d, -2d, 0d);
    };
    
};
