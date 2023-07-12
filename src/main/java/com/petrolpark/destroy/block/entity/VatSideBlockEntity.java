package com.petrolpark.destroy.block.entity;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.capability.blockEntity.VatTankCapability;
import com.petrolpark.destroy.util.DestroyLang;
import com.petrolpark.destroy.util.vat.Vat;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class VatSideBlockEntity extends CopycatBlockEntity implements IHaveGoggleInformation {

    private static final int BUFFER_TANK_CAPACITY = 1000;

    private static final DecimalFormat df = new DecimalFormat();
    static {
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(1);
    };

    protected SmartFluidTankBehaviour inputBehaviour;
    protected LazyOptional<IFluidHandler> fluidCapability;

    public Direction direction; // The outward direction this side is facing
    public BlockPos controllerPosition;

    protected DisplayType displayType;

    public VatSideBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        displayType = DisplayType.NORMAL;
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        inputBehaviour = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.TYPE, this, 1, BUFFER_TANK_CAPACITY, false)
            .whenFluidUpdates(this::tryInsertFluidInVat)
            .forbidExtraction();
        behaviours.add(inputBehaviour);
        refreshFluidCapability();
    };

    @Override
    public void destroy() {
        super.destroy();
        VatControllerBlockEntity vatController = getController();
        if (vatController != null && !vatController.underDeconstruction) vatController.deleteVat(getBlockPos());
    };

    protected void refreshFluidCapability() {
        fluidCapability = LazyOptional.of(() -> {
            // Allow inserting into this side tank (which is then mixed into the main tank)
            LazyOptional<? extends IFluidHandler> inputCap = inputBehaviour.getCapability();
            // Allow withdrawing from the main tank
            LazyOptional<? extends IFluidHandler> outputCap = LazyOptional.empty();
            VatControllerBlockEntity vatController = getController();
            if (vatController != null) {
                outputCap = vatController.getBehaviour(SmartFluidTankBehaviour.TYPE).getCapability();
            };
			return new VatTankCapability(outputCap.orElse(null), inputCap.orElse(null));
        });
    };

    @Override
    public void invalidateCaps() {
        fluidCapability.invalidate();
    };

    @Nullable
    @SuppressWarnings("null")
    public VatControllerBlockEntity getController() {
        if (!hasLevel() || controllerPosition == null) return null;
        BlockEntity be = getLevel().getBlockEntity(controllerPosition);
        if (!(be instanceof VatControllerBlockEntity vatController)) return null;
        return vatController;
    };

    public Optional<Vat> getVatOptional() {
        VatControllerBlockEntity vatController = getController();
        if (vatController == null) return Optional.empty();
        return vatController.getVatOptional();
    };

    public void tryInsertFluidInVat() {
        VatControllerBlockEntity vatController = getController();
        if (vatController == null) return;
        // Attempt to transfer Fluid from this Vat Side Block Entity to the Controller's Tank, which is the main one
        inputBehaviour.allowExtraction();
        // Determine how much Fluid could be added to the main tank (this should usually be everything)
        int drained = vatController.addFluid(inputBehaviour.getPrimaryHandler().drain(BUFFER_TANK_CAPACITY, FluidAction.SIMULATE));
        // Drain that amount from this Vat Side Block Entity's Tank
        inputBehaviour.getPrimaryHandler().drain(drained, FluidAction.EXECUTE);
        inputBehaviour.forbidExtraction();
    };

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (tag.contains("Side")) {
            direction = Direction.values()[tag.getInt("Side")];
        } else {
            direction = null;
        }
        controllerPosition = NbtUtils.readBlockPos(tag.getCompound("ControllerPosition"));
        displayType = DisplayType.values()[tag.getInt("DisplayType")];
    };

    @Override
    public void tick() {
        super.tick();
        VatControllerBlockEntity controller = getController();
        if (controller == null || controller.full) {
            inputBehaviour.forbidInsertion();
        } else {
            inputBehaviour.allowInsertion();
        };
    };

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (direction != null) tag.putInt("Side", direction.ordinal());
        if (controllerPosition != null) tag.put("ControllerPosition", NbtUtils.writeBlockPos(controllerPosition));
        tag.putInt("DisplayType", displayType.ordinal());
    };

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && side == direction && (displayType == DisplayType.NORMAL || displayType == DisplayType.PIPE)) {
            return fluidCapability.cast();
        };
        return super.getCapability(cap, side);
    };

    /**
     * @see VatControllerBlockEntity#getPercentagePressure()
     */
    public float getPercentagePressure() {
        VatControllerBlockEntity controller = getController();
        if (controller == null) return 0f;
        return controller.getPercentagePressure();
    };

    @Override
    public void setMaterial(BlockState blockState) {
        if (blockState.is(DestroyBlocks.VAT_SIDE.get())) return;
        super.setMaterial(blockState);
    };

    public static enum DisplayType {
        NORMAL,
        BAROMETER,
        THERMOMETER,
        PIPE
    };

    public DisplayType getDisplayType() {
        return displayType;
    };

    public void setDisplayType(DisplayType displayType) {
        if (this.displayType == displayType) return;
        this.displayType = displayType;
        if (!hasLevel()) return;
        getBlockState().updateNeighbourShapes(getLevel(), getBlockPos(), 3);
        updateDisplayType(getBlockPos().relative(direction)); // Check for a Pipe
        sendData();
    };

    @SuppressWarnings("null")
    public void updateDisplayType(BlockPos neighborPos) {
        if (getController() == null) return;
        FluidTransportBehaviour behaviour = BlockEntityBehaviour.get(getLevel(), neighborPos, FluidTransportBehaviour.TYPE);
        boolean nextToPipe = behaviour == null ? false : behaviour.canHaveFlowToward(getBlockState(), direction) || behaviour.canPullFluidFrom(getController().getTank().getFluid(), getBlockState(), direction);
        if (getDisplayType() == DisplayType.NORMAL && nextToPipe) {
            setDisplayType(DisplayType.PIPE);
        } else if (getDisplayType() == DisplayType.PIPE && !nextToPipe) {
            setDisplayType(DisplayType.NORMAL);
        };
    };

    @Override
    @SuppressWarnings("null")
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (!getVatOptional().isPresent() || getController() == null) return false;
        switch (getDisplayType()) {
            case THERMOMETER: {
                DestroyLang.translate("tooltip.vat.temperature", df.format(getController().getTemperature())).style(ChatFormatting.WHITE).forGoggles(tooltip);
                return true;
            } case BAROMETER: {
                Vat vat = getVatOptional().get();
                DestroyLang.translate("tooltip.vat.pressure.header").style(ChatFormatting.WHITE).forGoggles(tooltip);
                DestroyLang.translate("tooltip.vat.pressure.current", df.format(getController().getPressure() / 1000f)).style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
                DestroyLang.translate("tooltip.vat.pressure.max", df.format(vat.getMaxPressure() / 1000f), vat.getWeakestBlock().getBlock().getName().getString()).style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
                return true;
            } default: {
                DestroyLang.tankInfoTooltip(tooltip, DestroyLang.translate("tooltip.vat.contents"), getController().getTank().getFluid(), getController().getCapacity());
                return true;
            }
        }
    };
};
