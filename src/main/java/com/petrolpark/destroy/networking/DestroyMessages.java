package com.petrolpark.destroy.networking;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.networking.packet.LevelPollutionS2CPacket;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class DestroyMessages {

    private static SimpleChannel INSTANCE;
    private static int packetID = 0;

    private static int id() {
        return packetID++;
    };

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
            .named(Destroy.asResource("messages"))
            .networkProtocolVersion(() -> "1.0")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();
    
        INSTANCE = net;

        net.messageBuilder(LevelPollutionS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
        .decoder(LevelPollutionS2CPacket::new)
        .encoder(LevelPollutionS2CPacket::toBytes)
        .consumerMainThread(LevelPollutionS2CPacket::handle)
        .add();
    };

    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    };

    public static <MSG> void sendToAllClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    };
}
