package com.petrolpark.destructivedelight.block;

import com.petrolpark.destroy.Destroy;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;

public class DestructiveDelightBlocks {
    private static final CreateRegistrate REGISTRATE = Destroy.registrate();

    public static final BlockEntry<YeastColonyBlock> YEAST_COLONY = REGISTRATE.block("yeast_colony", YeastColonyBlock::new)
        .register();
};
