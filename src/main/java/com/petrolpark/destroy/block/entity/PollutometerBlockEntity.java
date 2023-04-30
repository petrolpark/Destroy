package com.petrolpark.destroy.block.entity;

import java.util.List;

import com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollOptionBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class PollutometerBlockEntity extends SmartTileEntity {

    private PollutionType pollutionType;

    protected ScrollOptionBehaviour<PollutionType> pollutionTypeDisplay;

    public PollutometerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {

        ValueBoxTransform.Sided slot = new ValueBoxTransform.Sided() {

            @Override
            protected Vec3 getSouthLocation() {
                return Vec3.ZERO;
            };
        };

        pollutionTypeDisplay = new ScrollOptionBehaviour<PollutionType>(PollutionType.class, DestroyLang.translate("tooltip.pollutometer.pollution_type").component(), this, slot);
        pollutionTypeDisplay.withCallback(this::setPollutionType);
        behaviours.add(pollutionTypeDisplay);
    };

    public PollutionType getPollutionType() {
        return pollutionType;
    };

    private void setPollutionType(Integer pollutionTypeIndex) {
        this.pollutionType = PollutionType.values()[pollutionTypeIndex];
    };
    
};
