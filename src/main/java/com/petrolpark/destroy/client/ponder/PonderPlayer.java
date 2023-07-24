package com.petrolpark.destroy.client.ponder;

import com.mojang.authlib.GameProfile;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedClientWorld;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.level.Level;

public class PonderPlayer extends AbstractClientPlayer {

    public PonderPlayer(Level level, String playername) {
        super(WrappedClientWorld.of(level), new GameProfile(null, playername));
    };
    
};
