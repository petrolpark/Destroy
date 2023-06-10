package com.petrolpark.destroy.block.entity;

import java.lang.ref.WeakReference;
import java.util.List;

import com.mongodb.lang.Nullable;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.PumpjackBlock;
import com.petrolpark.destroy.sound.DestroySoundEvents;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

public class PumpjackBlockEntity extends SmartBlockEntity {

    public WeakReference<PumpjackCamBlockEntity> source;

    private boolean squeak; // Client-only

    public PumpjackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        source = new WeakReference<>(null);
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        
    };

    @Override
    @SuppressWarnings("null")
    public void tick() {
        super.tick();
        PumpjackCamBlockEntity cam = getCam();

        if (!hasLevel()) return; // Don't do anything if we're not in a level
        if (cam == null) return; // Don't do anything if our cam is missing
        if (!cam.getBlockPos() // Don't do anything if our cam is pointing to the wrong place
            .subtract(worldPosition)
            .equals(cam.pumpjackPos))
            return;
        Direction facing = PumpjackBlock.getFacing(getBlockState());
        if (getLevel().isLoaded(worldPosition.relative(facing.getOpposite()))) cam.update(worldPosition);
        
        if (getLevel().isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> playClientSound());
        };

        return;
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
    @SuppressWarnings("resource")
    public void playClientSound() {
        // Sound is syncronised to Pumpjack movement
        if (getTargetAngle() - Mth.PI < 0.0001 && squeak) {
            DestroySoundEvents.PUMPJACK_CREAK_1.play(getLevel(), Minecraft.getInstance().player, getBlockPos());
            squeak = false;
        } else if (getTargetAngle() + Mth.PI < 0.0001 && squeak) {
            squeak = false;
            DestroySoundEvents.PUMPJACK_CREAK_2.play(getLevel(), Minecraft.getInstance().player, getBlockPos());
        } else {
            squeak = true;
        };
    };

    @Nullable
    @SuppressWarnings("null")
    public PumpjackCamBlockEntity getCam() {
        PumpjackCamBlockEntity cam = source.get();
        if (cam == null || cam.isRemoved() || !cam.canPower(worldPosition)) {
            if (cam != null) source = new WeakReference<>(null);
            Direction facing = PumpjackBlock.getFacing(getBlockState());
            BlockEntity anyCamAt = getLevel().getBlockEntity(worldPosition.relative(facing, 1)); // It thinks getLevel() might be null
            if (anyCamAt instanceof PumpjackCamBlockEntity newCam && newCam.canPower(worldPosition)) {
                cam = newCam;
				source = new WeakReference<>(cam);
            };
        };
        return cam;
    };
    
};
