package com.petrolpark.destroy.content.processing.moltenblock;

import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.DestroyItems;
import com.petrolpark.fluid.ICustomBlockStateFluid;
import com.simibubi.create.content.fluids.VirtualFluid;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class MoltenStainlessSteelFluid extends VirtualFluid implements ICustomBlockStateFluid {

    public MoltenStainlessSteelFluid(Properties properties, boolean source) {
        super(properties, source);
    };


    public static MoltenStainlessSteelFluid createSource(Properties properties) {
        return new MoltenStainlessSteelFluid(properties, true);
    }

    public static MoltenStainlessSteelFluid createFlowing(Properties properties) {
        return new MoltenStainlessSteelFluid(properties, false);
    }


    @Override
    public Item getBucket() {
        return DestroyItems.MOLTEN_STAINLESS_STEEL_BUCKET.get();
    };

    @Override
    public BlockState getBlockState() {
        return DestroyBlocks.MOLTEN_STAINLESS_STEEL.getDefaultState();
    };
    
};
