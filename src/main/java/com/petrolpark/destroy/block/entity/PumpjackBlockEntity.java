package com.petrolpark.destroy.block.entity;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.petrolpark.destroy.advancement.DestroyAdvancements;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.PumpjackBlock;
import com.petrolpark.destroy.block.entity.behaviour.DestroyAdvancementBehaviour;
import com.petrolpark.destroy.block.entity.behaviour.PollutingBehaviour;
import com.petrolpark.destroy.capability.chunk.ChunkCrudeOil;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.sound.DestroySoundEvents;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.DistExecutor;

public class PumpjackBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    public SmartFluidTankBehaviour tank;
    protected PollutingBehaviour pollutionBehaviour;
    protected DestroyAdvancementBehaviour advancementBehaviour;

    public WeakReference<PumpjackCamBlockEntity> source;

    private boolean upsqueak; // Client-only

    public PumpjackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        source = new WeakReference<>(null);
        upsqueak = false;
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 1, 2000, false)
            .forbidInsertion();
        behaviours.add(tank);

        advancementBehaviour = new DestroyAdvancementBehaviour(this);
        behaviours.add(advancementBehaviour);

        pollutionBehaviour = new PollutingBehaviour(this);
        behaviours.add(pollutionBehaviour);
    };

    @Override
    @SuppressWarnings("null")
    public void tick() {
        super.tick();
        PumpjackCamBlockEntity cam = getCam();

        // Updating cam
        if (!hasLevel()) return; // Don't do anything if we're not in a level
        if (cam == null) return; // Don't do anything if our cam is missing
        if (!cam.getBlockPos() // Don't do anything if our cam is pointing to the wrong place
            .subtract(getBlockPos())
            .equals(cam.pumpjackPos))
        {
            cam.update(getBlockPos());
            sendData();
            return;
        };
        Direction facing = PumpjackBlock.getFacing(getBlockState());
        if (getLevel().isLoaded(getBlockPos().relative(facing.getOpposite()))) cam.update(getBlockPos()); // It thinks getLevel() might be null (it's not)

        // Sounds
        if (getLevel().isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> playClientSound());
            return;
        };

        // Pumping oil
        if (cam.getSpeed() == 0) return; // Don't go any further if we're not pumping
        BlockPos pipePos = getBlockPos().relative(facing);
        LevelChunk chunk = level.getChunkAt(pipePos);
        LazyOptional<ChunkCrudeOil> crudeOilOptional = chunk.getCapability(ChunkCrudeOil.Provider.CHUNK_CRUDE_OIL);
        if (!crudeOilOptional.isPresent()) return; // Don't go any further if there's somehow no capability
        int oilAmount = crudeOilOptional.map(crudeOilCap -> {
            crudeOilCap.generate(chunk, advancementBehaviour.getPlayer());
            return crudeOilCap.getAmount();
        }).orElse(0);
        if (oilAmount == 0) return; // Don't go any further if there's no oil
        advancementBehaviour.awardDestroyAdvancement(DestroyAdvancements.USE_PUMPJACK);
        // Add the oil to the Pumpjack's internal tank
        tank.allowInsertion();
        int amountPumped = tank.getPrimaryHandler().fill(new FluidStack(DestroyFluids.CRUDE_OIL.get(), (int)Math.min(oilAmount, Math.abs(cam.getSpeed() / 16))), FluidAction.EXECUTE);
        tank.forbidInsertion();
        crudeOilOptional.ifPresent(crudeOilCap -> crudeOilCap.decreaseAmount(amountPumped));
    };

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && (side == null || (PumpjackBlock.getFacing(getBlockState()).getAxis() != side.getAxis() && side.getAxis() != Axis.Y))) {
            return tank.getCapability().cast();
        };
        return super.getCapability(cap, side);
    };

    public Float getTargetAngle() {
        float angle = 0;
		BlockState blockState = getBlockState();
		if (!DestroyBlocks.PUMPJACK.has(blockState)) return null;

		PumpjackCamBlockEntity cam = getCam();

		if (cam == null) return null;

        Direction facing = PumpjackBlock.getFacing(blockState);
		Axis axis = KineticBlockEntityRenderer.getRotationAxisOf(cam);
		angle = KineticBlockEntityRenderer.getAngleForTe(cam, cam.getBlockPos(), axis);

        if (axis.isHorizontal() && (facing.getAxis() == Axis.X ^ facing.getAxisDirection() == AxisDirection.NEGATIVE))
			angle *= -1;

		return angle;
    };

    @OnlyIn(Dist.CLIENT)
    public void playClientSound() {
        if (getTargetAngle() == null) return;
        Minecraft minecraft = Minecraft.getInstance();
        // Sound is syncronised to Pumpjack movement
        if (Math.abs(getTargetAngle()) <= 0.1f && upsqueak) {
            DestroySoundEvents.PUMPJACK_CREAK_1.play(getLevel(), minecraft.player, getBlockPos());
            upsqueak = false;
        };
        if (Math.abs(Math.abs(getTargetAngle()) - Mth.PI) < 0.1 && !upsqueak) {
            DestroySoundEvents.PUMPJACK_CREAK_2.play(getLevel(), minecraft.player, getBlockPos());
            upsqueak = true;
        };
    };

    @Nullable
    @SuppressWarnings("null")
    public PumpjackCamBlockEntity getCam() {
        PumpjackCamBlockEntity cam = source.get();
        if (cam == null || cam.isRemoved() || !cam.canPower(getBlockPos())) {
            if (cam != null) source = new WeakReference<>(null);
            Direction facing = PumpjackBlock.getFacing(getBlockState());
            BlockEntity anyCamAt = getLevel().getBlockEntity(getBlockPos().relative(facing, 1)); // It thinks getLevel() might be null
            if (anyCamAt instanceof PumpjackCamBlockEntity newCam && newCam.canPower(getBlockPos())) {
                cam = newCam;
				source = new WeakReference<>(cam);
            };
        };
        return cam;
    };

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return containedFluidTooltip(tooltip, isPlayerSneaking, getCapability(ForgeCapabilities.FLUID_HANDLER));
    };
    
};
