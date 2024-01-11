package com.petrolpark.destroy.network;

import java.util.function.Function;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.network.packet.C2SPacket;
import com.petrolpark.destroy.network.packet.ChainCogwheelsC2SPacket;
import com.petrolpark.destroy.network.packet.ChemicalPoisonS2CPacket;
import com.petrolpark.destroy.network.packet.CryingS2CPacket;
import com.petrolpark.destroy.network.packet.EvaporatingFluidS2CPacket;
import com.petrolpark.destroy.network.packet.LevelPollutionS2CPacket;
import com.petrolpark.destroy.network.packet.S2CPacket;
import com.petrolpark.destroy.network.packet.SeismometerSpikeS2CPacket;
import com.petrolpark.destroy.network.packet.SwissArmyKnifeToolC2SPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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

        addS2CPacket(net, CryingS2CPacket.class, CryingS2CPacket::new);
        addS2CPacket(net, LevelPollutionS2CPacket.class, LevelPollutionS2CPacket::new);
        addS2CPacket(net, EvaporatingFluidS2CPacket.class, EvaporatingFluidS2CPacket::new);
        addS2CPacket(net, SeismometerSpikeS2CPacket.class, SeismometerSpikeS2CPacket::new);
        addS2CPacket(net, ChemicalPoisonS2CPacket.class, ChemicalPoisonS2CPacket::new);

        addC2SPacket(net, SwissArmyKnifeToolC2SPacket.class, SwissArmyKnifeToolC2SPacket::new);
        addC2SPacket(net, ChainCogwheelsC2SPacket.class, ChainCogwheelsC2SPacket::new);
    };

    public static <T extends S2CPacket> void addS2CPacket(SimpleChannel net, Class<T> clazz, Function<FriendlyByteBuf, T> decoder) {
        net.messageBuilder(clazz, id(), NetworkDirection.PLAY_TO_CLIENT)
            .decoder(decoder)
            .encoder(T::toBytes)
            .consumerMainThread(T::handle)
            .add();
    };

    public static <T extends C2SPacket> void addC2SPacket(SimpleChannel net, Class<T> clazz, Function<FriendlyByteBuf, T> decoder) {
        net.messageBuilder(clazz, id(), NetworkDirection.PLAY_TO_SERVER)
            .decoder(decoder)
            .encoder(T::toBytes)
            .consumerMainThread(T::handle)
            .add();
    };

    public static void sendToServer(C2SPacket message) {
        INSTANCE.sendToServer(message);
    };

    public static void sendToClient(S2CPacket message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    };

    public static void sendToAllClients(S2CPacket message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    };

    public static void sendToClientsTrackingEntity(S2CPacket message, Entity trackedEntity) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> trackedEntity), message);
    };
}
