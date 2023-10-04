package com.petrolpark.destroy.util;

import com.petrolpark.destroy.world.explosion.SmartExplosion;

import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

public class ExplosionHelper {

    public static Explosion explode(ServerLevel level, SmartExplosion explosion) {
        if (ForgeEventFactory.onExplosionStart(level, explosion)) return explosion; // True if cancelled
        explosion.explode();
        explosion.finalizeExplosion(true);

        Vec3 pos = explosion.getPosition();

        for(ServerPlayer player : level.getPlayers(player -> player.distanceToSqr(explosion.getPosition()) < 4096d)) {
            player.connection.send(new ClientboundExplodePacket(pos.x, pos.y, pos.z, explosion.getRadius(), explosion.getToBlow(), explosion.getHitPlayers().get(player)));
        };

        return explosion;
    };
};
