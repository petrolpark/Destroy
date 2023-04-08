package com.petrolpark.destroy.networking.packet;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public abstract class S2CPacket {

    public S2CPacket(FriendlyByteBuf buffer) {};

    public abstract void toBytes(FriendlyByteBuf buffer);

    public abstract boolean handle(Supplier<NetworkEvent.Context> supplier);
};
