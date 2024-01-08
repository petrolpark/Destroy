package com.petrolpark.destroy.block.entity;

import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public interface IDirectionalOutputFluidBlockEntity {

    public default boolean isTankFull(FluidTank tank) {
        return tank.getFluidAmount() == tank.getCapacity();
    };

    public default boolean canFitFluidInTank(FluidStack stack, FluidTank tank) {
        return (stack.getFluid() == tank.getFluid().getFluid() || tank.isEmpty()) && stack.getAmount() <= tank.getSpace();
    };

    public default boolean hasFluidInTank(FluidIngredient ingredient, FluidTank tank) {
        if (tank.drain(ingredient.getRequiredAmount(), FluidAction.SIMULATE).getAmount() != ingredient.getRequiredAmount()) return false; //check there is enough Fluid
        for (FluidStack stack : ingredient.getMatchingFluidStacks()) {
            if (stack.getFluid() == tank.getFluid().getFluid()) return true; //check it is the right Fluid type
        };
        return false;
    };

    /**
     * Used by the Centrifuge and Bubble Cap to the side to which they should input/output.
     * @param be The Block Entity being rotated
     * @param currentDirection The current face which the output is on
     * @param tank The Tank from which the output would be outputting
     * @param output Whether this is an output or input
     * @return The new face the output should point to
     */
    @SuppressWarnings("null")
    public default Direction refreshDirection(SmartBlockEntity be, Direction currentDirection, FluidTank tank, boolean output) {
        if (!be.hasLevel() || currentDirection.getAxis() == Direction.Axis.Y) { // If the level doesn't exist (low-key no idea how this error even occured), or the side is UP or DOWN, fix this
            return Direction.NORTH;
        };
        Direction direction = currentDirection;
        int i = 0;
        while (i < 4) { // Loop through possible Directions, prioritising the current Direction
            BlockEntity adjacentBE = be.getLevel().getBlockEntity(be.getBlockPos().relative(direction)); // It thinks 'level' can be null (it can't)
            if (adjacentBE != null) {
                FluidTransportBehaviour transport = BlockEntityBehaviour.get(adjacentBE, FluidTransportBehaviour.TYPE);
                if (transport != null) {
                    if (output && transport.canPullFluidFrom(tank.getFluid(), adjacentBE.getBlockState(), direction)) { // If Fluid can be outputted in this Direction
                        return direction;
                    } else if (!output && transport.canHaveFlowToward(adjacentBE.getBlockState(), direction)) { // If Fluid can be inserted from this Direction
                        return direction;
                    };
                } else {

                }
            };
            direction = direction.getClockWise(); // Check the next Direction
            i++;
        };

        return currentDirection; // If no other Direction was found, keep it the way it was
    };
}
