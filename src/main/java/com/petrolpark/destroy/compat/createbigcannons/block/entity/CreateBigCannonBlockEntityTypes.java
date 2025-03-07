package com.petrolpark.destroy.compat.createbigcannons.block.entity;

import static com.petrolpark.destroy.Destroy.REGISTRATE;

import com.petrolpark.destroy.compat.createbigcannons.block.CreateBigCannonsBlocks;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBlockEntityRenderer;
import rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBlockInstance;

public class CreateBigCannonBlockEntityTypes {

    public static final BlockEntityEntry<CustomExplosiveMixChargeBlockEntity> CUSTOM_EXPLOSIVE_MIX_CHARGE = REGISTRATE
        .blockEntity("custom_explosive_mix_charge", CustomExplosiveMixChargeBlockEntity::new)
        .validBlock(CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_CHARGE)
        .register();

    /*public static final BlockEntityEntry<CustomExplosiveMixShellBlockEntity> CUSTOM_EXPLOSIVE_MIX_SHELL = REGISTRATE
        .blockEntity("custom_explosive_mix_shell", CustomExplosiveMixShellBlockEntity::new)
        .visual(() -> FuzedBlockInstance::new) // TODO: CBC
		.renderer(() -> FuzedBlockEntityRenderer::new)
        .validBlock(CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_SHELL)
        .register();*/ // TODO: CBC

    public static void register() {};
    
};
