package com.petrolpark.destroy.block.entity;

import static com.petrolpark.destroy.Destroy.REGISTRATE;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.instance.CentrifugeCogInstance;
import com.petrolpark.destroy.block.renderer.AgingBarrelRenderer;
import com.petrolpark.destroy.block.renderer.CentrifugeRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class DestroyBlockEntities {

    public static final BlockEntityEntry<CentrifugeBlockEntity> CENTRIFUGE = REGISTRATE
        .tileEntity("centrifuge", CentrifugeBlockEntity::new)
        .instance(() -> CentrifugeCogInstance::new)
        .validBlocks(DestroyBlocks.CENTRIFUGE)
        .renderer(() -> CentrifugeRenderer::new)
        .register();


    public static final BlockEntityEntry<AgingBarrelBlockEntity> AGING_BARREL = REGISTRATE
        .tileEntity("aging_barrel", AgingBarrelBlockEntity::new)
        .validBlocks(DestroyBlocks.AGING_BARREL)
        .renderer(() -> AgingBarrelRenderer::new)
        .register();

    public static void register() {};
    
}
