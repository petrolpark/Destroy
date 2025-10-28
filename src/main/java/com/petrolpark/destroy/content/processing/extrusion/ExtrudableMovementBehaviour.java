package com.petrolpark.destroy.content.processing.extrusion;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.destroy.DestroyAdvancementTrigger;
import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.core.data.advancement.DestroyAdvancementBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;

import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.SuperBufferFactory;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.registries.BuiltInRegistries;
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

    private final BlockExtrusion extrusion;

    public ExtrudableMovementBehaviour(BlockExtrusion extrusion) {
        this.extrusion = extrusion;
    };

    @Override
    public void onSpeedChanged(MovementContext context, Vec3 oldMotion, Vec3 motion) {
        CompoundTag data = context.data;
        if (data.getBoolean("Extruding")) {
            Direction direction = getDirection(context);
            if (!VecHelper.isVecPointingTowards(motion, direction) && !VecHelper.isVecPointingTowards(motion, direction.getOpposite()))
                abandonExtrusion(context);
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
                BlockState state = extrusion.getExtruded(context.state, direction);
                if (VecHelper.isVecPointingTowards(context.relativeMotion, direction) && !state.isAir()) {
                    data.putBoolean("Extruding", true);
                    data.putInt("ExtrusionDirection", direction.ordinal());
                    data.put("ExtrusionDiePos", NbtUtils.writeBlockPos(pos));
                    data.put("ExtrudedBlockState", NbtUtils.writeBlockState(state));
                    break;
                };
            };

        } else if (data.getBoolean("Extruding")) {

            BlockPos diePos = NbtUtils.readBlockPos(data.getCompound("ExtrusionDiePos"));
            Direction direction = getDirection(context);

            if (context.contraption.entity instanceof OrientedContraptionEntity oce && oce.getInitialYaw() != oce.yaw) direction = direction.getOpposite();
            
            if (pos.equals(diePos.relative(direction))) {
                context.contraption.getBlocks().put(context.localPos, new StructureBlockInfo(context.localPos, getBlockState(context), null));
                if (!context.world.isClientSide()) {
                    DestroyAdvancementBehaviour advancementBehaviour = BlockEntityBehaviour.get(context.world, diePos, DestroyAdvancementBehaviour.TYPE);
                    if (advancementBehaviour != null) advancementBehaviour.awardDestroyAdvancement(DestroyAdvancementTrigger.EXTRUDE);
                };
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
        PoseStack modelTransform = matrices.getModel();
        VertexConsumer vbSolid = buffer.getBuffer(RenderType.solid());

        Direction direction = getDirection(context);
        float progress = 0f;

        if (data.getBoolean("Extruded")) {
            progress = 0f;
        } else {
            BlockPos diePos = NbtUtils.readBlockPos(data.getCompound("ExtrusionDiePos"));
            Vec3 displacement = context.position.subtract(Vec3.atLowerCornerOf(diePos)); // Vector between center of Die and of Block being extruded
            progress = (float)direction.getAxis().choose(displacement.x(), displacement.y(), displacement.z());
            boolean invertProgess = direction.getAxisDirection() == AxisDirection.POSITIVE;
            if (context.contraption.entity instanceof OrientedContraptionEntity oce) {
                if (oce.yaw != oce.getInitialYaw()) invertProgess = !invertProgess;
            };
            if (invertProgess) progress = 1f - progress;
        };

        // Making a new model every frame seems like a bad idea but it changes shape continually and I'm not smart enough to do it another way
        ms.pushPose();
        BakedModel model = new ExtrudedBlockModel(getBlockState(context), direction, progress);
		SuperByteBuffer extrudedBlockBuffer = SuperBufferFactory.getInstance().createForBlock(model, Blocks.AIR.defaultBlockState());
        if (modelTransform != null) extrudedBlockBuffer.transform(modelTransform);
        
        extrudedBlockBuffer.renderInto(ms, vbSolid);
        ms.popPose();
    };

    private void abandonExtrusion(MovementContext context) {
        CompoundTag data = context.data;
        data.putBoolean("Extruding", false);
        data.remove("ExtrusionDiePos");
        data.remove("ExtrusionDirection");
        // Don't remove the Block State tag as we still need to know what to render
    };

    private static Direction getDirection(MovementContext context) {
        return Direction.values()[context.data.getInt("ExtrusionDirection")];
    };

    @SuppressWarnings("deprecation")
    private static BlockState getBlockState(MovementContext context) {
        if (!context.data.contains("ExtrudedBlockState")) return Blocks.AIR.defaultBlockState();
        return NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), context.data.getCompound("ExtrudedBlockState"));
    };
};
