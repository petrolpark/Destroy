package com.petrolpark.destroy.util.vat;

import java.util.HashMap;
import java.util.Map;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.simibubi.create.AllBlocks;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Information about a Block from which a Vat can be construced.
 * @param maxPressure The maxmimum pressures (in pascals) this Material can withstand before the Vat explodes
 * @param transparent Whether this Block is permeable to sunlight and UV light
 * @param corrodes Whether this Block will be removed if the Vat contains a strong acid
 */
public record VatMaterial(float maxPressure, boolean transparent, boolean corrodes) {

    public static final Map<NonNullSupplier<? extends Block>, VatMaterial> BLOCK_MATERIALS = new HashMap<>();

    public static final VatMaterial UNBREAKABLE = new VatMaterial(Float.MAX_VALUE, false, false);
    public static final VatMaterial GLASS = new VatMaterial(10000f, true, false);

    /**
     * Whether the given Block can be used to construct a Vat.
     * @param block
     */
    public static boolean isValid(Block block) {
        return block == DestroyBlocks.VAT_SIDE.get() || BLOCK_MATERIALS.keySet().stream().anyMatch(sup -> block == sup.get());
    };

    public static void registerDestroyVatMaterials() {

        BLOCK_MATERIALS.put(DestroyBlocks.VAT_CONTROLLER.lazy(), UNBREAKABLE);
        BLOCK_MATERIALS.put(AllBlocks.COPPER_CASING.lazy(), new VatMaterial(500000f, false, true));
        BLOCK_MATERIALS.put(() -> Blocks.GLASS, GLASS);
    };
};
