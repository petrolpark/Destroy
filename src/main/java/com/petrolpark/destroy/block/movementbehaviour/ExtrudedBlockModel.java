package com.petrolpark.destroy.block.movementbehaviour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.simibubi.create.content.decoration.copycat.CopycatModel;
import com.simibubi.create.foundation.model.BakedModelHelper;
import com.simibubi.create.foundation.model.BakedQuadHelper;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.EmptyModel;
import net.minecraftforge.client.model.data.ModelData;

public class ExtrudedBlockModel extends CopycatModel {

    protected static final AABB CUBE_AABB = new AABB(BlockPos.ZERO);

    private final BlockState extrusionResult;
    private final Direction movementDirection;
    private final float progress;

    public ExtrudedBlockModel(BlockState extrusionResult, Direction movementDirection, float progress) {
        super(EmptyModel.BAKED);
        this.extrusionResult = extrusionResult;
        this.movementDirection = movementDirection;
        this.progress = progress;
    };

    @Override
    protected List<BakedQuad> getCroppedQuads(BlockState state, Direction side, RandomSource rand, BlockState material, ModelData wrappedData, RenderType renderType) {

        BakedModel model = getModelOf(extrusionResult);
        List<BakedQuad> templateQuads = model.getQuads(extrusionResult, side, rand, wrappedData, renderType);
		int size = templateQuads.size();

        List<BakedQuad> quads = new ArrayList<>();

        Vec3 normal = Vec3.atLowerCornerOf(movementDirection.getOpposite().getNormal());

        AABB bb = CUBE_AABB.contract(normal.x * progress, normal.y * progress, normal.z * progress);
        for (int i = 0; i < size; i++) {
            BakedQuad quad = templateQuads.get(i);

            quads.add(BakedQuadHelper.cloneWithCustomGeometry(quad, scale(
                BakedModelHelper.cropAndMove(quad.getVertices(), quad.getSprite(), bb, Vec3.ZERO)
            , 1.01f)));
        };
        
        return quads;
    };


    public static int[] scale(int[] vertexData, float factor) {
        vertexData = Arrays.copyOf(vertexData, vertexData.length);

        for (int vertex = 0; vertex < 4; vertex++) {
            Vec3 xyz = BakedQuadHelper.getXYZ(vertexData, vertex);
            BakedQuadHelper.setXYZ(vertexData, vertex, xyz.subtract(0.5, 0.5, 0.5).scale(factor).add(0.5, 0.5, 0.5));
        };
		
        return vertexData;
    };
};
