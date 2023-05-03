package com.petrolpark.destroy.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;

import java.util.List;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.advancement.DestroyAdvancements;
import com.petrolpark.destroy.behaviour.DestroyAdvancementBehaviour;
import com.petrolpark.destroy.behaviour.PollutingBehaviour;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.capability.chunk.ChunkCrudeOil;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution;
import com.petrolpark.destroy.capability.level.pollution.LevelPollutionProvider;
import com.petrolpark.destroy.capability.player.PlayerCrouching;
import com.petrolpark.destroy.capability.player.babyblue.PlayerBabyBlueAddiction;
import com.petrolpark.destroy.capability.player.babyblue.PlayerBabyBlueAddictionProvider;
import com.petrolpark.destroy.capability.player.previousposition.PlayerPreviousPositions;
import com.petrolpark.destroy.capability.player.previousposition.PlayerPreviousPositionsProvider;
import com.petrolpark.destroy.commands.BabyBlueAddictionCommand;
import com.petrolpark.destroy.commands.PollutionCommand;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.effect.DestroyMobEffects;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.item.SyringeItem;
import com.petrolpark.destroy.network.DestroyMessages;
import com.petrolpark.destroy.network.packet.LevelPollutionS2CPacket;
import com.petrolpark.destroy.util.DestroyTags.DestroyItemTags;
import com.petrolpark.destroy.world.village.DestroyTrades;
import com.petrolpark.destroy.world.village.DestroyVillageAddition;
import com.petrolpark.destroy.world.village.DestroyVillagers;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.event.TileEntityBehaviourEvent;
import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainTileEntity;
import com.simibubi.create.content.contraptions.fluids.actors.SpoutTileEntity;
import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlockItem;
import com.simibubi.create.content.curiosities.weapons.PotatoProjectileEntity;
import com.simibubi.create.foundation.ModFilePackResources;
import com.petrolpark.destroy.world.DestroyDamageSources;
import com.petrolpark.destroy.world.entity.goal.BuildSandCastleGoal;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

@Mod.EventBusSubscriber(modid = Destroy.MOD_ID)
public class DestroyServerEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesLevel(AttachCapabilitiesEvent<Level> event) {
        Level level = event.getObject();
        if (!level.getCapability(LevelPollutionProvider.LEVEL_POLLUTION).isPresent()) {
            event.addCapability(Destroy.asResource("pollution"), new LevelPollutionProvider());
        };
    };

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            // Add Baby Blue Addiction Capability
            if (!player.getCapability(PlayerBabyBlueAddictionProvider.PLAYER_BABY_BLUE_ADDICTION).isPresent()) {
                event.addCapability(Destroy.asResource("baby_blue_addiction"), new PlayerBabyBlueAddictionProvider());
            };
            // Add Previous Positions Capability
            if (!player.getCapability(PlayerPreviousPositionsProvider.PLAYER_PREVIOUS_POSITIONS).isPresent()) {
                event.addCapability(Destroy.asResource("previous_positions"), new PlayerPreviousPositionsProvider());
            };
        };
    };

    @SubscribeEvent
    public static void onAttachCapabilitiesChunk(AttachCapabilitiesEvent<LevelChunk> event) {
        LevelChunk chunk = event.getObject();
        if (!chunk.getCapability(ChunkCrudeOil.Provider.CHUNK_CRUDE_OIL).isPresent()) {
            event.addCapability(Destroy.asResource("crude_oil"), new ChunkCrudeOil.Provider());
        };
    };

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // Copy Baby Blue Addiction Data
            event.getOriginal().getCapability(PlayerBabyBlueAddictionProvider.PLAYER_BABY_BLUE_ADDICTION).ifPresent(oldStore -> {
                event.getOriginal().getCapability(PlayerBabyBlueAddictionProvider.PLAYER_BABY_BLUE_ADDICTION).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        };
    };

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        new BabyBlueAddictionCommand(event.getDispatcher());
        new PollutionCommand(event.getDispatcher());
    };

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(LevelPollution.class);
        event.register(PlayerBabyBlueAddiction.class);
        event.register(PlayerPreviousPositions.class);
    };

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient()) return;
        Player player = event.player;

        // Store the positions of this player for use with Chorus Wine
        player.getCapability(PlayerPreviousPositionsProvider.PLAYER_PREVIOUS_POSITIONS).ifPresent((playerPreviousPositions -> {
            playerPreviousPositions.incrementTickCounter();
            if (playerPreviousPositions.hasBeenSecond()) {
                playerPreviousPositions.recordPosition(player.blockPosition());
            };
        }));

        // Update the time this Player has been crouching
        if (player.isCrouching()) {
            player.getCapability(PlayerCrouching.Provider.PLAYER_CROUCHING).ifPresent(crouchingCap -> crouchingCap.ticksCrouching++);
        } else {
            player.getCapability(PlayerCrouching.Provider.PLAYER_CROUCHING).ifPresent(crouchingCap -> crouchingCap.ticksCrouching = 0);
        };

        BlockState stateOn = player.getBlockStateOn();
        if (player.isCrouching() && stateOn.getBlock() == Blocks.WATER_CAULDRON && stateOn.getValue(BlockStateProperties.LEVEL_CAULDRON) == 1) {
        };

        LevelChunk chunk= player.getLevel().getChunkAt(player.blockPosition());
        chunk.getCapability(ChunkCrudeOil.Provider.CHUNK_CRUDE_OIL).ifPresent(crudeOil -> crudeOil.generate(chunk, player));
    };

    @SubscribeEvent
    @SuppressWarnings("null") // Capability is checked for nullness
    public static void changeMiningSpeedWithBabyBlueEffects(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player.hasEffect(DestroyMobEffects.BABY_BLUE_HIGH.get())) {
            event.setNewSpeed(event.getOriginalSpeed() + (0.5f * (player.getEffect(DestroyMobEffects.BABY_BLUE_HIGH.get()).getAmplifier() + 1))); // Increase Haste with Baby Blue High
        } else if (player.hasEffect(DestroyMobEffects.BABY_BLUE_WITHDRAWAL.get())) {
            event.setNewSpeed(event.getOriginalSpeed() - (0.3f * (player.getEffect(DestroyMobEffects.BABY_BLUE_WITHDRAWAL.get()).getAmplifier() + 1))); // Decrease Haste with Baby Blue Withdrawal
            if (event.getNewSpeed() <= 0f) { // Mining speed probably shouldn't be less than 0
                event.setNewSpeed(0f);
            };
        };
    };

    @SubscribeEvent
    public static void playerHearsSound(PlayLevelSoundEvent.AtPosition event) {
        switch (event.getSource()) {
            // Ignore these sounds:
            case AMBIENT:
            case PLAYERS:
            case MUSIC:
            case VOICE:
            case NEUTRAL:
                break;
            // Don't ignore these sounds:
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
    public static void onPlayerEntersWorld(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        Level level = player.getLevel();
        level.getCapability(LevelPollutionProvider.LEVEL_POLLUTION).ifPresent(levelPollution -> {
            DestroyMessages.sendToClient(new LevelPollutionS2CPacket(levelPollution), serverPlayer);
        });

        // try {
        //     Badge.getBadgesOf(player.getGameProfile().getName());
        // } finally {};
    };

    @SubscribeEvent
    public static void disableEatingWithBabyBlueWithdrawal(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().getItem().isEdible() && event.getItemStack().getItem() != DestroyItems.BABY_BLUE_POWDER.get() && event.getEntity().hasEffect(DestroyMobEffects.BABY_BLUE_WITHDRAWAL.get())) {
            event.setCanceled(true);
        };
    };

    @SubscribeEvent
    public static void onPlayersWakeUp(SleepFinishedTimeEvent event) {
        for (Player player : event.getLevel().players()) {
            MobEffectInstance effect = player.getEffect(DestroyMobEffects.INEBRIATION.get());
            if (effect != null) {
                player.addEffect(new MobEffectInstance(DestroyMobEffects.HANGOVER.get(), DestroyAllConfigs.COMMON.substances.hangoverDuration.get() * (effect.getAmplifier() + 1)));
                player.removeEffect(DestroyMobEffects.INEBRIATION.get());
                DestroyAdvancements.HANGOVER.award(player.getLevel(), player);
            };
        };
    };

    @SubscribeEvent
    public static void onSyringeAttack(LivingAttackEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity livingAttacker)) return;
        ItemStack itemStack = livingAttacker.getMainHandItem();
        if (!(itemStack.getItem() instanceof SyringeItem syringeItem)) return;
        syringeItem.onInject(itemStack, attacker.getLevel(), event.getEntity());
        livingAttacker.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(DestroyItems.SYRINGE.get()));
    };

    @SubscribeEvent
    public static void onMechanicalHandAttack(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (AllBlocks.MECHANICAL_ARM.isIn(player.getMainHandItem()) && DestroyItems.ZIRCONIUM_PANTS.isIn(player.getItemBySlot(EquipmentSlot.LEGS))) {
            event.getEntity().spawnAtLocation(new ItemStack(DestroyItems.CHALK_DUST.get()));
            DestroyAdvancements.MECHANICAL_HANDS.award(player.getLevel(), player);
        };
    };

    @SubscribeEvent
    public static void addVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() == DestroyVillagers.INNKEEPER.get()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            trades.get(1).addAll(DestroyTrades.INNKEEPER_NOVICE_TRADES);
            trades.get(2).addAll(DestroyTrades.INNKEEPER_APPRENTICE_TRADES);
            trades.get(3).addAll(DestroyTrades.INNKEEPER_JOURNEYMAN_TRADES);
            trades.get(4).addAll(DestroyTrades.INNKEEPER_EXPERT_TRADES);
            trades.get(5).addAll(DestroyTrades.INNKEEPER_MASTER_TRADES);
        };
    };

    @SubscribeEvent
    public static void addVillagerBuildings(ServerAboutToStartEvent event) {
        Registry<StructureTemplatePool> templatePoolRegistry = event.getServer().registryAccess().registry(Registry.TEMPLATE_POOL_REGISTRY).orElseThrow();
        Registry<StructureProcessorList> processorListRegistry = event.getServer().registryAccess().registry(Registry.PROCESSOR_LIST_REGISTRY).orElseThrow();
        
        DestroyVillageAddition.addBuildingToPool(templatePoolRegistry, processorListRegistry, new ResourceLocation("minecraft:village/plains/houses"), "destroy:plains_inn", 250);
    };

    @SubscribeEvent
    public static void onJoinEntity(EntityJoinLevelEvent event) {

        // Award achievement for shooting a Hefty Beetroot
        if (event.getEntity() instanceof PotatoProjectileEntity projectile && projectile.getOwner() instanceof ServerPlayer player && DestroyItemTags.HEFTY_BEETROOT.matches(projectile.getItem().getItem())) {
            DestroyAdvancements.SHOOT_HEFTY_BEETROOT.award(player.getLevel(), player);
        };

        // Attach new AI to Villagers
        if (event.getEntity() instanceof Villager villager && villager.isBaby()) {
            villager.goalSelector.addGoal(0, new BuildSandCastleGoal(villager, true)); // It would be cleaner to use a Behavior rather than a Goal here but what you have failed to consider with that option is that I am lazy
        };
    
    };

    @SubscribeEvent
    public static void attachBasinBehaviours(TileEntityBehaviourEvent<BasinTileEntity> event) {
        event.attach(new DestroyAdvancementBehaviour(event.getTileEntity()));
        event.attach(new PollutingBehaviour(event.getTileEntity()));
    };

    @SubscribeEvent
    public static void attachDrainBehaviours(TileEntityBehaviourEvent<ItemDrainTileEntity> event) {
        event.attach(new PollutingBehaviour(event.getTileEntity()));
    };

    @SubscribeEvent
    public static void attachSpoutBehaviours(TileEntityBehaviourEvent<SpoutTileEntity> event) {
        event.attach(new PollutingBehaviour(event.getTileEntity()));
    };

    @SubscribeEvent
    public static void rightClickEntity(PlayerInteractEvent.EntityInteractSpecific event) {
        Player player = event.getEntity();
        ItemStack itemStack = player.getItemInHand(event.getHand());

        // Capturing a Stray
        if (AllItems.EMPTY_BLAZE_BURNER.isIn(itemStack) && event.getTarget() instanceof Stray stray) {
            BlazeBurnerBlockItem item = (BlazeBurnerBlockItem) itemStack.getItem();
            if (item.hasCapturedBlaze()) return;

            event.getLevel().playSound(null, new BlockPos(stray.position()), SoundEvents.STRAY_HURT, SoundSource.HOSTILE, 0.25f, 0.75f);
            stray.discard(); // Remove the Stray

            // Give the Cooler to the Player
            ItemStack filled = DestroyBlocks.COOLER.asStack();
            if (!player.isCreative())
                itemStack.shrink(1);
            if (itemStack.isEmpty()) {
                player.setItemInHand(event.getHand(), filled);
            } else {
                player.getInventory().placeItemBackInInventory(filled);
            };

            DestroyAdvancements.CAPTURE_STRAY.award(event.getLevel(), player);

            event.setResult(Result.DENY);
            return;
        };

        // Collecting Tears
        if (itemStack.is(Items.GLASS_BOTTLE) && event.getTarget() instanceof LivingEntity livingEntity && livingEntity.hasEffect(DestroyMobEffects.CRYING.get())) {

            livingEntity.removeEffect(DestroyMobEffects.CRYING.get()); // Stop the crying

            // Give the Tear Bottle to the Player
            ItemStack filled = DestroyItems.TEAR_BOTTLE.asStack();
            if (!player.isCreative())
                itemStack.shrink(1);
            if (itemStack.isEmpty()) {
                player.setItemInHand(event.getHand(), filled);
            } else {
                player.getInventory().placeItemBackInInventory(filled);
            };

            DestroyAdvancements.COLLECT_TEARS.award(event.getLevel(), player);

            event.setResult(Result.DENY);
            return;
        };
    };

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
	public static class ModBusEvents {

        /**
         * Copied from the {@link com.simibubi.create.events.CommonEvents.ModBusEvents#addPackFinders Create source code}.
         * Add the Schematicannon Tooltip resource pack, which replaces the text of tooltips in Schematicannons
         * to reflect that they can accept any Destroy explosive, not just gunpowder.
         */
		@SubscribeEvent
		public static void addPackFinders(AddPackFindersEvent event) {
			if (event.getPackType() == PackType.CLIENT_RESOURCES) {
				IModFileInfo modFileInfo = ModList.get().getModFileById(Destroy.MOD_ID);
				if (modFileInfo == null) {
					Destroy.LOGGER.error("Could not find Destroy mod file info; built-in resource packs will be missing!");
					return;
				}
				IModFile modFile = modFileInfo.getFile();
				event.addRepositorySource((consumer, constructor) -> {
					consumer.accept(Pack.create(Destroy.asResource("schematicannon_tooltips").toString(), true, () -> new ModFilePackResources("Destroy Schematicannon Tooltips", modFile, "resourcepacks/schematicannon_tooltips"), constructor, Pack.Position.TOP, PackSource.BUILT_IN));
				});
			}
		}

	}
};
