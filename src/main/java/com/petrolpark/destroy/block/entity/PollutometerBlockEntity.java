package com.petrolpark.destroy.block.entity;

import java.util.List;

import com.petrolpark.destroy.block.PollutometerBlock;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class PollutometerBlockEntity extends SmartTileEntity {

    private PollutionType pollutionType;

    protected ScrollOptionBehaviour<PollutionType> pollutionTypeDisplay;

    public PollutometerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        pollutionType = PollutionType.RADIOACTIVITY;
    };

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        pollutionTypeDisplay = new ScrollOptionBehaviour<PollutionType>(PollutionType.class, DestroyLang.translate("tooltip.pollutometer.pollution_type").component(), this, slot);
        pollutionTypeDisplay.withCallback(this::setPollutionType);
        behaviours.add(pollutionTypeDisplay);
    };

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        int pollutionTypeIndex = tag.getInt("PollutionType");
        setPollutionType(pollutionTypeIndex);
        pollutionTypeDisplay.value = pollutionTypeIndex;
        super.read(tag, clientPacket);
    };

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        tag.putInt("PollutionType", pollutionType.ordinal());
        super.write(tag, clientPacket);
    };

    public PollutionType getPollutionType() {
        return pollutionType;
    };

    private void setPollutionType(Integer pollutionTypeIndex) {
        this.pollutionType = PollutionType.values()[pollutionTypeIndex];
    };

    private static final ValueBoxTransform.Sided slot = new ValueBoxTransform.Sided() {

        @Override
        protected Vec3 getLocalOffset(BlockState state) {
            return VecHelper.rotateCentered(VecHelper.voxelSpace(8, 6, 13f), AngleHelper.horizontalAngle(getSide()), Axis.Y);
        };

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction == state.getValue(PollutometerBlock.DIRECTION);
        };

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 6, 13f);
        };

        @Override
        protected float getScale() {
            return 0.35f;
        };
    };
    
};
