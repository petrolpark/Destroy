package com.petrolpark.destroy.block.entity;

import java.util.Arrays;
import java.util.List;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.behaviour.SidedScrollValueBehaviour;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class DynamiteBlockEntity extends SmartBlockEntity {

    SidedScrollValueBehaviour scrollValueBehaviour;

    public DynamiteBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        scrollValueBehaviour = new SidedScrollValueBehaviour(DestroyLang.translate("tooltip.dynamite.excavation_radius").component(), this, new DynamiteValueBox())
            .between(0, 10)
            .withCallback(this::updateDimensions);
        Arrays.fill(scrollValueBehaviour.values, 2);
        behaviours.add(scrollValueBehaviour);
    };

    private void updateDimensions(Direction direction, Integer value) {
        Destroy.LOGGER.info("Now at "+value+" in direction "+direction.toString());
    };

    protected class DynamiteValueBox extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 16);  
        };
    };
    
};
