package com.petrolpark.destroy.core.chemistry.vat.observation;

import java.util.function.Supplier;

import com.petrolpark.network.packet.C2SPacket;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class RedstoneQuantityMonitorThresholdChangeC2SPacket extends C2SPacket {

    public final float lower;
    public final float upper;
    public final BlockPos pos;

    public RedstoneQuantityMonitorThresholdChangeC2SPacket(float lower, float upper, BlockPos pos) {
        this.lower = lower;
        this.upper = upper;
        this.pos = pos;
    };

    public RedstoneQuantityMonitorThresholdChangeC2SPacket(FriendlyByteBuf buffer) {
        lower = buffer.readFloat();
        upper = buffer.readFloat();
        pos = buffer.readBlockPos();
    };

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeFloat(lower);
        buffer.writeFloat(upper);
        buffer.writeBlockPos(pos);
    };

    @Override
    public boolean handle(Supplier<Context> supplier) {
        Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            RedstoneQuantityMonitorBehaviour behaviour = BlockEntityBehaviour.get(sender.level(), pos, RedstoneQuantityMonitorBehaviour.TYPE);
            if (behaviour != null) {
                behaviour.lowerThreshold = lower;
                behaviour.upperThreshold = upper;
                behaviour.notifyUpdate();
            };
        });
        return true;
    };
    
};
