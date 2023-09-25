package com.petrolpark.destroy.util.vat;

import java.util.HashMap;
import java.util.Map;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.simibubi.create.AllBlocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Information about a Block from which a Vat can be construced.
 * @param maxPressure The maxmimum pressure (in pascals) this Material can withstand before the Vat explodes
 * @param thermalConductivity The thermal conductivity (in watts per block-side-length-kelvin) of this Material
 * @param transparent Whether Blocks of this Material are permeable to sunlight and UV
 * @param corrodes Whether a Block of this Material will be removed if the Vat contains a strong acid
 */
public record VatMaterial(float maxPressure, float thermalConductivity, boolean transparent) {

    public static final Map<Block, VatMaterial> BLOCK_MATERIALS = new HashMap<>();

    public static final VatMaterial UNBREAKABLE = new VatMaterial(Float.MAX_VALUE, 0f, false);
    public static final VatMaterial GLASS = new VatMaterial(100000f, 15f, true);

    /**
     * Whether the given Block can be used to construct a Vat.
     * @param block
     */
    public static boolean isValid(Block block) {
        return BLOCK_MATERIALS.containsKey(block);
    };

    public static void registerDestroyVatMaterials() {

        BLOCK_MATERIALS.put(DestroyBlocks.VAT_CONTROLLER.get(), UNBREAKABLE);
        BLOCK_MATERIALS.put(AllBlocks.COPPER_CASING.get(), new VatMaterial(1000000f, 400f, false));
        BLOCK_MATERIALS.put(Blocks.IRON_BLOCK, new VatMaterial(500000f, 50f, false));
        BLOCK_MATERIALS.put(Blocks.GLASS, GLASS);
        BLOCK_MATERIALS.put(Blocks.TINTED_GLASS, new VatMaterial(100000f, 12f, false));
    };
};
