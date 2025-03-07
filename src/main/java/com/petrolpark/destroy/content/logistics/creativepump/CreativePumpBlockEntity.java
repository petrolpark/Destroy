package com.petrolpark.destroy.content.logistics.creativepump;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class CreativePumpBlockEntity extends PumpBlockEntity {

    public ScrollValueBehaviour pumpSpeedBehaviour;
    protected int simulatedSpeed = 16; // Can't use KineticBlockEntity speed because this gets set to 0 if there is no source

    public CreativePumpBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        pumpSpeedBehaviour = new ScrollValueBehaviour(Component.translatable("block.destroy.creative_pump.speed"), this, new CreativePumpValueSlot()) {
            @Override
            public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
                return new ValueSettingsBoard(label, max, 16, ImmutableList.of(Component.translatable("block.destroy.creative_pump.speed")), new ValueSettingsFormatter(ValueSettings::format));
            };
        }
            .between(0, AllConfigs.server().kinetics.maxRotationSpeed.get())
            .withCallback(i -> {
                simulatedSpeed = i;
                updatePressureChange();
            });
        pumpSpeedBehaviour.setValue(simulatedSpeed);
        behaviours.add(pumpSpeedBehaviour);
    };

    @Override
    public float getSpeed() {
        return simulatedSpeed;
    };

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        simulatedSpeed = compound.getInt("SimulatedSpeed");
        pumpSpeedBehaviour.setValue(simulatedSpeed);
    };

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("SimulatedSpeed", simulatedSpeed);
    };

    public class CreativePumpValueSlot extends ValueBoxTransform.Sided {

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return state.getValue(CreativePumpBlock.FACING).getAxis() != direction.getAxis();
        };

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8d, 8d, 12.5d);
        };

    };
    
};
