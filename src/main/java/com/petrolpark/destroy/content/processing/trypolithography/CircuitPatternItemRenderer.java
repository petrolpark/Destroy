package com.petrolpark.destroy.content.processing.trypolithography;

import java.util.ArrayList;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.petrolpark.PetrolparkItemDisplayContexts;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.client.DummyBaker;
import com.petrolpark.util.BinaryMatrix4x4;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT, modid = Destroy.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CircuitPatternItemRenderer extends CustomRenderedItemModelRenderer {

    public static final ReloadListener RELOAD_LISTENER = new ReloadListener();

    protected final ResourceLocation fragmentTextureResourceLocation;

    public CircuitPatternItemRenderer(ResourceLocation fragmentTextureResourceLocation) {
        this.fragmentTextureResourceLocation = fragmentTextureResourceLocation;
    };

    @OnlyIn(Dist.CLIENT)
    protected int reloads = 0;
    protected BakedModel[] models = null;

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (models == null || reloads != RELOAD_LISTENER.reloads) {
            generateModels(model.getOriginalModel());
            reloads = RELOAD_LISTENER.reloads;
        };
        ms.pushPose();
        Minecraft mc = Minecraft.getInstance();

        if (transformType == PetrolparkItemDisplayContexts.BELT) {
            if (stack.getOrCreateTag().contains("Flipped")) TransformStack.of(ms).rotateYDegrees(180);
        };

        ItemRenderer itemRenderer = mc.getItemRenderer();
        itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, model.getOriginalModel()); // Render the Item normally
        int pattern = CircuitPatternItem.getPattern(stack);
        for (int i = 0; i < 16; i++) {
            if (BinaryMatrix4x4.is1(pattern, i)) continue;
            itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, models[i]);
        };

        ms.popPose();
    };

    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("deprecation")
    public void generateModels(BakedModel originalModel) {
        models = new BakedModel[16];
        for (int i = 0; i < 16; i++) {
            final String number = String.valueOf(i);
            ResourceLocation rl = fragmentTextureResourceLocation.withPath(p -> p.concat("/" + number));
            models[i] = DummyBaker.bake(
                ITEM_MODEL_GENERATOR.generateBlockModel(
                    Material::sprite, // Tell the Item Model Generator how to fetch the sprite
                    new BlockModel(
                        rl,
                        new ArrayList<>(), // No elements - these get added by the Item Model Generator
                        Map.of("layer0", Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, rl))),
                        null,
                        null,
                        originalModel.getTransforms(), // The Item Model Generator sets these
                        new ArrayList<>()
                    )
                ),
                rl
            );
        };
    };

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(RELOAD_LISTENER);
    };

    protected static class ReloadListener implements ResourceManagerReloadListener {

        protected int reloads = 0;

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            reloads++;
        };

    };
    
};
