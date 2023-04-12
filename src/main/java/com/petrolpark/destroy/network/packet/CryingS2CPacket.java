package com.petrolpark.destroy.network.packet;

import java.util.function.Supplier;

import com.petrolpark.destroy.effect.DestroyMobEffects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

public class CryingS2CPacket {

    private final boolean isCrying;
    private final int entityId;

    public CryingS2CPacket(LivingEntity entity, boolean isCrying) {
        this.entityId = entity.getId();
        this.isCrying = isCrying;
    };

    public CryingS2CPacket(FriendlyByteBuf buffer) {
        this.isCrying = buffer.readBoolean();
        this.entityId = buffer.readInt();
    };

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBoolean(isCrying);
        buffer.writeInt(entityId);
    };
    
    @SuppressWarnings("resource")
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;
            Entity entity = level.getEntity(entityId);
            if (entity == null || !(entity instanceof LivingEntity livingEntity)) return;
            if (isCrying && !livingEntity.hasEffect(DestroyMobEffects.CRYING.get())) {
                livingEntity.addEffect(new MobEffectInstance(DestroyMobEffects.CRYING.get(), Integer.MAX_VALUE, 0, true, false, false));
            } else {
                livingEntity.removeEffect(DestroyMobEffects.CRYING.get());
            };
        });
        return true;
    };  
};
