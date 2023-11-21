package com.petrolpark.destroy.network.packet;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public abstract class C2SPacket {
    
    public abstract void toBytes(FriendlyByteBuf buffer);

    public abstract boolean handle(Supplier<NetworkEvent.Context> supplier);
};
