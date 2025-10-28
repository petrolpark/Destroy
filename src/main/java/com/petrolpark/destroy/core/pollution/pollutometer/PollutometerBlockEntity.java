package com.petrolpark.destroy.core.pollution.pollutometer;

import java.util.List;

import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.core.pollution.PollutometerBlock;
import com.petrolpark.destroy.core.pollution.Pollution.PollutionType;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;

import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class PollutometerBlockEntity extends SmartBlockEntity {

    private PollutionType pollutionType;

    protected ScrollOptionBehaviour<PollutionType> pollutionTypeDisplay;

    public PollutometerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        pollutionType = PollutionType.GREENHOUSE;
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
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
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            return VecHelper.rotateCentered(getSouthLocation(), AngleHelper.horizontalAngle(getSide()), Axis.Y);
        };

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction == state.getValue(PollutometerBlock.DIRECTION);
        };

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 6, 12.75f);
        };
    };
    
};
