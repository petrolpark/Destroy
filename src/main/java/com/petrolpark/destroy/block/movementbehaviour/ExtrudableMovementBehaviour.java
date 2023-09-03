package com.petrolpark.destroy.block.movementbehaviour;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.render.BakedModelRenderHelper;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ExtrudableMovementBehaviour implements MovementBehaviour {

    @Override
    public void onSpeedChanged(MovementContext context, Vec3 oldMotion, Vec3 motion) {
        CompoundTag data = context.data;
        if (data.getBoolean("Extruding")) {
            Direction direction = getDirection(context);
            if (!VecHelper.isVecPointingTowards(motion, direction) && !VecHelper.isVecPointingTowards(motion, direction.getOpposite())) abandonExtrusion(context);
        };
    };

    @Override
	public void visitNewPosition(MovementContext context, BlockPos pos) {
        BlockState dieState = context.world.getBlockState(pos);

        CompoundTag data = context.data;

        if (DestroyBlocks.EXTRUSION_DIE.has(dieState) && !data.getBoolean("Extruding") && !data.getBoolean("Extruded")) {
            Axis axis = dieState.getValue(BlockStateProperties.AXIS);

            for (AxisDirection axisDirection : AxisDirection.values()) {
                Direction direction = Direction.get(axisDirection, axis);
                if (VecHelper.isVecPointingTowards(context.relativeMotion, Direction.get(axisDirection, axis))) {
                    data.putBoolean("Extruding", true);
                    data.putInt("ExtrusionDirection", direction.ordinal());
                    data.put("ExtrusionDiePos", NbtUtils.writeBlockPos(pos));
                    break;
                };
            };

        } else if (data.getBoolean("Extruding")) {

            BlockPos diePos = NbtUtils.readBlockPos(data.getCompound("ExtrusionDiePos"));
            Direction direction = getDirection(context);
            
            if (pos.equals(diePos.relative(direction))) {
                context.contraption.getBlocks().put(context.localPos, new StructureBlockInfo(context.localPos, Blocks.BRICKS.defaultBlockState(), null));
                data.putBoolean("Extruded", true);
            };
            
            abandonExtrusion(context);
        };
	};

    @OnlyIn(Dist.CLIENT)
	public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        CompoundTag data = context.data;

        if (!data.getBoolean("Extruding") && !data.getBoolean("Extruded")) return;
        PoseStack ms = matrices.getViewProjection();
        VertexConsumer vbSolid = buffer.getBuffer(RenderType.solid());

        Direction direction = getDirection(context);
        float progress = 0f;

        if (data.getBoolean("Extruded")) {
            progress = 0f;
        } else {
            BlockPos diePos = NbtUtils.readBlockPos(data.getCompound("ExtrusionDiePos"));
            Vec3 displacement = context.position.subtract(Vec3.atLowerCornerOf(diePos)); // Vector between center of Die and of Block being extruded
            progress = (float)direction.getAxis().choose(displacement.x(), displacement.y(), displacement.z());
            if (direction.getAxisDirection() == AxisDirection.POSITIVE) progress = 1f - progress;
            Destroy.LOGGER.info(""+progress);
        };

        ms.translate(context.localPos.getX(), context.localPos.getY(), context.localPos.getZ());
        ms.pushPose();
        BakedModel model = new ExtrudedBlockModel(Blocks.BRICKS.defaultBlockState(), direction, progress);
		BakedModelRenderHelper.standardModelRender(model, Blocks.AIR.defaultBlockState())
            .renderInto(ms, vbSolid);
        ms.popPose();
    };

    private void abandonExtrusion(MovementContext context) {
        CompoundTag data = context.data;
        data.putBoolean("Extruding", false);
        data.remove("ExtrusionDiePos");
        data.remove("ExtrusionDirection");
    };

    private static Direction getDirection(MovementContext context) {
        return Direction.values()[context.data.getInt("ExtrusionDirection")];
    };
};
