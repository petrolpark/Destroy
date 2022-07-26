package com.petrolpark.destroy.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.capability.methaddiction.PlayerMethAddiction;
import com.petrolpark.destroy.capability.methaddiction.PlayerMethAddictionProvider;
import com.petrolpark.destroy.commands.MethAddictionCommand;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.effect.DestroyMobEffects;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.item.SyringeItem;
import com.petrolpark.destroy.world.DestroyDamageSources;

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
        Player player = event.getEntity();
        if (player.hasEffect(DestroyMobEffects.METH_HIGH.get())) {
            event.setNewSpeed(event.getOriginalSpeed() + (0.5f * (player.getEffect(DestroyMobEffects.METH_HIGH.get()).getAmplifier() + 1))); //increase Haste with Meth High
        } else if (player.hasEffect(DestroyMobEffects.METH_WITHDRAWAL.get())) {
            event.setNewSpeed(event.getOriginalSpeed() - (0.3f * (player.getEffect(DestroyMobEffects.METH_WITHDRAWAL.get()).getAmplifier() + 1))); //decrease Haste with Meth Withdrawal
            if (event.getNewSpeed() <= 0f) { //mining speed probably shouldn't be less than 0
                event.setNewSpeed(0f);
            };
        };
    };

    @SubscribeEvent
    public static void playerHearsSound(PlayLevelSoundEvent.AtPosition event) {
        switch (event.getSource()) {
            case AMBIENT:
            case PLAYERS:
            case MUSIC:
            case VOICE:
            case NEUTRAL:
                break; //ignore certain sounds
            // case BLOCKS:
            // case HOSTILE:
            // case MASTER:
            // case RECORDS:
            // case WEATHER:
            default:
                Vec3 pos = event.getPosition();
                List<Entity> nearbyEntities = event.getLevel().getEntities(null, new AABB(pos.add(new Vec3(-5,-5,-5)), pos.add(new Vec3(5,5,5))));
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof Player) {
                        Player player = (Player)entity;
                        if (player.hasEffect(DestroyMobEffects.HANGOVER.get())) {
                            player.hurt(DestroyDamageSources.HEADACHE, 1f);
                        };
                    };
                }; 
                break;
        };
    };

    @SubscribeEvent
    public static void disableEatingWithMethWithdrawal(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().getItem().isEdible() && event.getItemStack().getItem() != DestroyItems.METHAMPHETAMINE_POWDER.get() && event.getEntity().hasEffect(DestroyMobEffects.METH_WITHDRAWAL.get())) {
            event.setCanceled(true);
        };
    };

    @SubscribeEvent
    public static void onPlayersWakeUp(SleepFinishedTimeEvent event) {
        for (Player player : event.getLevel().players()) {
            MobEffectInstance effect = player.getEffect(DestroyMobEffects.INEBRIATION.get());
            if (effect != null) {
                player.addEffect(new MobEffectInstance(DestroyMobEffects.HANGOVER.get(), DestroyAllConfigs.SERVER.substances.hangoverDuration.get() * (effect.getAmplifier() + 1)));
                player.removeEffect(DestroyMobEffects.INEBRIATION.get());
            };
        };
    };

    @SubscribeEvent
    public static void onSyringeAttack(LivingAttackEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity)) return;
        ItemStack itemStack = ((LivingEntity) attacker).getMainHandItem();
        if (!(itemStack.getItem() instanceof SyringeItem)) return;
        ((SyringeItem) itemStack.getItem()).onInject(itemStack, attacker.getLevel(), event.getEntity());
        ((LivingEntity) attacker).setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(DestroyItems.SYRINGE.get()));
    };
};
