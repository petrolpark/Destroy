package com.petrolpark.destructivedelight.block;

import static com.petrolpark.destroy.Destroy.REGISTRATE;

import com.tterrag.registrate.util.entry.BlockEntry;

public class DestructiveDelightBlocks {

    public static final BlockEntry<YeastColonyBlock> YEAST_COLONY = REGISTRATE.block("yeast_colony", YeastColonyBlock::new)
        .register();
};
