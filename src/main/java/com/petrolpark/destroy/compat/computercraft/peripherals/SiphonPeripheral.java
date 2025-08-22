package com.petrolpark.destroy.compat.computercraft.peripherals;

import com.petrolpark.destroy.content.logistics.siphon.SiphonBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import dan200.computercraft.api.detail.ForgeDetailRegistries;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

public class SiphonPeripheral implements IPeripheral {

    SiphonBlockEntity sbe;

    public SiphonPeripheral(SiphonBlockEntity sbe) {
        this.sbe = sbe;
    }

    @LuaFunction(mainThread = true)
    public final int getLeftToDrain() {
        return sbe.leftToDrain;
    }

    @LuaFunction(mainThread = true)
    public final float getFluidLevel() {
        return sbe.tank.getPrimaryTank().getRenderedFluid().getAmount();
    }

    @LuaFunction(mainThread = true)
    public final void drain(int amount) {
        sbe.leftToDrain += amount;
        sbe.notifyUpdate();
    }

    @Override
    public String getType() {
        return "siphon";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof SiphonPeripheral o && sbe == o.sbe;
    }
}
