package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;

@Mixin(LevelSettings.class)
public abstract class LevelSettingsMixin {
    
    /**
     * Patch for https://github.com/MinecraftForge/MinecraftForge/issues/9938.
     * Probably worth removing
     */
    @Overwrite
    public LevelSettings withDataConfiguration(WorldDataConfiguration pDataConfiguration) {
        LevelSettings thisls = (LevelSettings)(Object)this;
        return new LevelSettings(thisls.levelName(), thisls.gameType(), thisls.hardcore(), thisls.difficulty(), thisls.allowCommands(), thisls.gameRules(), pDataConfiguration, thisls.getLifecycle());
    };
};
