package com.petrolpark.destroy.network.packet;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.petrolpark.destroy.item.SwissArmyKnifeItem;
import com.petrolpark.destroy.item.SwissArmyKnifeItem.Tool;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

public class SwissArmyKnifeToolC2SPacket extends C2SPacket {

    private final boolean hasTool;
    private final Tool tool;

    public SwissArmyKnifeToolC2SPacket(@Nullable Tool tool) {
        this.tool = tool;
        hasTool = tool != null;
    };

    public SwissArmyKnifeToolC2SPacket(FriendlyByteBuf buffer) {
        hasTool = buffer.readBoolean();
        tool = hasTool ? Tool.values()[buffer.readInt()] : null;
    };

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBoolean(hasTool);
        buffer.writeInt(hasTool ? tool.ordinal() : 0);
    };

    @Override
    public boolean handle(Supplier<Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = player.getItemInHand(hand);
                if (stack.getItem() instanceof SwissArmyKnifeItem) SwissArmyKnifeItem.putTool(stack, tool);
            };
        });
        return true;
    };
    
};
