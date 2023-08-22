package com.petrolpark.destroy.block.renderer;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.block.entity.PlanetaryGearsetBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;

public class PlanetaryGearsetRenderer extends KineticBlockEntityRenderer<PlanetaryGearsetBlockEntity> {

    public PlanetaryGearsetRenderer(Context context) {
        super(context);
    };

     @Override
    protected void renderSafe(PlanetaryGearsetBlockEntity planetaryGearsetBlockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (Backend.canUseInstancing(planetaryGearsetBlockEntity.getLevel())) return;
    };
    
};
