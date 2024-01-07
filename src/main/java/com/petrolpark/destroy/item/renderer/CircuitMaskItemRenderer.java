package com.petrolpark.destroy.item.renderer;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.item.CircuitMaskItem;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Destroy.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CircuitMaskItemRenderer extends CustomRenderedItemModelRenderer {

    @OnlyIn(Dist.CLIENT)
    public static final BakedModel[] models = new BakedModel[16];

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ms.pushPose();
        Minecraft mc = Minecraft.getInstance();

        if (transformType == ItemDisplayContext.FIXED) {
            if (stack.getOrCreateTag().contains("Flipped")) TransformStack.cast(ms).rotateY(180);
            ms.scale(1.98f, 1.98f, 1.98f);
        };

        ItemRenderer itemRenderer = mc.getItemRenderer();
        itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, model.getOriginalModel()); // Render the Item normally
        int pattern = (stack.getItem() instanceof CircuitMaskItem item ? item.getPattern(stack): 0);
        for (int i = 0; i < 16; i++) {
            if (CircuitMaskItem.isPunched(pattern, i)) continue;
            itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, models[i]);
        };

        ms.popPose();
    };

    @SubscribeEvent
    public static void onRegisterModels(ModelEvent.RegisterAdditional event) {
        for (int i = 0; i < 16; i++) {
            event.register(Destroy.asResource("item/circuit_mask/"+i));
        };  
    };

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onModelsLoaded(ModelEvent.BakingCompleted event) {
        for (int i = 0; i < 16; i++) {
            models[i] = event.getModels().get(Destroy.asResource("item/circuit_mask/"+i));
        };  
    };
    
};
