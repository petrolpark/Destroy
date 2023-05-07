package com.petrolpark.destroy.network.packet;

import java.util.function.Supplier;

import com.petrolpark.destroy.item.renderer.SeismometerItemRenderer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

public class SeismometerSpikeS2CPacket extends S2CPacket {

    public SeismometerSpikeS2CPacket() {};

    public SeismometerSpikeS2CPacket(FriendlyByteBuf buffer) {};

    @Override
    public void toBytes(FriendlyByteBuf buffer) {};

    @Override
    public boolean handle(Supplier<Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            SeismometerItemRenderer.spike();
        });
        return true;
    };
    
};
