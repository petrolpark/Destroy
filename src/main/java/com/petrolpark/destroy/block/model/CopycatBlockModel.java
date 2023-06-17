package com.petrolpark.destroy.block.model;

import java.util.List;

import com.simibubi.create.content.decoration.copycat.CopycatModel;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class CopycatBlockModel extends CopycatModel {

    public CopycatBlockModel(BakedModel originalModel) {
        super(originalModel);
    };

    @Override
    protected List<BakedQuad> getCroppedQuads(BlockState state, Direction side, RandomSource rand, BlockState material, ModelData wrappedData, RenderType renderType) {
        return getModelOf(material).getQuads(material, side, rand, wrappedData, renderType);
    };
    
};
