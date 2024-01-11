package com.petrolpark.destroy.network.packet;

import java.util.function.Supplier;

import com.petrolpark.destroy.block.ChainedCogwheelBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

public class ChainCogwheelsC2SPacket extends C2SPacket {

    private final BlockPos blockPos1;
    private final BlockPos blockPos2;

    public ChainCogwheelsC2SPacket(BlockPos pos1, BlockPos pos2) {
        blockPos1 = pos1;
        blockPos2 = pos2;
    };

    public ChainCogwheelsC2SPacket(FriendlyByteBuf buffer) {
        blockPos1 = buffer.readBlockPos();
        blockPos2 = buffer.readBlockPos();
    };

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos1);
        buffer.writeBlockPos(blockPos2);
    };

    @Override
    public boolean handle(Supplier<Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            ChainedCogwheelBlock.tryPlace(player, player.level(), blockPos1, blockPos2);
        });
        return true;
    };
    
};
