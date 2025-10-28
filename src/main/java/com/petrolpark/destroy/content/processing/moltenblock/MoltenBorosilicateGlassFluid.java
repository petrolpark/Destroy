package com.petrolpark.destroy.content.processing.moltenblock;

import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.DestroyItems;
import com.petrolpark.fluid.ICustomBlockStateFluid;
import com.simibubi.create.content.fluids.VirtualFluid;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class MoltenBorosilicateGlassFluid extends VirtualFluid implements ICustomBlockStateFluid {

    public MoltenBorosilicateGlassFluid(Properties properties, boolean source) {
        super(properties, source);
    };


    public static MoltenBorosilicateGlassFluid createSource(Properties properties) {
        return new MoltenBorosilicateGlassFluid(properties, true);
    }

    public static MoltenBorosilicateGlassFluid createFlowing(Properties properties) {
        return new MoltenBorosilicateGlassFluid(properties, false);
    }

    @Override
    public Item getBucket() {
        return DestroyItems.MOLTEN_BOROSILICATE_GLASS_BUCKET.get();
    };

    @Override
    public BlockState getBlockState() {
        return DestroyBlocks.MOLTEN_BOROSILICATE_GLASS.getDefaultState();
    };
    
};
