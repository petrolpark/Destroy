package com.petrolpark.destroy.util;

import javax.annotation.Nullable;

import com.simibubi.create.AllTags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CropMutation {

    public static final Map<Block, List<CropMutation>> MUTATIONS = new HashMap<>();

    private Supplier<Block> startCrop;
    private Supplier<BlockState> endCrop;
    private boolean oreSpecific;
    @Nullable private Supplier<Block> ore;
    private boolean successful;

    /**
     * Declare a Crop Mutation which does not require a specific Ore.
     * @param startCrop Supplies the Block which must be clicked with Hyperaccumulating Fertilizer
     * @param endCrop The Block State to be generated
     */
    public CropMutation(Supplier<Block> startCrop, Supplier<BlockState> endCrop) {
        this.startCrop = startCrop;
        this.endCrop = endCrop;
        this.oreSpecific = false;
        this.ore = null;
        this.successful = true;
        register();
    };

    /**
     * Declares a Crop Mutation which requires a specific Ore one block underneath the Farmland block.
     * It does not have to be an Ore, but it will be replaced with the corresponding stone-like Block if it is one, and Stone otherwise.
     * @param startCrop Supplies the Block which must be clicked with Hyperaccumulating Fertilizer
     * @param endCrop The Block State to be generated
     * @param ore The Ore Block which this Mutation requires
     */
    public CropMutation(Supplier<Block> startCrop, Supplier<BlockState> endCrop, Supplier<Block> ore) {
        this.startCrop = startCrop;
        this.endCrop = endCrop;
        this.oreSpecific = true;
        this.ore = ore;
        this.successful = true;
        register();
    };

    /**
     * Used to generate a 'non-Mutation', when there are no possible Mutations for the given Crop and Ore combination.
     */
    private CropMutation(BlockState crop) { //used for when no Mutation occurs
        this.endCrop = () -> crop;
        this.oreSpecific = false;
        this.successful = false;
    };

    /**
     * Let the system know that this Mutation exists.
     */
    private void register() {
        if (!MUTATIONS.keySet().contains(startCrop.get())) {
            MUTATIONS.put(startCrop.get(), new ArrayList<>());
        };
        MUTATIONS.get(startCrop.get()).add(this);
    };

    public static CropMutation getMutation(BlockState cropBlockState, BlockState blockUnder) {
        Block cropBlock = cropBlockState.getBlock();
        CropMutation mutation = null;
        checkAllEntries: for (Block crop : MUTATIONS.keySet()) {
            if (crop == cropBlock) {
                for (CropMutation possibleMutation : MUTATIONS.get(crop)) {
                    if (possibleMutation.oreSpecific) {
                        if (blockUnder.is(possibleMutation.ore.get())) { // This is the bit it thinks is null
                            mutation = possibleMutation;
                            break checkAllEntries; // Prioritize Ore-specific Mutations
                        };
                    } else {
                        mutation = possibleMutation;
                    };
                };
            };
        };
        if (mutation == null) {
            return new CropMutation(cropBlockState);
        } else {
            return mutation;
        }
    };

    public Supplier<Block> getStartCropSupplier() {
        return startCrop;
    };
    
    public Supplier<BlockState> getResultantCropSupplier() {
        return endCrop;
    };

    public Supplier<Block> getOreSupplier() {
        return ore;
    };

    public boolean isOreSpecific() {
        return oreSpecific;
    };

    public BlockState getResultantBlockUnder(BlockState ore) {
        if (!this.successful || !this.oreSpecific) return ore;
        if (ore.is(AllTags.forgeBlockTag("ores_in_ground/deepslate"))) {
            return Blocks.DEEPSLATE.defaultBlockState();
        } else if (ore.is(AllTags.forgeBlockTag("ores_in_ground/netherrack"))) {
            return Blocks.NETHERRACK.defaultBlockState();
        } else if (ore.is(AllTags.forgeBlockTag("ores_in_ground/end_stone"))) {
            return Blocks.END_STONE.defaultBlockState();
        } else {
            return Blocks.STONE.defaultBlockState();
        }
    };

    public boolean isSuccessful() {
        return successful;
    };
};
