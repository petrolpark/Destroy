package com.petrolpark.destroy.block.instance;

import java.util.ArrayList;
import java.util.List;

import com.jozufozu.flywheel.api.Material;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.block.DoubleCardanShaftBlock;
import com.petrolpark.destroy.block.entity.DoubleCardanShaftBlockEntity;
import com.petrolpark.destroy.block.model.DestroyPartials;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.flwdata.KineticData;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class DoubleCardanShaftInstance extends KineticBlockEntityInstance<DoubleCardanShaftBlockEntity> implements DynamicInstance {

    protected final RotatingData shaft1;
    protected final RotatingData grip1;
    protected final ModelData gimbal1;

    protected final RotatingData shaft2;
    protected final RotatingData grip2;
    protected final ModelData gimbal2;

    protected final ModelData centerShaft;

    protected final Direction shaft1Direction;
    protected final Direction shaft2Direction;

    protected Direction sourceFacing;

    public DoubleCardanShaftInstance(MaterialManager materialManager, DoubleCardanShaftBlockEntity blockEntity) {
        super(materialManager, blockEntity);
        Material<ModelData> modelMaterial = getTransformMaterial();
        Material<RotatingData> rotatingMaterial = getRotatingMaterial();
        Direction[] directions = DoubleCardanShaftBlock.getDirectionsConnectedByState(blockEntity.getBlockState());
        
        shaft1Direction = directions[0];
        shaft2Direction = directions[1];

        int blockLight = world.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = world.getBrightness(LightLayer.SKY, pos);

        updateSourceFacing();

        shaft1 = rotatingMaterial.getModel(DestroyPartials.DCS_SIDE_SHAFT, blockEntity.getBlockState(), shaft1Direction)
            .createInstance();
        shaft1.setRotationAxis(Direction.get(AxisDirection.POSITIVE, shaft1Direction.getAxis()).step())
            .setRotationOffset(getRotationOffset(shaft1Direction.getAxis()));
        transformShaft(shaft1, shaft1Direction, blockLight, skyLight);

        grip1 = rotatingMaterial.getModel(DestroyPartials.DCS_SIDE_GRIP, blockEntity.getBlockState(), shaft1Direction)
            .createInstance();
        grip1.setRotationAxis(Direction.get(AxisDirection.POSITIVE, shaft1Direction.getAxis()).step())
            .setRotationOffset(0f);
        transformShaft(grip1, shaft1Direction, blockLight, skyLight);

        gimbal1 = modelMaterial.getModel(DestroyPartials.DCS_GIMBAL, blockEntity.getBlockState(), shaft1Direction)
            .createInstance();

        shaft2 = rotatingMaterial.getModel(DestroyPartials.DCS_SIDE_SHAFT, blockEntity.getBlockState(), shaft2Direction)
            .createInstance();
        shaft2.setRotationAxis(Direction.get(AxisDirection.POSITIVE, shaft2Direction.getAxis()).step())
            .setRotationOffset(getRotationOffset(shaft2Direction.getAxis())).setColor(blockEntity);
        transformShaft(shaft2, shaft2Direction, blockLight, skyLight);

        grip2 = rotatingMaterial.getModel(DestroyPartials.DCS_SIDE_GRIP, blockEntity.getBlockState(), shaft2Direction)
            .createInstance();
        grip2.setRotationAxis(Direction.get(AxisDirection.POSITIVE, shaft2Direction.getAxis()).step())
            .setRotationOffset(0f);
        transformShaft(grip2, shaft2Direction, blockLight, skyLight);

        gimbal2 = modelMaterial.getModel(DestroyPartials.DCS_GIMBAL, blockEntity.getBlockState(), shaft2Direction)
            .createInstance();

        centerShaft = modelMaterial.getModel(DestroyPartials.DCS_CENTER_SHAFT).createInstance();
    };

    private float getSpeed(Direction direction) {
        float speed = blockEntity.getSpeed();
        if (speed != 0 && sourceFacing != null) {
            if (sourceFacing.getAxisDirection() == direction.getAxisDirection() && direction != sourceFacing) speed *= -1;
        };
        return speed;
    };

    private void transformShaft(KineticData data, Direction face, int blockLight, int skyLight) {
        data
            .setRotationalSpeed(getSpeed(face))
            .setPosition(getInstancePosition())
			.setBlockLight(blockLight)
			.setSkyLight(skyLight);
    };

    @Override
    public void beginFrame() {
        float angle = KineticBlockEntityRenderer.getAngleForTe(blockEntity, pos, shaft1Direction.getAxis());
        double fluctuatingAngle = Math.atan2(Mth.sin(angle), Mth.cos(angle) * Mth.sqrt(2) / 2);

        List<Axis> axes = new ArrayList<>();
        axes.addAll(List.of(Axis.values()));
        axes.remove(shaft1Direction.getAxis());
        axes.remove(shaft2Direction.getAxis());
        Axis axis = axes.get(0);

        if (axis != Axis.Y) {

        };

        centerShaft.loadIdentity()
            .translate(getInstancePosition())
            .translate(shaft1Direction.step().mul(2.5f / 16f))
            .translate(shaft2Direction.step().mul(2.5f / 16f))
            .centre()
            .rotateY(axis == Axis.Z ? 90f : 0f)
            .rotate((DoubleCardanShaftBlock.isPositiveDirection(shaft1Direction) == DoubleCardanShaftBlock.isPositiveDirection(shaft2Direction)) ^ axis != Axis.Y ? 135f : 45f, axis)
            .unCentre()
            .centre()
            .rotateZRadians(fluctuatingAngle * ((axis == Axis.Y ^ shaft1Direction.getAxisDirection() == shaft2Direction.getAxisDirection()) ? 1f : -1f))
            .unCentre();

        float gimbal1Angle = Mth.PI * ((AnimationTickHolder.getRenderTime() * getSpeed(shaft1Direction) * 3f / 10) % 360) / 180f;
        float gimbal2Angle = Mth.PI * ((AnimationTickHolder.getRenderTime() * getSpeed(shaft2Direction) * 3f / 10) % 360) / 180f;
        float gimbal1FluctuatingAngle = Mth.sin((float)Math.atan2(Mth.sin(gimbal1Angle), Mth.cos(gimbal1Angle) * Mth.sqrt(2) / 2)) * Mth.PI / 4;
        float gimbal2FluctuatingAngle = Mth.sin((float)Math.atan2(Mth.sin(gimbal2Angle), Mth.cos(gimbal2Angle) * Mth.sqrt(2) / 2)) * Mth.PI / 4;

        gimbal1.loadIdentity()
            .translate(getInstancePosition())
            
            .centre()
            .rotate(Direction.get(AxisDirection.POSITIVE, shaft1Direction.getAxis()), gimbal1Angle)
            .centre()

            .translateBack(gimbalTranslation(shaft1Direction))
            .rotate(gimbalRotation(shaft1Direction), gimbal1FluctuatingAngle)
            .translate(gimbalTranslation(shaft1Direction))

            .unCentre()
            .unCentre()
            ;

        gimbal2.loadIdentity()
            .translate(getInstancePosition())
            
            .centre()

            .rotate(Direction.get(AxisDirection.POSITIVE, shaft2Direction.getAxis()), gimbal2Angle)
            .centre()

            .translateBack(gimbalTranslation(shaft2Direction))
            .rotate(gimbalRotation(shaft2Direction), gimbal2FluctuatingAngle)
            .translate(gimbalTranslation(shaft2Direction))

            .unCentre()
            .unCentre()
            ;
    };

    private static Vec3 gimbalTranslation(Direction face) {
        switch (face) {
            case NORTH:
                return new Vec3(8 / 16d, 8 / 16d, 13 / 16d);
            case EAST:
                return new Vec3(3 / 16d, 8 / 16d, 8 / 16d);
            case SOUTH:
                return new Vec3(8 / 16d, 8 / 16d, 3 / 16d);
            case WEST:
                return new Vec3(13 / 16d, 8 / 16d, 8 / 16d);
            case UP:
                return new Vec3(8 / 16d, 3 / 16d, 8 / 16d);
            case DOWN:
                return new Vec3(8 / 16d, 13 / 16d, 8 / 16d);
            default:
                return new Vec3(8 / 16d, 8 / 16d, 8 / 16d); // Shouldn't be called
        }
    };

    private static Direction gimbalRotation(Direction direction) {
        switch (direction) {
            case NORTH:
                return Direction.EAST;
            case EAST:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.EAST;
            case WEST:
                return Direction.SOUTH;
            case UP:
                return Direction.EAST;
            case DOWN:
                return Direction.EAST;
            default:
                throw new IllegalStateException("Unknown direction");
        }
    };

    protected void updateSourceFacing() {
        if (blockEntity.hasSource()) {
            BlockPos source = blockEntity.source.subtract(pos);
            sourceFacing = Direction.getNearest(source.getX(), source.getY(), source.getZ());
        } else {
            sourceFacing = null;
        }
    };

    @Override
    public void update() {
        super.update();
        updateSourceFacing();
        updateRotation(shaft1, shaft1Direction.getAxis(), getSpeed(shaft1Direction));
        updateRotation(grip1, shaft1Direction.getAxis(), getSpeed(shaft1Direction));
        grip1.setRotationOffset(0f);
        updateRotation(shaft2, shaft2Direction.getAxis(), getSpeed(shaft2Direction));
        updateRotation(grip2, shaft2Direction.getAxis(), getSpeed(shaft2Direction));
        grip2.setRotationOffset(0f);
    };

    @Override
    public void updateLight() {
        relight(pos, shaft1, shaft2, grip1, grip2, gimbal1, gimbal2, centerShaft);
    };

    @Override
    protected void remove() {
        shaft1.delete();
        shaft2.delete();
        grip1.delete();
        grip2.delete();
        gimbal1.delete();
        gimbal2.delete();
        centerShaft.delete();
    };
    
};
