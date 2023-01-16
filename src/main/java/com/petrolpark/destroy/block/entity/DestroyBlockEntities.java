package com.petrolpark.destroy.block.entity;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.instance.CentrifugeCogInstance;
import com.petrolpark.destroy.block.renderer.CentrifugeRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class DestroyBlockEntities { //this is gonna be the only place I actually use registrate cause this is allllllll getting copied from the Create source code baby

    public static final BlockEntityEntry<CentrifugeBlockEntity> CENTRIFUGE = Destroy.registrate()
        .tileEntity("centrifuge", CentrifugeBlockEntity::new)
        .instance(() -> CentrifugeCogInstance::new)
        .validBlocks(DestroyBlocks.CENTRIFUGE)
        .renderer(() -> CentrifugeRenderer::new)
        .register();


    public static final BlockEntityEntry<AgingBarrelBlockEntity> AGING_BARREL = Destroy.registrate()
        .tileEntity("aging_barrel", AgingBarrelBlockEntity::new)
        .validBlocks(DestroyBlocks.AGING_BARREL)
        .register();

    public static void register() {};
    
}
