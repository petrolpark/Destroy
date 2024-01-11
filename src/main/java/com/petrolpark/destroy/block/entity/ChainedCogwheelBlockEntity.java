package com.petrolpark.destroy.block.entity;

import java.util.List;

import com.petrolpark.destroy.block.IChainableBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChainedCogwheelBlockEntity extends BracketedKineticBlockEntity {

    /**
     * The relative position of the partner Cogwheel.
     */
    public BlockPos partner;
    /**
     * Whether this Cogwheel is in charge of rendering and dropping the item for the pair.
     */
    public boolean controller;
    /**
     * The Cog Blockstate of which this is a chained version.
     */
    public BlockState copiedState;

    public ChainedCogwheelBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        controller = false;
    };

    @Override
    public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
        neighbours.add(getBlockPos().offset(partner));
        return neighbours;
    };

    @Override
    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        if (diff.equals(partner) && target instanceof ChainedCogwheelBlockEntity otherGear) {
            return IChainableBlock.getPropagatedSpeed(otherGear.copiedState) / IChainableBlock.getPropagatedSpeed(copiedState);
        };
        return super.propagateRotationTo(target, stateFrom, stateTo, diff, connectedViaAxes, connectedViaCogs);
    };

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        partner = NbtUtils.readBlockPos(compound.getCompound("PartnerPos"));
        controller = compound.getBoolean("Controller");
        copiedState = NbtUtils.readBlockState(blockHolderGetter(), compound.getCompound("CopiedState"));
    };

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.put("PartnerPos", NbtUtils.writeBlockPos(partner));
        compound.putBoolean("Controller", controller);
        compound.put("CopiedState", NbtUtils.writeBlockState(copiedState));
    };
    
};
