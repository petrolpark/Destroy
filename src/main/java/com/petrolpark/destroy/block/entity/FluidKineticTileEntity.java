package com.petrolpark.destroy.block.entity;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.fluids.FluidTransportBehaviour;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidKineticTileEntity extends KineticTileEntity {

    public FluidKineticTileEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    };

    public boolean isTankFull(FluidTank tank) {
        return tank.getFluidAmount() == tank.getCapacity();
    };

    public boolean canFitFluidInTank(FluidStack stack, FluidTank tank) {
        return (stack.getFluid() == tank.getFluid().getFluid() || tank.isEmpty()) && stack.getAmount() <= tank.getSpace();
    };

    public boolean hasFluidInTank(FluidIngredient ingredient, FluidTank tank) {
        if (tank.drain(ingredient.getRequiredAmount(), FluidAction.SIMULATE).getAmount() != ingredient.getRequiredAmount()) return false; //check there is enough Fluid
        for (FluidStack stack : ingredient.getMatchingFluidStacks()) {
            if (stack.getFluid() == tank.getFluid().getFluid()) return true; //check it is the right Fluid type
        };
        return false;
    };

    @SuppressWarnings("null")
    protected void onFluidStackChanged() {
        if (!hasLevel()) return;
        if (!(getLevel() == null || getLevel().isClientSide())) {
            setChanged();
            sendData();
        };
    };

    /**
     * Used by the Centrifuge and Bubble Cap to the side to which they should output.
     * @param currentDirection The current face which the output is on
     * @param tank The Tank from which the output would be outputting
     * @return The new face the output should point to
     */
    @SuppressWarnings("null")
    public Direction refreshDirection(Direction currentDirection, FluidTank tank) {
        if (getLevel() == null || currentDirection.getAxis() == Direction.Axis.Y) { // If the level doesn't exist (low-key no idea how this error even occured), or the side is UP or DOWN, fix this
            return Direction.NORTH;
        };
        Direction direction = currentDirection;
        int i = 0;
        while (i < 4) { // Loop through possible Directions, prioritising the current Direction
            BlockEntity be = getLevel().getBlockEntity(getBlockPos().relative(direction)); // It thinks 'level' can be null (it can't)
            if (be != null) {
                FluidTransportBehaviour transport = TileEntityBehaviour.get(be, FluidTransportBehaviour.TYPE);
                if (transport != null && transport.canPullFluidFrom(tank.getFluid(), getBlockState(), direction)) {
                    return direction;
                } else {

                }
            };
            direction = direction.getClockWise(); // Check the next Direction
            i++;
        };

        return currentDirection; // If no other Direction was found, keep it the way it was
    };
    
}
