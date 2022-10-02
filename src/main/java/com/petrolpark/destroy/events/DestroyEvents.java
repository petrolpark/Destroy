package com.petrolpark.destroy.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.commands.MethAddictionCommand;
import com.petrolpark.destroy.effect.DestroyMobEffects;
import com.petrolpark.destroy.methaddiction.PlayerMethAddiction;
import com.petrolpark.destroy.methaddiction.PlayerMethAddictionProvider;

@Mod.EventBusSubscriber(modid = Destroy.MOD_ID)
public class DestroyEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event){
         if (event.getObject() instanceof Player) {
            // Add Meth Addiction
            if (!event.getObject().getCapability(PlayerMethAddictionProvider.PLAYER_METH_ADDICTION).isPresent()) {
                event.addCapability(new ResourceLocation(Destroy.MOD_ID, "properties"), new PlayerMethAddictionProvider());
            };
         };
    };

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // Copy Meth Addiction Data
            event.getOriginal().getCapability(PlayerMethAddictionProvider.PLAYER_METH_ADDICTION).ifPresent(oldStore -> {
                event.getOriginal().getCapability(PlayerMethAddictionProvider.PLAYER_METH_ADDICTION).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        };
    };

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        new MethAddictionCommand(event.getDispatcher());
    };

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerMethAddiction.class);
    };

    @SubscribeEvent
    public static void changeMiningSpeedWithMethEffects(PlayerEvent.BreakSpeed event) {
        Player player = event.getPlayer();
        if (player.hasEffect(DestroyMobEffects.METH_HIGH.get())) {
            event.setNewSpeed(event.getOriginalSpeed() + (0.5f * (player.getEffect(DestroyMobEffects.METH_HIGH.get()).getAmplifier() + 1))); //increase Haste with Meth High
        } else if (player.hasEffect(DestroyMobEffects.METH_WITHDRAWAL.get())) {
            event.setNewSpeed(event.getOriginalSpeed() - (0.3f * (player.getEffect(DestroyMobEffects.METH_WITHDRAWAL.get()).getAmplifier() + 1))); //decrease Haste with Meth Withdrawal
            if (event.getNewSpeed() <= 0f) { //mining speed probably shouldn't be less than 0
                event.setNewSpeed(0f);
            };
        };
    };
}
