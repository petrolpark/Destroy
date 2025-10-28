package com.petrolpark.destroy.content.processing.distillation;

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyAdvancementTrigger;
import com.petrolpark.destroy.DestroySoundEvents;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.client.DestroyLang.TemperatureUnit;
import com.petrolpark.destroy.client.DestroyParticleTypes;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.core.block.entity.IDirectionalOutputFluidBlockEntity;
import com.petrolpark.destroy.core.block.entity.IHaveLabGoggleInformation;
import com.petrolpark.destroy.core.chemistry.MixtureContentsDisplaySource;
import com.petrolpark.destroy.core.data.advancement.DestroyAdvancementBehaviour;
import com.petrolpark.destroy.core.fluid.GeniusFluidTankBehaviour;
import com.petrolpark.destroy.core.fluid.gasparticle.GasParticleData;
import com.petrolpark.destroy.core.pollution.PollutingBehaviour;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.fluid.SmartFluidTank;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class BubbleCapBlockEntity extends SmartBlockEntity implements IHaveLabGoggleInformation, IDirectionalOutputFluidBlockEntity, ThresholdSwitchObservable {

    private static final DecimalFormat df = new DecimalFormat();
    static {
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(0);
    };

    private static final int TRANSFER_SPEED = 20; // The rate (mB/tick) at which Fluid is transferred from the internal Tank to the actual Tank

    private int fraction; // Where in the Tower this Bubble Cap is (0 = base (controller), 1 = first fraction, etc)
    private int ticksToFill; // How long before this Bubble Cap should start transferring from its internal Tank to its actual Tank (allowing for the illusion of Fluid 'moving up' the Tower)
    public FluidStack particleFluid; // Which Fluid to use if we have to make particles

    private boolean isController;
    private BlockPos towerControllerPos;
    private DistillationTower tower;

    protected SmartFluidTankBehaviour internalTank, tank;
    protected LazyOptional<IFluidHandler> allFluidCapability;

    public DestroyAdvancementBehaviour advancementBehaviour;
    protected PollutingBehaviour pollutingBehaviour;

    private int initializationTicks;

    public BubbleCapBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        fraction = 0;
        isController = false;
        towerControllerPos = pos; // Temporary assignment
        ticksToFill = 0;
        particleFluid = FluidStack.EMPTY;
        initializationTicks = 3;
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = new GeniusFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 1, getTankCapacity(), true)
            .whenFluidUpdates(this::notifyUpdate);
        internalTank = new GeniusFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, getTankCapacity(), true)
            .forbidExtraction()
            .forbidInsertion()
            .whenFluidUpdates(this::notifyUpdate);
        behaviours.add(tank);
        behaviours.add(internalTank);
        allFluidCapability = LazyOptional.of(() -> { // For Polluting Behaviour, we need access to all tanks
			return new CombinedTankWrapper(tank.getCapability().orElse(null), internalTank.getCapability().orElse(null));
		});

        advancementBehaviour = new DestroyAdvancementBehaviour(this, DestroyAdvancementTrigger.DISTILL);
        behaviours.add(advancementBehaviour);

        pollutingBehaviour = new PollutingBehaviour(this);
        behaviours.add(pollutingBehaviour);
    };

    @Override
    @SuppressWarnings("null")
    public void remove() {
        if (!hasLevel() || getLevel().isClientSide()) super.remove(); // It thinks getLevel() can be null (it can't)
        removeFromDistillationTower();
        super.remove();
    };

    @Override
    @SuppressWarnings("null")
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        if (!hasLevel()) return;
        fraction = compound.getInt("Fraction");
        int[] controllerPosArray = compound.getIntArray("DistillationTowerControllerPosition");
        if (controllerPosArray.length == 0) {
            towerControllerPos = getBlockPos();
        } else {
            towerControllerPos = new BlockPos(controllerPosArray[0], controllerPosArray[1], controllerPosArray[2]);
        };

        // Load Tower if this is the controller
        if (compound.contains("DistillationTower")) {
            isController = true;
            tower = new DistillationTower(compound.getCompound("DistillationTower"), getLevel(), getBlockPos());
        };
        // Load Tower if this isn't the controller
        BlockEntity be = getLevel().getBlockEntity(towerControllerPos); // It thinks getLevel() might be null
        if (be instanceof BubbleCapBlockEntity controllerBubbleCap && controllerBubbleCap.isController) {
            tower = controllerBubbleCap.getDistillationTower();
        };

        if (clientPacket) { // If we need to tell the client to start rendering particles
            particleFluid = FluidStack.loadFluidStackFromNBT(compound.getCompound("ParticleFluid"));
        };

        ticksToFill = compound.getInt("TicksToFill");
        initializationTicks = compound.getInt("InitializationTicks");
    };

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("Fraction", fraction);
        compound.putIntArray("DistillationTowerControllerPosition", List.of(towerControllerPos.getX(), towerControllerPos.getY(), towerControllerPos.getZ()));
        if (isController) {
            compound.put("DistillationTower", tower.serializeNBT());
        };
        compound.putInt("TicksToFill", ticksToFill);
        if (clientPacket) {
            compound.put("ParticleFluid", particleFluid.writeToNBT(new CompoundTag()));
        };
        compound.putInt("InitializationTicks", initializationTicks);
    };

    @Override
    @SuppressWarnings("null")
    public void tick() {
        super.tick();
        if (!particleFluid.isEmpty()) {
            if (getLevel().isClientSide()) { // It thinks getLevel() might be null (it can't be).
                spawnParticles(particleFluid);
            };
            particleFluid = FluidStack.EMPTY; // Reset this, we've made them now
        };
        if (initializationTicks > 0) initializationTicks--;
        if (ticksToFill > 0) {
            ticksToFill--;
            if (ticksToFill == 0) {
                DestroySoundEvents.DISTILLATION_TOWER_CONDENSE.playOnServer(getLevel(), getBlockPos());
            };
        };
        if (ticksToFill <= 0 && !internalTank.isEmpty()) {
            internalTank.allowInsertion();
            FluidStack transferredFluid = getInternalTank().drain(TRANSFER_SPEED, FluidAction.EXECUTE);
            internalTank.forbidInsertion();
            getTank().fill(transferredFluid, FluidAction.EXECUTE);
        };
        if (!hasLevel()) return;
        if (isController && hasLevel()) {
            tower.tick(getLevel());
        };
        sendData();
    };

    public void onDistill() {
        if (!isController) return;
        DestroySoundEvents.DISTILLATION_TOWER_BOIL.playOnServer(level, getBlockPos());
        advancementBehaviour.awardDestroyAdvancement(DestroyAdvancementTrigger.DISTILL);
    };

    public static int getTankCapacity() {
        return DestroyAllConfigs.SERVER.blocks.bubbleCapCapacity.get();
    };

    /**
     * The rate (mB/tick) at which Fluid is transferred from this Bubble Cap's internal Tank to its actual Tank.
     */
    public static int getTransferRate() {
        return TRANSFER_SPEED;
    };

    public SmartFluidTank getTank() {
        return tank.getPrimaryHandler();
    };

    public SmartFluidTank getInternalTank() {
        return internalTank.getPrimaryHandler();
    };

    public TankSegment getTankToRender() {
        return tank.getPrimaryTank();
    };

    public int getLuminosity() {
        if (getTank().isEmpty()) return 0;
        FluidStack fluidStack = getTank().getFluid();
        return fluidStack.getRawFluid().getFluidType().getLightLevel(fluidStack);
    };


    @Override
    public int getMaxValue() {
        return getTank().getCapacity();
    }

    @Override
    public int getMinValue() {
        return 0;
    }

    @Override
    public int getCurrentValue() {
        return getTank().getFluidAmount();
    }

    @Override
    public MutableComponent format(int value) {
        return DestroyLang.translateDirect("gui.bubble_cap.bubble_cap_liquid_amount", value);
    }

    /**
     * Set the amount of time before Fluid from the internal Tank will start being transferred to the actual Tank.
     * Used to create the illusion of Fluid 'moving up' the Tower.
     * @param ticks
     */
    public void setTicksToFill(int ticks) {
        ticksToFill = ticks;
    };

    /**
     * Mark the Distillation Tower as containing this Bubble Cap, and let this Bubble Cap know where in the Tower it is (including whether it is the controller).
     */
    public void addToDistillationTower(DistillationTower tower) {
        this.tower = tower;
        this.towerControllerPos = tower.getControllerPos();
        this.fraction = tower.getHeight();
        if (fraction == 0) { // If this is the first Bubble Cap in the Tower, it must be the controller.
            isController = true;
        } else {
            isController = false;
        };
        sendData();
    };

    @SuppressWarnings("null")
    public void spawnParticles(FluidStack fluidStack) {
        Vec3 center = VecHelper.getCenterOf(getBlockPos());
        if (!(hasLevel() && getLevel().isClientSide() && isController)) return; // It thinks getLevel() can be null (it can't)
        GasParticleData particleData = new GasParticleData(DestroyParticleTypes.DISTILLATION.get(), fluidStack, getDistillationTower().getHeight() - 1.3f);
        for (int i = 0; i < 10; i++) {
            getLevel().addParticle(particleData, center.x, center.y - 0.3f, center.z, 0, 0, 0); // It thinks 'getLevel()' might be null (it can't be at this point)
        };
    };

    public void removeFromDistillationTower() {
        if (tower == null) return;
        tower.removeBubbleCap(this);
    };

    /**
     * Used when the Bubble Cap is placed or the Bubble Cap below is broken.
     * @param level The Level in which this Bubble Cap is
     */
    public void createOrAddToTower() {
        BlockEntity belowBE = getLevel().getBlockEntity(getBlockPos().below());
        if (belowBE == null || !(belowBE instanceof BubbleCapBlockEntity bubbleCapBelow) || bubbleCapBelow.getDistillationTower() == null) { // If this should start a new Distillation Tower
            tower = new DistillationTower(getLevel(), getBlockPos());
        } else { // If this should add to an existing Distillation Tower (because it has just been placed, or because the Bubble Cap below has just become part of a different Distillation Tower)
            bubbleCapBelow.getDistillationTower().addBubbleCap(this);
        };
    };

    /**
     * Get the Distillation Tower of which this Bubble Cap is a part, assigning the Tower if necessary.
     */
    public DistillationTower getDistillationTower() {
        if (!hasLevel()) {
            Destroy.LOGGER.warn("Tried to access Distillation Tower of Bubble Cap at "+getBlockPos().toShortString()+" but it has no assigned Level.");
            return null;
        };
        return tower;
    };

    @Nonnull
    @Override
    @SuppressWarnings("null")
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (side == null) { // For Polluting Behaviour, we need access to all Fluid Tanks
                return allFluidCapability.cast();
            } else if (side.getAxis() != Axis.Y) {
                return tank.getCapability().cast();
            };

        };
        return super.getCapability(cap, side);
    };

    @Override
    public void invalidate() {
        super.invalidate();
        allFluidCapability.invalidate();
    };

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (initializationTicks > 0) return false;
        DistillationTower clientTower = getDistillationTower();
        if (clientTower == null) return false;
        if (clientTower.getControllerBubbleCap() == null) return false;

        // Label this Bubble Cap
        if (isController) {
            DestroyLang.translate("tooltip.bubble_cap.reboiler")
                .style(ChatFormatting.WHITE)
                .forGoggles(tooltip);
        } else {
            DestroyLang.builder()
                .add(Component.translatable("block.destroy.bubble_cap"))
                .style(ChatFormatting.WHITE)
                .space()
                .add(Component.literal(""+fraction+"/"+(clientTower.getHeight() - 1)))
                .forGoggles(tooltip);
        };

        SmartFluidTank inputTank = clientTower.getControllerBubbleCap().getTank();

        // Add Fluid info header
        DestroyLang.fluidContainerInfoHeader(tooltip);

        // Add contents 
        if (!isController) DestroyLang.tankInfoTooltip(tooltip, DestroyLang.translate("tooltip.bubble_cap.output_tank"), getTank());
        DestroyLang.tankInfoTooltip(tooltip, DestroyLang.translate("tooltip.bubble_cap.input_tank"), inputTank);

        TemperatureUnit unit = DestroyAllConfigs.CLIENT.chemistry.temperatureUnit.get();
        if (isController) DestroyLang.translate("tooltip.bubble_cap.reboiler_temperature", unit.of(DistillationTower.getTemperatureForDistillationTower(getLevel(), worldPosition), df)).forGoggles(tooltip);

        return true;
    };


    public static class BubbleCapDisplaySource extends MixtureContentsDisplaySource {
        public BubbleCapDisplaySource() {
            super(false);
        };

        @Override
        public FluidStack getFluidStack(DisplayLinkContext context) {
            if (context.getSourceBlockEntity() instanceof BubbleCapBlockEntity bubbleCap) {
                return bubbleCap.getTank().getFluid();
            } else {
                return null;
            }
        };

        @Override
        public Component getName() {
            return DestroyLang.translate("display_source.bubble_cap").component();
        };
    }
};
