package com.petrolpark.destroy.events;

import java.util.List;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.advancement.DestroyAdvancements;
import com.petrolpark.destroy.badge.BadgeHandler;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.entity.VatControllerBlockEntity;
import com.petrolpark.destroy.block.entity.VatSideBlockEntity;
import com.petrolpark.destroy.block.entity.behaviour.ExtendedBasinBehaviour;
import com.petrolpark.destroy.block.entity.behaviour.PollutingBehaviour;
import com.petrolpark.destroy.capability.chunk.ChunkCrudeOil;
import com.petrolpark.destroy.capability.entity.EntityChemicalPoison;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType;
import com.petrolpark.destroy.capability.level.pollution.LevelPollutionProvider;
import com.petrolpark.destroy.capability.player.PlayerBadges;
import com.petrolpark.destroy.capability.player.PlayerCrouching;
import com.petrolpark.destroy.capability.player.babyblue.PlayerBabyBlueAddiction;
import com.petrolpark.destroy.capability.player.babyblue.PlayerBabyBlueAddictionProvider;
import com.petrolpark.destroy.capability.player.previousposition.PlayerPreviousPositions;
import com.petrolpark.destroy.capability.player.previousposition.PlayerPreviousPositionsProvider;
import com.petrolpark.destroy.commands.BabyBlueAddictionCommand;
import com.petrolpark.destroy.commands.CrudeOilCommand;
import com.petrolpark.destroy.commands.PollutionCommand;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.effect.DestroyMobEffects;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.item.RedstoneProgrammerBlockItem;
import com.petrolpark.destroy.item.SyringeItem;
import com.petrolpark.destroy.item.TestTubeItem;
import com.petrolpark.destroy.network.DestroyMessages;
import com.petrolpark.destroy.network.packet.LevelPollutionS2CPacket;
import com.petrolpark.destroy.network.packet.SeismometerSpikeS2CPacket;
import com.petrolpark.destroy.sound.DestroySoundEvents;
import com.petrolpark.destroy.util.ChemistryDamageHelper;
import com.petrolpark.destroy.util.DestroyLang;
import com.petrolpark.destroy.util.DestroyTags.DestroyItemTags;
import com.petrolpark.destroy.util.InebriationHelper;
import com.petrolpark.destroy.util.PollutionHelper;
import com.petrolpark.destroy.util.RedstoneProgrammerItemHandler;
import com.petrolpark.destroy.world.damage.DestroyDamageSources;
import com.petrolpark.destroy.world.entity.goal.BuildSandCastleGoal;
import com.petrolpark.destroy.world.village.DestroyTrades;
import com.petrolpark.destroy.world.village.DestroyVillageAddition;
import com.petrolpark.destroy.world.village.DestroyVillagers;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.api.event.BlockEntityBehaviourEvent;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.fluids.drain.ItemDrainBlockEntity;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockItem;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.foundation.ModFilePackResources;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Couple;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent.CropGrowEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;

@Mod.EventBusSubscriber(modid = Destroy.MOD_ID)
public class DestroyCommonEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesLevel(AttachCapabilitiesEvent<Level> event) {
        Level level = event.getObject();
        if (!level.getCapability(LevelPollutionProvider.LEVEL_POLLUTION).isPresent()) {
            event.addCapability(Destroy.asResource("pollution"), new LevelPollutionProvider());
        };
    };

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof LivingEntity) {
            // Add Chemical Poison Capability
            if (!entity.getCapability(EntityChemicalPoison.Provider.ENTITY_CHEMICAL_POISON).isPresent()) {
                event.addCapability(Destroy.asResource("chemical_poison"), new EntityChemicalPoison.Provider());
            };
        };
        if (event.getObject() instanceof Player player) {
            // Add Baby Blue Addiction Capability
            if (!player.getCapability(PlayerBabyBlueAddictionProvider.PLAYER_BABY_BLUE_ADDICTION).isPresent()) {
                event.addCapability(Destroy.asResource("baby_blue_addiction"), new PlayerBabyBlueAddictionProvider());
            };
            // Add Previous Positions Capability
            if (!player.getCapability(PlayerPreviousPositionsProvider.PLAYER_PREVIOUS_POSITIONS).isPresent()) {
                event.addCapability(Destroy.asResource("previous_positions"), new PlayerPreviousPositionsProvider());
            };
            // Add Crouching Capability
            if (!player.getCapability(PlayerCrouching.Provider.PLAYER_CROUCHING).isPresent()) {
                event.addCapability(Destroy.asResource("crouching"), new PlayerCrouching.Provider());
            };
            // Add Badge Capability
            if (!player.getCapability(PlayerBadges.Provider.PLAYER_BADGES).isPresent()) {
                event.addCapability(Destroy.asResource("badges"), new PlayerBadges.Provider());
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

    /**
     * Collect the Player's Badges and refresh the Pollution they see.
     */
    @SubscribeEvent
    public static void onPlayerEntersWorld(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        Level level = player.level();

        // Update render info
        level.getCapability(LevelPollutionProvider.LEVEL_POLLUTION).ifPresent(levelPollution -> {
            DestroyMessages.sendToClient(new LevelPollutionS2CPacket(levelPollution), serverPlayer);
        });

        // Collect the Player's badges
        BadgeHandler.fetchAndAddBadgesIncludingEarlyBird(serverPlayer);
    };

    /**
     * Refresh the Pollution the Player sees and remove information on their previous positions.
     */
    @SubscribeEvent
    public static void onEntityEntersDimension(EntityTravelToDimensionEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ServerPlayer player)) return;
        MinecraftServer server = player.level().getServer();
        if (server == null) return;
        Level level = server.getLevel(event.getDimension());
        if (level == null) return;

        // Update render info
        level.getCapability(LevelPollutionProvider.LEVEL_POLLUTION).ifPresent(levelPollution -> {
            DestroyMessages.sendToClient(new LevelPollutionS2CPacket(levelPollution), player);
        });

        // Clear Chorus wine info
        player.getCapability(PlayerPreviousPositionsProvider.PLAYER_PREVIOUS_POSITIONS).ifPresent(previousPositions -> {
            previousPositions.clearPositions();
        });
    };

    /**
     * Conserve Baby Blue addiction and Badges across death.
     */
    @SubscribeEvent
    @MoveToPetrolparkLibrary
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // Copy Baby Blue Addiction Data
            event.getOriginal().getCapability(PlayerBabyBlueAddictionProvider.PLAYER_BABY_BLUE_ADDICTION).ifPresent(oldStore -> {
                event.getOriginal().getCapability(PlayerBabyBlueAddictionProvider.PLAYER_BABY_BLUE_ADDICTION).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
            // Copy Badge data
            event.getOriginal().getCapability(PlayerBadges.Provider.PLAYER_BADGES).ifPresent(oldStore -> {
                event.getOriginal().getCapability(PlayerBadges.Provider.PLAYER_BADGES).ifPresent(newStore -> {
                    newStore.setBadges(oldStore.getBadges());
                });
            });
        };
    };

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        new CrudeOilCommand(event.getDispatcher());
        new BabyBlueAddictionCommand(event.getDispatcher());
        new PollutionCommand(event.getDispatcher());
    };

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ChunkCrudeOil.class);
        event.register(LevelPollution.class);
        event.register(PlayerBabyBlueAddiction.class);
        event.register(PlayerPreviousPositions.class);
        event.register(PlayerCrouching.class);
        event.register(EntityChemicalPoison.class);
        event.register(PlayerBadges.class);
    };

    @SubscribeEvent
    public static void attachBasinBehaviours(BlockEntityBehaviourEvent<BasinBlockEntity> event) {
        BasinBlockEntity basin = event.getBlockEntity();
        event.attach(new PollutingBehaviour(basin));
        event.attach(new ExtendedBasinBehaviour(basin));
    };

    @SubscribeEvent
    public static void attachDrainBehaviours(BlockEntityBehaviourEvent<ItemDrainBlockEntity> event) {
        event.attach(new PollutingBehaviour(event.getBlockEntity()));
    };

    @SubscribeEvent
    public static void attachSpoutBehaviours(BlockEntityBehaviourEvent<SpoutBlockEntity> event) {
        event.attach(new PollutingBehaviour(event.getBlockEntity()));
    };

    /**
     * Add trades to the Innkeeper.
     */
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

    /**
     * Allow inns to spawn in Villages.
     */
    @SubscribeEvent
    public static void addVillagerBuildings(ServerAboutToStartEvent event) {
        Registry<StructureTemplatePool> templatePoolRegistry = event.getServer().registryAccess().registry(Registries.TEMPLATE_POOL).orElseThrow();
        Registry<StructureProcessorList> processorListRegistry = event.getServer().registryAccess().registry(Registries.PROCESSOR_LIST).orElseThrow();
        
        DestroyVillageAddition.addBuildingToPool(templatePoolRegistry, processorListRegistry, new ResourceLocation("minecraft:village/plains/houses"), "destroy:plains_inn", 5);
        DestroyVillageAddition.addBuildingToPool(templatePoolRegistry, processorListRegistry, new ResourceLocation("minecraft:village/desert/houses"), "destroy:desert_inn", 5);
    };

    /**
     * Store the Player's previous positions (for use with {@link com.petrolpark.destroy.item.ChorusWineItem Chorus Wine}),
     * and check if the Player should be urinating
     */
    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Level level = player.level();

        // Store the positions of this player for use with Chorus Wine
        player.getCapability(PlayerPreviousPositionsProvider.PLAYER_PREVIOUS_POSITIONS).ifPresent((playerPreviousPositions -> {
            playerPreviousPositions.incrementTickCounter();
            if (playerPreviousPositions.hasBeenSecond()) {
                playerPreviousPositions.recordPosition(player.blockPosition());
            };
        }));

        // Update the time this Player has been crouching/urinating
        BlockPos posOn = player.getOnPos();
        BlockState stateOn = level.getBlockState(posOn);
        boolean urinating = (stateOn.getBlock() == Blocks.WATER_CAULDRON || stateOn.getBlock() == Blocks.CAULDRON) && player.hasEffect(DestroyMobEffects.INEBRIATION.get());
        if (player.isCrouching()) {
            player.getCapability(PlayerCrouching.Provider.PLAYER_CROUCHING).ifPresent(crouchingCap -> {
                crouchingCap.ticksCrouching++;
                if (urinating) {crouchingCap.ticksUrinating++;} else crouchingCap.ticksUrinating = 0;
            });
        } else {
            player.getCapability(PlayerCrouching.Provider.PLAYER_CROUCHING).ifPresent(crouchingCap -> {
                crouchingCap.ticksCrouching = 0;
                crouchingCap.ticksUrinating = 0;
            });
        };

        // Enact the effects of urinating
        int ticksUrinating = player.getCapability(PlayerCrouching.Provider.PLAYER_CROUCHING).map(crouchingCap -> crouchingCap.ticksUrinating).orElse(0);
        if (ticksUrinating > 0) {
            Vec3 pos = player.position();
            if (level.isClientSide())
                level.addParticle(FluidFX.getFluidParticle(new FluidStack(DestroyFluids.URINE.get(), 1000)), pos.x, pos.y + 0.5f, pos.z, 0d, -0.07d, 0d);
            if (ticksUrinating % 40 == 0)
                DestroySoundEvents.URINATE.playOnServer(level, posOn);
            if (ticksUrinating == 119) {
                InebriationHelper.increaseInebriation(player, -1);
                DestroyAdvancements.URINATE.award(level, player);
                level.setBlockAndUpdate(posOn, DestroyBlocks.URINE_CAULDRON.getDefaultState());
            };
        };

        // Give the Player cancer if in direct sunlight
        if (level.canSeeSky(posOn.above()) && !player.hasEffect(DestroyMobEffects.SUN_PROTECTION.get())) {
            if (player.getRandom().nextInt(PollutionType.OZONE_DEPLETION.max * 600) < PollutionHelper.getPollution(level, PollutionType.OZONE_DEPLETION)) player.addEffect(DestroyMobEffects.cancerInstance());
        };
    };

    /**
     * Give the Player Haste/Mining Fatigue if they have Baby Blue High/Withdrawal respectively.
     */
    @SubscribeEvent
    @SuppressWarnings("null") // Stop giving warnings for effects we've already checked exist
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

    /**
     * Damage the Player if they are hungover and hear loud sounds.
     */
    @SubscribeEvent
    public static void playerHearsSound(PlayLevelSoundEvent.AtPosition event) {
        if (event.getOriginalVolume() < 0.5f) return;
        switch (event.getSource()) {
            // Ignore these sounds:
            case AMBIENT:
            case PLAYERS:
            case MUSIC:
            case VOICE:
            case NEUTRAL:
                break;
            // Don't ignore these sounds:
            case BLOCKS:
            case HOSTILE:
            case MASTER:
            case RECORDS:
            case WEATHER:
            default:
                Vec3 pos = event.getPosition();
                List<Entity> nearbyEntities = event.getLevel().getEntities(null, new AABB(pos.add(new Vec3(-5,-5,-5)), pos.add(new Vec3(5,5,5))));
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof Player) {
                        Player player = (Player)entity;
                        if (player.hasEffect(DestroyMobEffects.HANGOVER.get())) {
                            player.hurt(DestroyDamageSources.headache(player.level()), 1f);
                        };
                    };
                }; 
                break;
        };
    };

    /**
     * Disable eating if the Player is in Baby Blue withdrawal or wearing a Gas Mask.
     */
    @SubscribeEvent
    public static void disableEating(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();
        if (stack.getItem().isEdible()) {
            if (DestroyItemTags.CHEMICAL_PROTECTION_HEAD.matches(player.getItemBySlot(EquipmentSlot.HEAD).getItem())) {
                player.displayClientMessage(DestroyLang.translate("tooltip.eating_prevented.gas_mask").component(), true);
                event.setCanceled(true);
                return;
            };
            if (stack.getItem() != DestroyItems.BABY_BLUE_POWDER.get() && player.hasEffect(DestroyMobEffects.BABY_BLUE_WITHDRAWAL.get())) {
                player.displayClientMessage(DestroyLang.translate("tooltip.eating_prevented.baby_blue").component(), true);
                event.setCanceled(true);
            };
        };
    };

    @SubscribeEvent
    public static void chainCogwheels(PlayerInteractEvent.RightClickBlock event) {
        // ItemStack stack = event.getItemStack();
        // Level level = event.getLevel();
        // BlockState state = level.getBlockState(event.getPos());
        // if (stack.is(Items.CHAIN) && (IChainableBlock.isStateChainable(state) || DestroyBlocks.CHAINED_COGWHEEL.has(state))) {
        //     if (level.isClientSide()) {
        //         event.setCancellationResult(CogwheelChainingHandler.tryConnect(event.getPos()) ? InteractionResult.SUCCESS : InteractionResult.FAIL);
        //     } else {
        //         event.setCancellationResult(InteractionResult.SUCCESS);
        //     };
        //     event.setCanceled(true);
        // };
        //TODO uncomment when chaining is fully implemented
    };

    /**
     * Give Players a hangover when they wake up if they go to sleep drunk.
     */
    @SubscribeEvent
    public static void onPlayersWakeUp(SleepFinishedTimeEvent event) {
        for (Player player : event.getLevel().players()) {
            MobEffectInstance effect = player.getEffect(DestroyMobEffects.INEBRIATION.get());
            if (effect != null) {
                player.addEffect(new MobEffectInstance(DestroyMobEffects.HANGOVER.get(), DestroyAllConfigs.COMMON.substances.hangoverDuration.get() * (effect.getAmplifier() + 1)));
                player.removeEffect(DestroyMobEffects.INEBRIATION.get());
                DestroyAdvancements.HANGOVER.award(player.level(), player);
            };
        };
    };

    /**
     * Enact the effect of injecting a syringe when it is used to attack a Mob.
     */
    @SubscribeEvent
    public static void onSyringeAttack(LivingAttackEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity livingAttacker)) return;
        ItemStack itemStack = livingAttacker.getMainHandItem();
        if (!(itemStack.getItem() instanceof SyringeItem syringeItem)) return;
        syringeItem.onInject(itemStack, attacker.level(), event.getEntity());
        livingAttacker.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(DestroyItems.SYRINGE.get()));
    };

    // /**
    //  * Award the silly little Tally Hall reference Advancement.
    //  */
    // @SubscribeEvent
    // public static void onMechanicalHandAttack(LivingDeathEvent event) {
    //     if (!(event.getSource().getEntity() instanceof Player player)) return;
    //     if (AllBlocks.MECHANICAL_ARM.isIn(player.getMainHandItem()) && DestroyItems.ZIRCONIUM_PANTS.isIn(player.getItemBySlot(EquipmentSlot.LEGS))) {
    //         event.getEntity().spawnAtLocation(new ItemStack(DestroyItems.CHALK_DUST.get()));
    //         DestroyAdvancements.MECHANICAL_HANDS.award(player.level(), player);
    //     };
    // };

    /**
     * Award an Advancement for shooting Hefty Beetroots and allow Baby Villagers to build sandcastles.
     */
    @SubscribeEvent
    public static void onJoinEntity(EntityJoinLevelEvent event) {

        // Award achievement for shooting a Hefty Beetroot
        if (event.getEntity() instanceof PotatoProjectileEntity projectile && projectile.getOwner() instanceof ServerPlayer player && DestroyItemTags.HEFTY_BEETROOT.matches(projectile.getItem().getItem())) {
            DestroyAdvancements.SHOOT_HEFTY_BEETROOT.award(player.level(), player);
        };

        // Attach new AI to Villagers
        if (event.getEntity() instanceof Villager villager && villager.isBaby()) {
            villager.goalSelector.addGoal(0, new BuildSandCastleGoal(villager, true)); // It would be cleaner to use a Behavior rather than a Goal here but what you have failed to consider with that option is that I am lazy
        };
    
    };

    /**
     * Allow Strays to be captured and tears to be collected from crying Mobs.
     */
    @SubscribeEvent
    public static void rightClickEntity(PlayerInteractEvent.EntityInteractSpecific event) {
        Player player = event.getEntity();
        ItemStack itemStack = player.getItemInHand(event.getHand());

        // Capturing a Stray
        if (AllItems.EMPTY_BLAZE_BURNER.isIn(itemStack) && event.getTarget() instanceof Stray stray) {
            BlazeBurnerBlockItem item = (BlazeBurnerBlockItem) itemStack.getItem();
            if (item.hasCapturedBlaze()) return;

            event.getLevel().playSound(null, BlockPos.containing(stray.position()), SoundEvents.STRAY_HURT, SoundSource.HOSTILE, 0.25f, 0.75f);
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

    /**
     * Allow Redstone Link Frequencies to be added to Redstone Programmers without setting the Programmer itself as a Frequency,
     * and allow empty Test Tubes to be filled from Fluid Tanks
     */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();

        // Redstone Programmers
        LinkBehaviour link = BlockEntityBehaviour.get(level, pos, LinkBehaviour.TYPE);
        if (event.getItemStack().getItem() instanceof RedstoneProgrammerBlockItem && link != null) {
            if (player.isShiftKeyDown()) return;
            RedstoneProgrammerBlockItem.getProgram(stack, level, player).ifPresent((program) -> {
                Couple<Frequency> key = link.getNetworkKey();
                if (program.getChannels().stream().anyMatch(channel -> channel.getNetworkKey().equals(key))) {
                    event.setCancellationResult(InteractionResult.FAIL);
                    if (level.isClientSide()) player.displayClientMessage(DestroyLang.translate("tooltip.redstone_programmer.add_frequency.failure").style(ChatFormatting.RED).component(), true); 
                } else {
                    program.addBlankChannel(link.getNetworkKey());
                    RedstoneProgrammerBlockItem.setProgram(stack, program);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    if (level.isClientSide()) player.displayClientMessage(DestroyLang.translate("tooltip.redstone_programmer.add_frequency.success", key.getFirst().getStack().getHoverName(), key.getSecond().getStack().getHoverName()).component(), true); 
                };
            });
            event.setCanceled(true);
        };

        // Fill Test Tubes from any Fluid-containing block
        if (stack.getItem() instanceof TestTubeItem && TestTubeItem.isEmpty(stack) && player.isCreative()) {
            BlockEntity be = level.getBlockEntity(pos);
            if(be == null) return; // fix a crash
            if (!(be instanceof VatSideBlockEntity) && !(be instanceof VatControllerBlockEntity) && be.getCapability(ForgeCapabilities.FLUID_HANDLER, event.getFace()).map(handler -> {
                FluidStack drained = handler.drain(200, FluidAction.SIMULATE);
                if (DestroyFluids.isMixture(drained)) {
                    player.setItemInHand(event.getHand(), TestTubeItem.of(drained));
                    return true;
                };
                return false;
            }).orElse(false)) {
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
                event.setCanceled(true);  
            };
        };
    };

    /**
     * Trigger Handheld Seismometers when there are nearby Explosions.
     */
    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Start event) {
        Level level = event.getLevel();
        level.getEntitiesOfClass(Player.class, AABB.ofSize(event.getExplosion().getPosition(), 16, 16, 16), player -> true).forEach(player -> {
            boolean holdingSeismometer = false;
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack itemStack = player.getItemInHand(hand);
                if (DestroyItems.SEISMOMETER.isIn(itemStack)) holdingSeismometer = true;
            };
            if (holdingSeismometer) {
                // Generate the Oil
                LevelChunk chunk = level.getChunkAt(player.getOnPos());
                int bucketsOfOil = chunk.getCapability(ChunkCrudeOil.Provider.CHUNK_CRUDE_OIL).map(crudeOil -> {
                    crudeOil.generate(chunk, player);
                    return crudeOil.getAmount();
                }).orElse(0) / 1000;
                // Let the Player know how much Oil there is
                if (!level.isClientSide()) player.displayClientMessage(DestroyLang.translate("tooltip.seismometer.crude_oil", bucketsOfOil).component(), true);
                // Update the animation of the Seismometer(s)
                if (player instanceof ServerPlayer serverPlayer) DestroyMessages.sendToClient(new SeismometerSpikeS2CPacket(), serverPlayer);
                // Award Advancement if some oil was found
                if (bucketsOfOil > 0) DestroyAdvancements.USE_SEISMOMETER.award(level, player);
            };
        });
    };

    /**
     * Add a chance for birth failures depending on the level of smog in the world.
     */
    @SubscribeEvent
    public static void onBabyBirthed(BabyEntitySpawnEvent event) {
        if (!PollutionHelper.pollutionEnabled() || !DestroyAllConfigs.SERVER.pollution.breedingAffected.get()) return;
        Level level = event.getParentA().level();
        RandomSource random = event.getParentA().getRandom();
        if (event.getParentA().getRandom().nextInt(PollutionType.SMOG.max) <= PollutionHelper.getPollution(level, PollutionType.SMOG)) { // 0% chance of failure for 0 smog, 100% chance for full smog
            if (level instanceof ServerLevel serverLevel) {
                for (Mob parent : List.of(event.getParentA(), event.getParentB())) {
                    for(int i = 0; i < 7; ++i) {
                        serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, parent.getRandomX(1d), parent.getRandomY() + 0.5d, parent.getRandomZ(1d), 1, random.nextGaussian() * 0.5d, random.nextGaussian() * 0.5d, random.nextGaussian() * 0.5d, 0.02d);
                    };
                };
            };
            event.setCanceled(true);
        };
    };

    /**
     * Add a chance for crop growth failures depending on the level of smog, greenhouse gas and acid rain.
     */
    @SubscribeEvent
    public static void onPlantGrows(CropGrowEvent.Pre event) {
        if (!PollutionHelper.pollutionEnabled() || !DestroyAllConfigs.SERVER.pollution.growingAffected.get()) return;
        if (!(event.getLevel() instanceof Level level)) return;
        for (PollutionType pollutionType : new PollutionType[]{PollutionType.SMOG, PollutionType.GREENHOUSE, PollutionType.ACID_RAIN}) {
            if (level.random.nextInt(pollutionType.max) <= PollutionHelper.getPollution(level, pollutionType)) {
                event.setResult(Result.DENY);
                return;
            };
        };
    };

    /**
     * Damage entities with the effects of chemicals if they take off contaminated armor without washing it first.
     * @param event
     */
    @SubscribeEvent
    public static void onContaminatedArmorRemoved(LivingEquipmentChangeEvent event) {
        CompoundTag tag = event.getFrom().getOrCreateTag();
        if (tag.contains("ContaminatingFluid", Tag.TAG_COMPOUND)) {
            ChemistryDamageHelper.damage(event.getEntity().level(), event.getEntity(), FluidStack.loadFluidStackFromNBT(tag.getCompound("ContaminatingFluid")), true);
            ChemistryDamageHelper.decontaminate(event.getFrom());
        };
    };

    /**
     * Decrease Pollution when a tree is grown.
     */
    @SubscribeEvent
    public static void onTreeGrown(SaplingGrowTreeEvent event) {
        if (!(event.getLevel() instanceof Level level)) return;
        if (level.random.nextInt(3) == 0) PollutionHelper.changePollution(level, PollutionType.GREENHOUSE, -1);
        if (level.random.nextInt(3) == 0) PollutionHelper.changePollution(level, PollutionType.SMOG, -1);
        if (level.random.nextInt(3) == 0) PollutionHelper.changePollution(level, PollutionType.ACID_RAIN, -1);
    };

    /**
     * Remove dead Redstone Programmer items and naturally decrease Pollution over time.
     */
    @SubscribeEvent
    public static void onTick(TickEvent.LevelTickEvent event) {

        Level level = event.level;

        // Redstone Programmers
        RedstoneProgrammerItemHandler.tick(level);

        // Pollution
        for (PollutionType pollutionType : PollutionType.values()) {
            if (level.random.nextInt(100) == 0) PollutionHelper.changePollution(event.level, pollutionType, -1);
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
                event.addRepositorySource(consumer -> {
					Pack pack = Pack.readMetaAndCreate(Create.asResource("destroy_create_patches").toString(), Components.literal("Destroy Patches For Create"), true, id -> new ModFilePackResources(id, modFile, "resourcepacks/destroy_create_patches"), PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN);
					if (pack != null) {
						consumer.accept(pack);
					}
				});
			};
		};

	};
};
