package com.petrolpark.destroy.core.fluid.gasparticle;

import java.util.function.Supplier;

import com.petrolpark.destroy.client.DestroyParticleTypes;
import com.petrolpark.network.packet.S2CPacket;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

/**
 * Summons evaporating Fluid Particles to simulate the emission of a gas.
 */
public class EvaporatingFluidS2CPacket extends S2CPacket {

    private BlockPos blockPos;
    private FluidStack fluidStack;

    public EvaporatingFluidS2CPacket(BlockPos pos, FluidStack stack) {
        this.blockPos = pos;
        this.fluidStack = stack;
    };

    public EvaporatingFluidS2CPacket(FriendlyByteBuf buffer) {
        blockPos = buffer.readBlockPos();
        fluidStack = buffer.readFluidStack();
    };

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeFluidStack(fluidStack);
    };

    @Override
    @SuppressWarnings("resource")
    public boolean handle(Supplier<Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            Vec3 center = VecHelper.getCenterOf(blockPos);
            if (level == null || fluidStack.isEmpty()) return;
            GasParticleData particleData = new GasParticleData(DestroyParticleTypes.EVAPORATION.get(), fluidStack);
            for (int i = 0; i < 5; i++) {
                level.addParticle(particleData, center.x, center.y, center.z, 0, 0.07D, 0);
            };
        });
        return true;
    };
    
};
