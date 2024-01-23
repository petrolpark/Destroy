package com.petrolpark.destroy.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import com.petrolpark.destroy.item.RedstoneProgrammerBlockItem.ItemStackRedstoneProgram;
import com.simibubi.create.foundation.utility.WorldAttached;

import net.minecraft.world.level.LevelAccessor;

public class RedstoneProgrammerItemHandler {

    public static final WorldAttached<Map<UUID, ItemStackRedstoneProgram>> programs = new WorldAttached<>(level -> new HashMap<>());
    public static final int TIMEOUT = 30;

    public static void tick(LevelAccessor level) {
        Map<UUID, ItemStackRedstoneProgram> map = programs.get(level);
        for (Iterator<Entry<UUID, ItemStackRedstoneProgram>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
            Entry<UUID, ItemStackRedstoneProgram> entry = iterator.next();
            ItemStackRedstoneProgram program = entry.getValue();
            program.ttl--;
            if (!program.shouldTransmit()) {
                program.unload();
                iterator.remove();
            };
        };
    };
    
};
