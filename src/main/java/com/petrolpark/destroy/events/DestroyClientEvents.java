package com.petrolpark.destroy.events;

import com.petrolpark.destroy.block.renderer.BlockEntityBehaviourRenderer;
import com.petrolpark.destroy.item.renderer.SeismometerItemRenderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(Dist.CLIENT)
public class DestroyClientEvents {

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            SeismometerItemRenderer.tick();
        } else {
            BlockEntityBehaviourRenderer.tick();
        };
    };

    @SubscribeEvent
    public static void onRender(RenderTickEvent event) {
        
    };
};
