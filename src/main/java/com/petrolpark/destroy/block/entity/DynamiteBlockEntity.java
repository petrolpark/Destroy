package com.petrolpark.destroy.block.entity;

import java.util.Arrays;
import java.util.List;

import com.petrolpark.destroy.block.entity.behaviour.SidedScrollValueBehaviour;
import com.petrolpark.destroy.block.entity.behaviour.WhenTargetedBehaviour;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class DynamiteBlockEntity extends SmartBlockEntity {

    public BlockPos excavationAreaUpperCorner;
    public BlockPos excavationAreaLowerCorner;
    protected SidedScrollValueBehaviour scrollValueBehaviour;
    protected WhenTargetedBehaviour targetedBehaviour;

    private int initializationTicks;

    public DynamiteBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        excavationAreaLowerCorner = getBlockPos();
        excavationAreaUpperCorner = getBlockPos();
        initializationTicks = 3;
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        scrollValueBehaviour = new SidedScrollValueBehaviour(DestroyLang.translate("tooltip.dynamite.excavation_radius").component(), this, new DynamiteValueBox())
            .between(0, 10)
            .oppositeSides()
            .withCallback((d, i) -> updateExcavationArea());
        Arrays.fill(scrollValueBehaviour.values, 2); // Set default values
        behaviours.add(scrollValueBehaviour);

        updateExcavationArea();

        targetedBehaviour = new WhenTargetedBehaviour(this, this::showExcavationArea);
        behaviours.add(targetedBehaviour);
    };

    @Override
    public void tick() {
        super.tick();
        if (initializationTicks > 0) {
            initializationTicks--;
            if (initializationTicks == 1) updateExcavationArea();
        };
    };

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        excavationAreaUpperCorner = NbtUtils.readBlockPos(tag.getCompound("UpperCorner"));
        excavationAreaLowerCorner = NbtUtils.readBlockPos(tag.getCompound("LowerCorner"));
    };

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("UpperCorner", NbtUtils.writeBlockPos(excavationAreaUpperCorner));
        tag.put("LowerCorner", NbtUtils.writeBlockPos(excavationAreaLowerCorner));
    };

    private void showExcavationArea(LocalPlayer player, BlockHitResult blockHitResult) {
        CreateClient.OUTLINER.chaseAABB(Pair.of("excavationArea", getBlockPos()), new AABB(excavationAreaLowerCorner, excavationAreaUpperCorner))
            .colored(0xFF_d80051);
    };

    private void updateExcavationArea() {
        excavationAreaLowerCorner = getBlockPos();
        excavationAreaUpperCorner = getBlockPos();
        int[] values = scrollValueBehaviour.values;
        for (Direction direction : Direction.values()) {
            if (direction.getAxisDirection() == AxisDirection.POSITIVE) {
                excavationAreaUpperCorner = excavationAreaUpperCorner.relative(direction, values[direction.getOpposite().ordinal()] + 1);
            } else {
                excavationAreaLowerCorner = excavationAreaLowerCorner.relative(direction, values[direction.getOpposite().ordinal()]);
            };
        };
        sendData();
    };

    protected class DynamiteValueBox extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 15.5);  
        };
    };
    
};
