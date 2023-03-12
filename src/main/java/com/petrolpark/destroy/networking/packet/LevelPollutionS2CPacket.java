package com.petrolpark.destroy.networking.packet;

import java.util.function.Supplier;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.capability.level.pollution.ClientLevelPollutionData;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class LevelPollutionS2CPacket {
    
    private final LevelPollution levelPollution;

    public LevelPollutionS2CPacket(LevelPollution levelPollution) {
        this.levelPollution = levelPollution;
    };

    public LevelPollutionS2CPacket(FriendlyByteBuf buffer) {
        this.levelPollution = new LevelPollution();
        levelPollution.loadNBTData(buffer.readNbt());
    };

    public void toBytes(FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        levelPollution.saveNBTData(tag);
        buffer.writeNbt(tag);
    };
    
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Destroy.LOGGER.info("Yipeeee I've recieved it!!!");
            ClientLevelPollutionData.setLevelPollution(levelPollution);
        });
        return true;
    };
};
