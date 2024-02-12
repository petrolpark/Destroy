package com.petrolpark.destroy.block.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.petrolpark.destroy.advancement.DestroyAdvancements;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.VatControllerBlock;
import com.petrolpark.destroy.block.display.MixtureContentsDisplaySource;
import com.petrolpark.destroy.block.entity.VatSideBlockEntity.DisplayType;
import com.petrolpark.destroy.block.entity.behaviour.DestroyAdvancementBehaviour;
import com.petrolpark.destroy.block.entity.behaviour.fluidTankBehaviour.VatFluidTankBehaviour;
import com.petrolpark.destroy.block.entity.behaviour.fluidTankBehaviour.VatFluidTankBehaviour.VatTankSegment.VatFluidTank;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.Mixture.ReactionContext;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.fluid.MixtureFluid;
import com.petrolpark.destroy.util.DestroyLang;
import com.petrolpark.destroy.util.ExplosionHelper;
import com.petrolpark.destroy.util.PollutionHelper;
import com.petrolpark.destroy.util.vat.Vat;
import com.petrolpark.destroy.world.explosion.SmartExplosion;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class VatControllerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, ISpecialWhenHovered {

    public static final float AIR_PRESSURE = 101000;

    protected Optional<Vat> vat;

    /**
     * Server-side only storage of the Mixture so it doesn't have to be de/serialized every tick.
     * This Mixture belongs to an imaginary Fluid Stack with a size equal to the capacity of the Vat.
     */
    protected Mixture cachedMixture;
    /**
     * The power (in W) being supplied to this Vat. This can be positive (if the Vat is
     * being heated) or negative (if it is being cooled).
     */
    protected float heatingPower;
    /**
     * The amount of UV being supplied to this Vat.
     */
    protected float UVPower;

    /**
     * As the client side doesn't have access to the cached Mixture, store the pressure and temperature.
     */
    protected LerpedFloat pressure = LerpedFloat.linear();
    protected LerpedFloat temperature = LerpedFloat.linear();

    protected VatFluidTankBehaviour tankBehaviour;
    protected LazyOptional<IFluidHandler> fluidCapability;
    protected BlockPos openVentPos;

    public SmartInventory inventory;
    protected LazyOptional<IItemHandler> itemCapability;
    protected boolean inventoryChanged;

    protected DestroyAdvancementBehaviour advancementBehaviour;

    protected int initializationTicks;
    /**
     * Whether the {@link com.petrolpark.destroy.util.vat.Vat Vat} associated with this Vat Controller is already under the process of being deleted.
     */
    protected boolean underDeconstruction;

    public VatControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        vat = Optional.empty();
        initializationTicks = 3;
        underDeconstruction = false;

        fluidCapability = LazyOptional.empty();
        itemCapability = LazyOptional.empty();
        openVentPos = null;

        updateItemCapability();
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        // Fluid behaviour
        tankBehaviour = new VatFluidTankBehaviour(this, 1000000); // Tank capacity is set very high but is not this high in effect
        tankBehaviour.whenFluidUpdates(this::onFluidStackChanged)
            .forbidExtraction() // Forbid extraction until the Vat is initialized
            .forbidInsertion(); // Forbid insertion no matter what
        fluidCapability = LazyOptional.empty();
        behaviours.add(tankBehaviour);

        // Advancement behaviour
        advancementBehaviour = new DestroyAdvancementBehaviour(this);
        behaviours.add(advancementBehaviour);
    };

    protected void updateItemCapability() {
        if (itemCapability.isPresent()) return;
        inventory = new SmartInventory(9, this)
            .whenContentsChanged(i -> setInventoryChanged());
        itemCapability = LazyOptional.of(() -> inventory);
    };

    protected void updateFluidCapability() {
        if (fluidCapability.isPresent()) return;
        if (vat != null && vat.isPresent()) {
            fluidCapability = LazyOptional.of(() -> tankBehaviour.getCapability().orElse(null));
        } else {
            fluidCapability = LazyOptional.empty();
        };
    };

    public void setInventoryChanged() {
        inventoryChanged = true;
    };

    @Override
    protected AABB createRenderBoundingBox() {
		if (vat.isEmpty()) return super.createRenderBoundingBox();
        return wholeVatAABB();
	};

    @Override
    @SuppressWarnings("null")
    public void tick() {
        super.tick();

        if (initializationTicks > 0) {
            initializationTicks--;
        };

        if (getLevel().isClientSide()) { // It thinks getLevel() might be null (it's not)
            pressure.tickChaser();
            temperature.tickChaser();
        } else {
            if (getVatOptional().isEmpty()) return;
            boolean shouldUpdateFluidMixture = false;
            Vat vat = getVatOptional().get();
            if (tankBehaviour.isEmpty()) return;
            double fluidAmount = getCapacity() / 1000; // 1000 converts getFluidAmount() in mB to Buckets

            int cyclesPerTick = getSimulationLevel();

            // Heating
            for (int cycle = 0; cycle < cyclesPerTick; cycle++) {
                float energyChange = heatingPower;
                energyChange += (LevelPollution.getLocalTemperature(getLevel(), getBlockPos()) - cachedMixture.getTemperature()) * vat.getConductance(); // Fourier's Law (sort of), the divide by 20 is for 20 ticks per second
                energyChange /= 20 * cyclesPerTick;
                if (Math.abs(energyChange / (fluidAmount * cachedMixture.getVolumetricHeatCapacity())) > 0.001f && fluidAmount != 0d) { // Only bother heating if the temperature change will be somewhat significant
                    cachedMixture.heat(energyChange / (float)fluidAmount);
                } else {
                    break;
                };
            };

            // Take all Items out of the Inventory
            List<ItemStack> availableItemStacks = new ArrayList<>();
            for (int slot = 0; slot < inventory.getSlots(); slot++) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (!stack.isEmpty()) availableItemStacks.add(stack.copy());
            };

            ReactionContext context = new ReactionContext(availableItemStacks, UVPower);

            // Dissolve new Items
            if (inventoryChanged) {
                cachedMixture.dissolveItems(context, fluidAmount);
                cachedMixture.disturbEquilibrium(); // Disturb the equilibrium anyway as even if an Item Stack is not dissolved, it may still be a new catalyst
            };
            inventory.clearContent(); // Clear all Items as they may get re-inserted

            // Reacting
            if (!cachedMixture.isAtEquilibrium()) {
                context = new ReactionContext(availableItemStacks, UVPower); // Update the context
                cachedMixture.reactForTick(context, getSimulationLevel());
                shouldUpdateFluidMixture = true;

                if (!cachedMixture.isAtEquilibrium()) advancementBehaviour.awardDestroyAdvancement(DestroyAdvancements.USE_VAT);
            };

            // Put all Items back in the Inventory
            for (ItemStack itemStack : availableItemStacks) {
                ItemHandlerHelper.insertItemStacked(inventory, itemStack, false);
            };

            inventoryChanged = false;

            if (shouldUpdateFluidMixture) {
                // Enact Reaction Results
                cachedMixture.getCompletedResults(fluidAmount).entrySet().forEach(entry -> {
                    for (int i = 0; i < entry.getValue(); i++) entry.getKey().onVatReaction(getLevel(), this);
                });
                updateFluidMixture();
            };

            // Releasing gas if there is an open vent
            VatSideBlockEntity openVent = getOpenVent();
            if (openVent != null && !getGasTank().isEmptyOrFullOfAir()) {
                PollutionHelper.pollute(getLevel(), openVent.getBlockPos().relative(openVent.direction), 10, tankBehaviour.flush(cachedMixture.getTemperature()));
                updateCachedMixture();
            };

            // Check for Explosion
            if (DestroyAllConfigs.SERVER.contraptions.vatExplodesAtHighPressure.get() && Math.abs(getPercentagePressure()) >= 1f) explode();

            sendData();
        };
    };

    public void explode() {
        explode((level, pos) -> new SmartExplosion(level, null, null, null, pos, 5, 0.6f));
    };

    public void explode(BiFunction<Level, Vec3, SmartExplosion> explosionFactory) {
        if (!(getLevel() instanceof ServerLevel serverLevel)) return;
        getVatOptional().ifPresent(vat -> {
            Vec3 center = vat.getCenter();
            deleteVat(getBlockPos());
            ExplosionHelper.explode(serverLevel, explosionFactory.apply(serverLevel, center));
        });
    };

    public static int getSimulationLevel() {
        return DestroyAllConfigs.SERVER.contraptions.simulationLevel.get();
    };

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);

        heatingPower = tag.getFloat("HeatingPower");
        UVPower = tag.getFloat("UVPower");

        // Vat
        if (tag.contains("Vat", Tag.TAG_COMPOUND)) {
            vat = Vat.read(tag.getCompound("Vat"));
            finalizeVatConstruction();
        } else {
            vat = Optional.empty();
        };
        underDeconstruction = tag.getBoolean("UnderDeconstruction");

        // Inventory
        inventory.deserializeNBT(tag.getCompound("Inventory"));
        inventoryChanged = tag.getBoolean("InventoryChanged");

        // Mixture
        if (clientPacket) {
            pressure.chase(tag.getFloat("Pressure"), 0.125f, Chaser.EXP);
            temperature.chase(tag.getFloat("Temperature"), 0.125f, Chaser.EXP);
        } else {
            if (tag.contains("VentPos", Tag.TAG_COMPOUND)) openVentPos = NbtUtils.readBlockPos(tag.getCompound("VentPos"));
            updateCachedMixture();
        };
    };

    @Override
    @SuppressWarnings("null")
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);

        tag.putFloat("HeatingPower", heatingPower);
        tag.putFloat("UVPower", UVPower);

        // Vat
        if (vat.isPresent()) {
            CompoundTag vatTag = new CompoundTag();
            vat.get().write(vatTag);
            tag.put("Vat", vatTag);
        };
        tag.putBoolean("UnderDeconstruction", underDeconstruction);

        // Inventory
        tag.put("Inventory", inventory.serializeNBT());
        tag.putBoolean("InventoryChanged", inventoryChanged);
        
        // Mixture
        if (!getLevel().isClientSide()) { // It thinks getLevel() might be null (it's not)
            tag.putFloat("Pressure", getPressure());
            tag.putFloat("Temperature", getTemperature());  
        };

        if (openVentPos != null) tag.put("VentPos", NbtUtils.writeBlockPos(openVentPos));
    };

    private void onFluidStackChanged() {
        if (!vat.isPresent()) return;
        notifyUpdate();
    };

    public Optional<Vat> getVatOptional() {
        return vat;
    };

    /**
     * Whether this Vat is able to accomodate Fluid, considering its fullness and whether or not the Vat has been initialized yet.
     */
    public boolean canFitFluid() {
        return vat.map(v -> !tankBehaviour.isFull()).orElse(false);
    };

    /**
     * Add Mixture to this Vat. This should only be done on the server side.
     * @param stack Only Mixtures can be added
     * @return The amount (in mB) of Fluid which could be or was added
     */
    public int addFluid(FluidStack stack, FluidAction action) {
        int amountAdded = fluidCapability.map(fh -> fh.fill(stack, action)).orElse(0);
        if (amountAdded != 0 && action == FluidAction.EXECUTE) {
            updateCachedMixture();
            updateGasVolume();
            sendData();
        };
        return amountAdded;
    };

    public int getCapacity() {
        if (vat.isEmpty()) return 0;
        return vat.get().getCapacity();
    };

    /**
     * Set the cached Mixture to the Mixture stored in the NBT of the contained Fluids.
     * @see VatControllerBlockEntity#updateFluidMixture Doing the opposite
     */
    public void updateCachedMixture() {
        Mixture emptyMixture = new Mixture();
        if (!getVatOptional().isPresent()) {
            cachedMixture = emptyMixture;
            return;
        };
        cachedMixture = tankBehaviour.getCombinedMixture();
    };

    /**
     * Set the Mixture stored in the NBT of the contained Fluids to the cached Mixture.
     * @see VatControllerBlockEntity#updateCachedMixture Doing the opposite
     */
    private void updateFluidMixture() {
        if (getVatOptional().isEmpty()) return;
        tankBehaviour.setMixture(cachedMixture, vat.get().getCapacity()); //TODO swap Fluid to not use entire vat capacity
        updateGasVolume();
        sendData();
    };

    /**
     * Ensure the gas contained in this Vat takes up all space left by the liquid.
     */
    public void updateGasVolume() {
        tankBehaviour.updateGasVolume();
    };

    /**
     * Try to make a {@link com.petrolpark.destroy.util.vat.Vat Vat} attached to this Vat Controller.
     */
    @SuppressWarnings("null") // It thinks getLevel() might be null (it's not)
    public boolean tryMakeVat() {
        if (!hasLevel() || getLevel().isClientSide()) return false;

        // Create the Vat starting with the Block behind the Controller
        BlockPos vatInternalStartPos = new BlockPos(getBlockPos().relative(getLevel().getBlockState(getBlockPos()).getValue(VatControllerBlock.FACING).getOpposite()));
        Optional<Vat> newVat = Vat.tryConstruct(getLevel(), vatInternalStartPos, getBlockPos());
        if (!newVat.isPresent()) return false;

        updateItemCapability();

        // Once the Vat has been successfully created...
        Collection<BlockPos> sides = newVat.get().getSideBlockPositions();
        // For each Block which makes up a side of this Vat...
        sides.forEach(pos -> {
            BlockState oldState = getLevel().getBlockState(pos);
            if (oldState.is(DestroyBlocks.VAT_CONTROLLER.get())) return;
            // ...replace it with a Vat Side Block which imitates the Block it's replacing
            getLevel().setBlockAndUpdate(pos, DestroyBlocks.VAT_SIDE.getDefaultState());
            getLevel().getBlockEntity(pos, DestroyBlockEntityTypes.VAT_SIDE.get()).ifPresent(vatSide -> {
                // Configure the Vat Side
                vatSide.direction = newVat.get().whereIsSideFacing(pos);
                vatSide.setMaterial(oldState);
                vatSide.setConsumedItem(new ItemStack(oldState.getBlock().asItem())); // Required to co-operate with the Copycat Block's internals
                vatSide.controllerPosition = getBlockPos();
                BlockPos adjacentPos = pos.relative(vatSide.direction);
                vatSide.refreshFluidCapability();
                vatSide.updateDisplayType(adjacentPos);
                vatSide.setPowerFromAdjacentBlock(adjacentPos);
                vatSide.refreshItemCapability();
                vatSide.notifyUpdate();
            });
        });

        vat = Optional.of(newVat.get());
        finalizeVatConstruction();
        updateCachedMixture();
        tankBehaviour.flush(cachedMixture.getTemperature());
        updateCachedMixture();

        return true;
    };

    private void finalizeVatConstruction() {
        tankBehaviour.allowExtraction(); // Enable extraction from the Vat now it actually exists
        tankBehaviour.setVatCapacity(vat.get().getCapacity());
        updateFluidCapability();
        invalidateRenderBoundingBox(); // Update the render box to be larger
        notifyUpdate();
    };

    @Override
    public void destroy() {
        deleteVat(getBlockPos());
        super.destroy();
    };

    @SuppressWarnings("null") // It thinks getLevel() might be null (it's not)
    public void deleteVat(BlockPos posDestroyed) {
        if (underDeconstruction || getLevel().isClientSide()) return;
        underDeconstruction = true;

        BlockPos pollutionPos = getBlockPos().relative(getBlockState().getValue(VatControllerBlock.FACING));
        Optional<VatSideBlockEntity> vatSideOptional = getLevel().getBlockEntity(posDestroyed, DestroyBlockEntityTypes.VAT_SIDE.get());
        if (vatSideOptional.isPresent()) {
            pollutionPos = posDestroyed.relative(vatSideOptional.get().direction);
        };

        ItemHelper.dropContents(getLevel(), posDestroyed, inventory);
        itemCapability.invalidate();
        PollutionHelper.pollute(getLevel(), pollutionPos, getLiquidTank().getFluid(), getGasTank().getFluid());

        getLiquidTank().setFluid(FluidStack.EMPTY);
        getGasTank().setFluid(FluidStack.EMPTY);

        tankBehaviour.forbidExtraction(); // Forbid Fluid extraction now this Vat no longer exists
        if (!vat.isPresent()) return;
        vat.get().getSideBlockPositions().forEach(pos -> {
            if (!pos.equals(posDestroyed)) {
                getLevel().getBlockEntity(pos, DestroyBlockEntityTypes.VAT_SIDE.get()).ifPresent(vatSide -> {
                    BlockState newState = vatSide.getMaterial();
                    getLevel().setBlockAndUpdate(pos, newState);
                });
            };
        });
        heatingPower = 0f;
        UVPower = 0f;

        cachedMixture = new Mixture();
        vat = Optional.empty();
        underDeconstruction = false;
        invalidateRenderBoundingBox(); // Update the render bounding box to be smaller
        notifyUpdate();
    };

    // Nullable, just not annotated so VSC stops giving me ugly yellow lines
    public VatFluidTank getLiquidTank() {
        return tankBehaviour.getLiquidHandler();
    };

    public VatFluidTank getGasTank() {
        return tankBehaviour.getGasHandler();
    };

    @Nullable
    @SuppressWarnings("null")
    public VatSideBlockEntity getOpenVent() {
        if (getLevel() == null || openVentPos == null) return null;
        return getLevel().getBlockEntity(openVentPos, DestroyBlockEntityTypes.VAT_SIDE.get()).orElse(null);
    };

    /**
     * Remove the recorded open vent at the given position (if there is one) and search for a new one.
     */
    @SuppressWarnings("null")
    public void removeVent() {
        openVentPos = null;
        if (!hasLevel() || getVatOptional().isEmpty()) return;
        getVatOptional().get().getSideBlockPositions().forEach(pos -> {
            getLevel().getBlockEntity(pos, DestroyBlockEntityTypes.VAT_SIDE.get()).ifPresent(vatSide -> { // It thinks getLevel() might be null
                if (vatSide.getDisplayType() == DisplayType.OPEN_VENT) openVentPos = pos;
            });
        });
    };

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidCapability.invalidate();
        itemCapability.invalidate();
    };

    /**
     * Height (in blocks) of the Fluid level above the internal base of the Vat.
     * This is server-side.
     * @see VatControllerBlockEntity#getRenderedFluidLevel Client-side Fluid level
     */
    public float getFluidLevel() {
        if (vat.isPresent()) {
            return (float)vat.get().getInternalHeight() * (float)getLiquidTank().getFluidAmount() / (float)getCapacity();
        } else {
            return 0f;
        }
    };

    /**
     * Height (in blocks) of the Fluid level above the internal base of the Vat.
     * This is client-side.
     * @see VatControllerBlockEntity#getFluidLevel Server-side Fluid level
     */
    public float getRenderedFluidLevel(float partialTicks) {
        if (vat.isPresent()) {
            return (float)vat.get().getInternalHeight() * (float)tankBehaviour.getLiquidTank().getTotalUnits(partialTicks) / (float)getCapacity();
        } else {
            return 0f;
        }
    };

    public void changeHeatingPower(float powerChange) {
        heatingPower += powerChange;
        sendData();
    };

    public void changeUVPower(float UVChange) {
        UVPower += UVChange;
        sendData();
    };

    public ReadOnlyMixture getCombinedReadOnlyMixture() {
        return tankBehaviour.getCombinedReadOnlyMixture();
    };

    public float getClientTemperature(float partialTicks) {
        return temperature.getValue(partialTicks);
    };

    public float getClientPressure(float partialTicks) {
        return pressure.getValue(partialTicks);
    };

    @SuppressWarnings("null")
    public float getTemperature() { 
        if (getLevel().isClientSide()) return temperature.getChaseTarget(); // It thinks getLevel() might be null (it's not)
        if (getVatOptional().isEmpty() || cachedMixture == null) return LevelPollution.getLocalTemperature(getLevel(), getBlockPos());
        return cachedMixture.getTemperature();
    };

    /**
     * Get the pressure above room pressure of the gas in this Vat.
     */
    @SuppressWarnings("null")
    public float getPressure() {
        if (getLevel().isClientSide()) return pressure.getChaseTarget(); // It thinks getLevel() might be null (it's not)
        if (!getVatOptional().isPresent() || getOpenVent() != null) return 0f;
        if (getGasTank().isEmpty()) {
            return getLiquidTank().getFluidAmount() == getLiquidTank().getCapacity() ? 0f : AIR_PRESSURE; // Return 0 for a vacuum, and normal air pressure for a full Vat
        };
        return Reaction.GAS_CONSTANT * getTemperature() * ReadOnlyMixture.readNBT(ReadOnlyMixture::new, getGasTank().getFluid().getOrCreateChildTag("Mixture")).getTotalConcentration() - AIR_PRESSURE;
    };

    /**
     * Get the pressure (above air pressure) of gas in this Vat as a proportion of the {@link com.petrolpark.destroy.util.vat.Vat#getMaxPressure maximum pressure}
     * the Vat can withstand.
     */
    public float getPercentagePressure() {
        if (!getVatOptional().isPresent()) return 0f;
        Vat vat = getVatOptional().get();
        return getPressure() / vat.getMaxPressure();
    };

    public AABB wholeVatAABB() {
        return new AABB(vat.get().getInternalLowerCorner(), vat.get().getUpperCorner()).inflate(1);
    };

    @OnlyIn(Dist.CLIENT)
    @Override
    public void whenLookedAt(LocalPlayer player, BlockHitResult blockHitResult) {
        if (vat.isPresent()) {
            CreateClient.OUTLINER.showAABB(Pair.of("vat", getBlockPos()), wholeVatAABB(), 20)
                .colored(0xFF_fffec2);
        };
    };

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (getVatOptional().isPresent()) {
            vatFluidTooltip(this, tooltip);
        } else if (initializationTicks == 0) {
            TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.vat.not_initialized").component(), TooltipHelper.Palette.RED).forEach(component -> {
                DestroyLang.builder().add(component.copy()).forGoggles(tooltip);
            });
        };
        return true;
    };

    public static void vatFluidTooltip(VatControllerBlockEntity vatController, List<Component> tooltip) {
        Lang.translate("gui.goggles.fluid_container")
			.forGoggles(tooltip);
        DestroyLang.tankInfoTooltip(tooltip, DestroyLang.translate("tooltip.vat.contents_liquid"), vatController.getLiquidTank().getFluid(), vatController.getCapacity());
        DestroyLang.tankInfoTooltip(tooltip, DestroyLang.translate("tooltip.vat.contents_gas"), vatController.getGasTank().getFluid(), vatController.getCapacity());
    };

    public static class VatDisplaySource extends MixtureContentsDisplaySource {

        private final Function<VatControllerBlockEntity, FluidStack> fluidGetter;
        private final String tankId;

        private VatDisplaySource(boolean moles, String tankId, Function<VatControllerBlockEntity, FluidStack> fluidGetter) {
            super(moles);
            this.tankId = tankId;
            this.fluidGetter = fluidGetter;
        };

        @Override
        public FluidStack getFluidStack(DisplayLinkContext context) {
            VatControllerBlockEntity controller = null;
            BlockEntity be = context.getSourceBlockEntity();
            if (be instanceof VatControllerBlockEntity) controller = (VatControllerBlockEntity)be;
            if (be instanceof VatSideBlockEntity vatSide) controller = vatSide.getController();
            if (controller == null) return FluidStack.EMPTY;
            return fluidGetter.apply(controller);
        };

        @Override
        public Component getName() {
            return DestroyLang.translate("display_source.vat."+tankId).component();
        };
    };

    public static final VatDisplaySource GAS_DISPLAY_SOURCE = new VatDisplaySource(false, "gas", v -> v.getGasTank().getFluid());
    public static final VatDisplaySource SOLUTION_DISPLAY_SOURCE = new VatDisplaySource(false, "solution", v -> v.getLiquidTank().getFluid());
    public static final VatDisplaySource ALL_DISPLAY_SOURCE = new VatDisplaySource(true, "all", v -> MixtureFluid.of(v.getCapacity(), v.cachedMixture)); //TODO swap out if we decide not to use the whole vat capacity
    
};
