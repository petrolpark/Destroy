package com.petrolpark.destroy.util.vat;

import java.util.HashMap;
import java.util.Map;

import com.simibubi.create.AllBlocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Information about a Block from which a Vat can be construced.
 * @param maxPressure The maxmimum pressures (in pascals) this Material can withstand before the Vat explodes
 * @param transparent Whether this Block is permeable to sunlight and UV light
 * @param corrodes Whether this Block will be removed if the Vat contains a strong acid
 */
public record VatMaterial(float maxPressure, boolean transparent, boolean corrodes) {

    public static final Map<Block, VatMaterial> BLOCK_MATERIALS = new HashMap<>();

    /**
     * Whether the given Block can be used to construct a Vat.
     * @param block
     */
    public static boolean isValid(Block block) {
        return BLOCK_MATERIALS.containsKey(block);
    };

    public static void registerDestroyVatMaterials() {

        VatMaterial GLASS_MATERIAL = new VatMaterial(10000f, true, false);

        BLOCK_MATERIALS.put(AllBlocks.COPPER_CASING.get(), new VatMaterial(500000f, false, true));
        BLOCK_MATERIALS.put(Blocks.GLASS, GLASS_MATERIAL);
    };
};
