package com.petrolpark.destroy.block.entity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.VatControllerBlock;
import com.petrolpark.destroy.block.display.MixtureContentsDisplaySource;
import com.petrolpark.destroy.block.entity.behaviour.WhenTargetedBehaviour;
import com.petrolpark.destroy.block.entity.behaviour.fluidTankBehaviour.VatFluidTankBehaviour;
import com.petrolpark.destroy.block.entity.behaviour.fluidTankBehaviour.VatFluidTankBehaviour.VatTankSegment.VatFluidTank;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.util.DestroyLang;
import com.petrolpark.destroy.util.vat.Vat;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.Pair;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class VatControllerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

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
     * As the client side doesn't have access to the cached Mixture, store the pressure and temperature.
     */
    protected float pressure;
    protected float temperature;

    protected VatFluidTankBehaviour tankBehaviour;
    protected LazyOptional<IFluidHandler> fluidCapability;

    public ItemStackHandler inventory;
    protected LazyOptional<IItemHandler> itemCapability;

    protected WhenTargetedBehaviour targetedBehaviour;

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

        inventory = new ItemStackHandler(9);
		itemCapability = LazyOptional.of(() -> inventory);
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        targetedBehaviour = new WhenTargetedBehaviour(this, this::onTargeted);
        behaviours.add(targetedBehaviour);

        tankBehaviour = new VatFluidTankBehaviour(this, 1000000); // Tank capacity is set very high but is not this high in effect
        tankBehaviour.whenFluidUpdates(this::onFluidStackChanged)
            .forbidExtraction() // Forbid extraction until the Vat is initialized
            .forbidInsertion(); // Forbid insertion no matter what
        fluidCapability = LazyOptional.empty();

        behaviours.add(tankBehaviour);
    };

    protected void updateFluidCapability() {
        if (fluidCapability.isPresent()) return;
        if (vat != null && vat.isPresent()) {
            fluidCapability = LazyOptional.of(() -> tankBehaviour.getCapability().orElse(null));
        } else {
            fluidCapability = LazyOptional.empty();
        };
    };

    @Override
    protected AABB createRenderBoundingBox() {
		if (vat.isEmpty()) return super.createRenderBoundingBox();
        return wholeVatAABB();
	};

    @Override
    public void tick() {
        super.tick();

        if (initializationTicks > 0) {
            initializationTicks--;
        };

        boolean shouldUpdateFluidMixture = false;

        if (getVatOptional().isEmpty() || level.isClientSide()) return;
        Vat vat = getVatOptional().get();
        if (tankBehaviour.isEmpty()) return;
        float energyChange = heatingPower / 20;
        energyChange += (LevelPollution.getLocalTemperature(getLevel(), getBlockPos()) - cachedMixture.getTemperature()) * vat.getConductance() / 20; // Fourier's Law (sort of), the divide by 20 is for 20 ticks per second
        // if (Math.abs(energyChange) > 0.0001f) {
        //     cachedMixture.heat(1000 * energyChange / getTank().getFluidAmount()); // 1000 converts getFluidAmount() in mB to Buckets
        // };
        // if (!cachedMixture.isAtEquilibrium()) {
        //     cachedMixture.reactForTick(new ReactionContext(List.of())); //TODO items in vats
        //     getTank().getFluid().setAmount(cachedMixture.recalculateVolume(getTank().getFluid().getAmount()));
        //     shouldUpdateFluidMixture = true;
        // };

        if (shouldUpdateFluidMixture) updateFluidMixture();
        sendData();
    };

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        heatingPower = tag.getFloat("HeatingPower");
        if (tag.contains("Vat", Tag.TAG_COMPOUND)) {
            vat = Vat.read(tag.getCompound("Vat"));
            finalizeVatConstruction();
        } else {
            vat = Optional.empty();
        };
        underDeconstruction = tag.getBoolean("UnderDeconstruction");
        inventory.deserializeNBT(tag.getCompound("Inventory"));
        if (clientPacket) {
            pressure = tag.getFloat("Pressure");
            temperature = tag.getFloat("Temperature");
        } else {
            updateCachedMixture();
        };
    };

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putFloat("HeatingPower", heatingPower);
        if (vat.isPresent()) {
            CompoundTag vatTag = new CompoundTag();
            vat.get().write(vatTag);
            tag.put("Vat", vatTag);
        };
        tag.putBoolean("UnderDeconstruction", underDeconstruction);
        tag.put("Inventory", inventory.serializeNBT());
        
        if (!level.isClientSide()) {
            tag.putFloat("Pressure", getPressure());
            tag.putFloat("Temperature", getTemperature());  
        };
    };

    private void onFluidStackChanged() {
        if (!vat.isPresent()) return;
        sendData();
    };

    public Optional<Vat> getVatOptional() {
        return vat;
    };

    public boolean canFitFluid() {
        return vat.map(v -> tankBehaviour.isFull()).orElse(false);
    };

    public int addFluid(FluidStack stack) {
        int amountAdded = fluidCapability.map(fh -> fh.fill(stack, FluidAction.EXECUTE)).orElse(0);
        updateCachedMixture();
        return amountAdded;
    };

    public int getCapacity() {
        if (vat.isEmpty()) return 0;
        return vat.get().getCapacity();
    };

    /**
     * Set the cached Mixture to the Mixture stored in the NBT of the contained Fluid.
     * @see VatControllerBlockEntity#updateFluidMixture Doing the opposite
     */
    private void updateCachedMixture() {
        Mixture emptyMixture = new Mixture();
        if (!getVatOptional().isPresent()) {
            cachedMixture = emptyMixture;
            return;
        };
        cachedMixture = tankBehaviour.getCombinedMixture();
    };

    /**
     * Set the Mixture stored in the NBT of the contained Fluid to the cached Mixture.
     * @see VatControllerBlockEntity#updateCachedMixture Doing the opposite
     */
    private void updateFluidMixture() {
        tankBehaviour.setMixture(cachedMixture, vat.get().getCapacity()); //TODO swap Fluid to not use entire vat capacity
    };

    /**
     * Try to make a {@link com.petrolpark.destroy.util.vat.Vat Vat} attached to this Vat Controller.
     */
    public boolean tryMakeVat() {
        if (!hasLevel() || getLevel().isClientSide()) return false;

        // Create the Vat starting with the Block behind the Controller
        BlockPos vatInternalStartPos = new BlockPos(getBlockPos().relative(getLevel().getBlockState(getBlockPos()).getValue(VatControllerBlock.FACING).getOpposite()));
        Optional<Vat> newVat = Vat.tryConstruct(getLevel(), vatInternalStartPos, getBlockPos());
        if (!newVat.isPresent()) return false;

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

        return true;
    };

    private void finalizeVatConstruction() {
        tankBehaviour.allowExtraction(); // Enable extraction from the Vat now it actually exists
        tankBehaviour.setVatCapacity(vat.get().getCapacity());
        updateFluidCapability();
        updateCachedMixture();
        invalidateRenderBoundingBox(); // Update the render box to be larger
        notifyUpdate();
    };

    @Override
    public void destroy() {
        deleteVat(getBlockPos());
        super.destroy();
    };

    public void deleteVat(BlockPos posDestroyed) {
        if (underDeconstruction || getLevel().isClientSide()) return;
        underDeconstruction = true;

        getLiquidTank().setFluid(FluidStack.EMPTY);
        getGasTank().setFluid(FluidStack.EMPTY);

        tankBehaviour.forbidExtraction(); // Forbid Fluid extraction now this Vat no longer exists
        if (!vat.isPresent()) return;
        vat.get().getSideBlockPositions().forEach(pos -> {
            if (!pos.equals(posDestroyed)) {
                getLevel().getBlockEntity(pos, DestroyBlockEntityTypes.VAT_SIDE.get()).ifPresent(vatSide -> {
                    BlockState newState = vatSide.getMaterial();
                    getLevel().removeBlock(pos, false);
                    getLevel().setBlockAndUpdate(pos, newState);
                });
            };
        });
        heatingPower = 0f;

        //TODO evaporation

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

    public float getTemperature() {
        if (getLevel().isClientSide()) return temperature;
        if (getVatOptional().isEmpty() || cachedMixture == null) return LevelPollution.getLocalTemperature(getLevel(), getBlockPos());
        return cachedMixture.getTemperature();
    };

    public float getPressure() {
        if (getLevel().isClientSide()) return pressure;
        if (!getVatOptional().isPresent()) return 0f;
        float moles = 1f; // TODO calculate moles of gas
        return moles * Reaction.GAS_CONSTANT * getTemperature() / vat.get().getVolume();
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

    private void onTargeted(LocalPlayer player, BlockHitResult blockHitResult) {
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
        //TODO swap to not just use liquids
    };

    public static MixtureContentsDisplaySource LIQUID_DISPLAY_SOURCE = new MixtureContentsDisplaySource() {

        @Override
        public FluidStack getFluidStack(DisplayLinkContext context) {
            if (context.getSourceBlockEntity() instanceof VatControllerBlockEntity controller) {
                if (controller.getVatOptional().isPresent()) return controller.getLiquidTank().getFluid();
            };
            return FluidStack.EMPTY;
        };

        @Override
        public Component getName() {
            return DestroyLang.translate("display_source.vat").component();
        };

    };
    
};
