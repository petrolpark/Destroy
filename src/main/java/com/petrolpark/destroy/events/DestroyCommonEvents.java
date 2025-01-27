package com.petrolpark.destroy.events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.advancement.DestroyAdvancementTrigger;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.IPickUpPutDownBlock;
import com.petrolpark.destroy.block.MeasuringCylinderBlock;
import com.petrolpark.destroy.block.PeriodicTableBlock;
import com.petrolpark.destroy.block.PeriodicTableBlock.PeriodicTableEntry;
import com.petrolpark.destroy.block.entity.behaviour.ExtendedBasinBehaviour;
import com.petrolpark.destroy.block.entity.behaviour.PollutingBehaviour;
import com.petrolpark.destroy.capability.Pollution;
import com.petrolpark.destroy.capability.Pollution.PollutionType;
import com.petrolpark.destroy.capability.chunk.ChunkCrudeOil;
import com.petrolpark.destroy.capability.entity.EntityChemicalPoison;
import com.petrolpark.destroy.capability.player.PlayerCrouching;
import com.petrolpark.destroy.capability.player.PlayerNovelCompoundsSynthesized;
import com.petrolpark.destroy.capability.player.babyblue.PlayerBabyBlueAddiction;
import com.petrolpark.destroy.capability.player.babyblue.PlayerBabyBlueAddictionProvider;
import com.petrolpark.destroy.capability.player.previousposition.PlayerPreviousPositions;
import com.petrolpark.destroy.capability.player.previousposition.PlayerPreviousPositionsProvider;
import com.petrolpark.destroy.commands.AttachedCheckCommand;
import com.petrolpark.destroy.commands.BabyBlueAddictionCommand;
import com.petrolpark.destroy.commands.CrudeOilCommand;
import com.petrolpark.destroy.commands.PollutionCommand;
import com.petrolpark.destroy.commands.RegenerateCircuitPatternCommand;
import com.petrolpark.destroy.commands.RegenerateCircuitPatternCommand.CircuitPatternIdArgument;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.config.DestroySubstancesConfigs;
import com.petrolpark.destroy.effect.DestroyMobEffects;
import com.petrolpark.destroy.entity.attribute.DestroyAttributes;
import com.petrolpark.destroy.entity.player.ExtendedInventory;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.item.BlowpipeItem;
import com.petrolpark.destroy.item.CircuitPatternItem;
import com.petrolpark.destroy.item.CreatineItem;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.item.DiscStamperItem;
import com.petrolpark.destroy.item.IMixtureStorageItem;
import com.petrolpark.destroy.item.MeasuringCylinderBlockItem;
import com.petrolpark.destroy.item.RedstoneProgrammerBlockItem;
import com.petrolpark.destroy.item.SeismographItem;
import com.petrolpark.destroy.item.SeismographItem.Seismograph;
import com.petrolpark.destroy.network.DestroyMessages;
import com.petrolpark.destroy.network.packet.CircuitPatternsS2CPacket;
import com.petrolpark.destroy.network.packet.LevelPollutionS2CPacket;
import com.petrolpark.destroy.network.packet.RefreshPeriodicTablePonderSceneS2CPacket;
import com.petrolpark.destroy.network.packet.SeismometerSpikeS2CPacket;
import com.petrolpark.destroy.network.packet.SyncChunkPollutionS2CPacket;
import com.petrolpark.destroy.network.packet.SyncVatMaterialsS2CPacket;
import com.petrolpark.destroy.recipe.CircuitDeployerApplicationRecipe;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.petrolpark.destroy.recipe.DiscStampingRecipe;
import com.petrolpark.destroy.recipe.ingredient.CircuitPatternIngredient;
import com.petrolpark.destroy.sound.DestroySoundEvents;
import com.petrolpark.destroy.util.ChemistryDamageHelper;
import com.petrolpark.destroy.util.DestroyLang;
import com.petrolpark.destroy.util.FireproofingHelper;
import com.petrolpark.destroy.util.DestroyTags.DestroyItemTags;
import com.petrolpark.destroy.util.DestroyTags.DestroyMobEffectTags;
import com.petrolpark.destroy.util.PollutionHelper;
import com.petrolpark.destroy.util.RedstoneProgrammerItemHandler;
import com.petrolpark.destroy.util.vat.VatMaterial;
import com.petrolpark.destroy.util.vat.VatMaterialResourceListener;
import com.petrolpark.destroy.world.damage.DestroyDamageSources;
import com.petrolpark.destroy.world.entity.goal.BuildSandCastleGoal;
import com.petrolpark.destroy.world.explosion.ExplosiveProperties;
import com.petrolpark.destroy.world.village.DestroyTrades;
import com.petrolpark.destroy.world.village.DestroyVillageAddition;
import com.petrolpark.destroy.world.village.DestroyVillagers;
import com.petrolpark.recipe.ingredient.BlockIngredient;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.event.BlockEntityBehaviourEvent;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.fluids.drain.ItemDrainBlockEntity;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerRecipeSearchEvent;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.foundation.ModFilePackResources;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.ponder.PonderWorld;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Iterate;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent.CropGrowEvent;
import net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = Destroy.MOD_ID)
public class DestroyCommonEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesLevel(AttachCapabilitiesEvent<Level> event) {
        Level level = event.getObject();
        if (!level.getCapability(Pollution.CAPABILITY).isPresent()) {
            event.addCapability(Destroy.asResource("pollution"), level instanceof PonderWorld ? new Pollution.PonderProvider() : new Pollution.Level.Provider());
        };
    };

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
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
            // Add Novel compound Capability
            if (!player.getCapability(PlayerNovelCompoundsSynthesized.Provider.PLAYER_NOVEL_COMPOUNDS_SYNTHESIZED).isPresent()) {
                event.addCapability(Destroy.asResource("novel_compounds_synthesized"), new PlayerNovelCompoundsSynthesized.Provider());
            };
        };
    };

    @SubscribeEvent
    public static void onAttachCapabilitiesChunk(AttachCapabilitiesEvent<LevelChunk> event) {
        LevelChunk chunk = event.getObject();
        if (!chunk.getCapability(ChunkCrudeOil.Provider.CHUNK_CRUDE_OIL).isPresent()) {
            event.addCapability(Destroy.asResource("crude_oil"), new ChunkCrudeOil.Provider());
        };
        if (!chunk.getCapability(Pollution.CAPABILITY).isPresent()) {
            event.addCapability(Destroy.asResource("pollution"), new Pollution.Chunk.Provider());
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
        level.getCapability(Pollution.CAPABILITY).ifPresent(levelPollution -> {
            DestroyMessages.sendToClient(new LevelPollutionS2CPacket(levelPollution), serverPlayer);
        });

        // Update the Ponders for periodic table blocks
        DestroyMessages.sendToClient(new RefreshPeriodicTablePonderSceneS2CPacket(), serverPlayer);

        // Update the circuit pattern crafting recipes
        DestroyMessages.sendToClient(new CircuitPatternsS2CPacket(Destroy.CIRCUIT_PATTERN_HANDLER.getAllPatterns()), serverPlayer);

        // Update the known Vat Materials
        Map<BlockIngredient<?>, VatMaterial> datapackMaterials = new HashMap<>(VatMaterial.BLOCK_MATERIALS.size());
        VatMaterial.BLOCK_MATERIALS.entrySet().stream()
            .filter(entry -> !entry.getValue().builtIn())
            .forEach(entry -> datapackMaterials.put(entry.getKey(), entry.getValue()));
        DestroyMessages.sendToClient(new SyncVatMaterialsS2CPacket(datapackMaterials), serverPlayer);
    };

    @SubscribeEvent
    public static void onPlayerLoadsChunk(ChunkWatchEvent.Watch event) {
        event.getChunk().getCapability(Pollution.CAPABILITY).ifPresent(pollution -> {
            DestroyMessages.sendToClient(new SyncChunkPollutionS2CPacket(event.getPos(), pollution), event.getPlayer());
        });
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
        level.getCapability(Pollution.CAPABILITY).ifPresent(levelPollution -> {
            DestroyMessages.sendToClient(new LevelPollutionS2CPacket(levelPollution), player);
        });

        // Clear Chorus wine info
        player.getCapability(PlayerPreviousPositionsProvider.PLAYER_PREVIOUS_POSITIONS).ifPresent(previousPositions -> {
            previousPositions.clearPositions();
        });
    };

    /**
     * Conserve Baby Blue addiction etc. across death.
     */
    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        boolean keepInv = event.getEntity().level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        if (event.isWasDeath()) {
            // Copy Baby Blue Addiction Data
            if (DestroyAllConfigs.SERVER.substances.keepBabyBlueAddictionOnDeath.get() || keepInv) event.getOriginal().getCapability(PlayerBabyBlueAddictionProvider.PLAYER_BABY_BLUE_ADDICTION).ifPresent(oldStore -> {
                event.getEntity().getCapability(PlayerBabyBlueAddictionProvider.PLAYER_BABY_BLUE_ADDICTION).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });

            // Copy Novel Compound Data
            event.getOriginal().getCapability(PlayerNovelCompoundsSynthesized.Provider.PLAYER_NOVEL_COMPOUNDS_SYNTHESIZED).ifPresent(oldStore -> {
                event.getEntity().getCapability(PlayerNovelCompoundsSynthesized.Provider.PLAYER_NOVEL_COMPOUNDS_SYNTHESIZED).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        } else if (!event.isWasDeath() || DestroyAllConfigs.SERVER.substances.keepCreatineExtraInventorySizeOnDeath.get() || keepInv) {
            // Copy Extra Inventory due to Creatine
            @Nullable AttributeModifier extraInventoryModifier = event.getOriginal().getAttribute(DestroyAttributes.EXTRA_INVENTORY_SIZE.get()).getModifier(CreatineItem.EXTRA_INVENTORY_ATTRIBUTE_MODIFIER);
            if(extraInventoryModifier != null) event.getEntity().getAttribute(DestroyAttributes.EXTRA_INVENTORY_SIZE.get()).addPermanentModifier(extraInventoryModifier);
            @Nullable AttributeModifier extraHotbarModifier = event.getOriginal().getAttribute(DestroyAttributes.EXTRA_HOTBAR_SLOTS.get()).getModifier(CreatineItem.EXTRA_HOTBAR_ATTRIBUTE_MODIFIER);
            if(extraHotbarModifier != null) event.getEntity().getAttribute(DestroyAttributes.EXTRA_HOTBAR_SLOTS.get()).addPermanentModifier(extraHotbarModifier);
            ExtendedInventory.get(event.getEntity()).updateSize();
            if (keepInv) event.getEntity().getInventory().replaceWith(event.getOriginal().getInventory()); // Do this again as this Event is fired after it occurs
        };
    };

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        new CrudeOilCommand(event.getDispatcher());
        new BabyBlueAddictionCommand(event.getDispatcher());
        new PollutionCommand(event.getDispatcher());
        new RegenerateCircuitPatternCommand(event.getDispatcher());
        new AttachedCheckCommand(event.getDispatcher());
    };

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ChunkCrudeOil.class);
        event.register(Pollution.class);
        event.register(PlayerBabyBlueAddiction.class);
        event.register(PlayerPreviousPositions.class);
        event.register(PlayerCrouching.class);
        event.register(EntityChemicalPoison.class);
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
        if (!level.isClientSide()) player.getCapability(PlayerPreviousPositionsProvider.PLAYER_PREVIOUS_POSITIONS).ifPresent((playerPreviousPositions -> {
            playerPreviousPositions.incrementTickCounter();
            if (playerPreviousPositions.hasBeenSecond()) {
                playerPreviousPositions.recordPosition(player.blockPosition());
            };
        }));

        // Update the time this Player has been crouching/urinating
        BlockPos posOn = player.getOnPos();
        BlockState stateOn = level.getBlockState(posOn);
        boolean urinating = (stateOn.getBlock() == Blocks.WATER_CAULDRON || stateOn.getBlock() == Blocks.CAULDRON) && player.hasEffect(DestroyMobEffects.FULL_BLADDER.get());
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
                DestroyMobEffects.increaseEffectLevel(player, DestroyMobEffects.FULL_BLADDER.get(), -1, 0);
                DestroyAdvancementTrigger.URINATE.award(level, player);
                level.setBlockAndUpdate(posOn, DestroyBlocks.URINE_CAULDRON.getDefaultState());
            };
        };

        // Give the Player cancer if in direct sunlight
        if (level.canSeeSky(posOn.above()) && !player.hasEffect(DestroyMobEffects.SUN_PROTECTION.get())) {
            if (player.getRandom().nextInt(PollutionType.OZONE_DEPLETION.max * 600) < PollutionHelper.getPollution(level, posOn, PollutionType.OZONE_DEPLETION)) player.addEffect(DestroyMobEffects.cancerInstance());
        };
    };

    /**
     * Give the Player Haste/Mining Fatigue if they have Baby Blue High/Withdrawal respectively.
     */
    @SubscribeEvent
    @SuppressWarnings("null") // Stop giving warnings for effects we've already checked exist
    public static void changeMiningSpeedWithBabyBlueEffects(PlayerEvent.BreakSpeed event) {
        if (!DestroySubstancesConfigs.babyBlueEnabled()) return;
        Player player = event.getEntity();
        if (player.hasEffect(DestroyMobEffects.BABY_BLUE_HIGH.get())) {
            event.setNewSpeed(event.getOriginalSpeed() + (DestroyAllConfigs.SERVER.substances.babyBlueMiningSpeedBonus.getF() * (player.getEffect(DestroyMobEffects.BABY_BLUE_HIGH.get()).getAmplifier() + 1))); // Increase Haste with Baby Blue High
        } else if (player.hasEffect(DestroyMobEffects.BABY_BLUE_WITHDRAWAL.get())) {
            event.setNewSpeed(event.getOriginalSpeed() + (DestroyAllConfigs.SERVER.substances.babyBlueWidthdrawalSpeedBonus.getF() * (player.getEffect(DestroyMobEffects.BABY_BLUE_WITHDRAWAL.get()).getAmplifier() + 1))); // Decrease Haste with Baby Blue Withdrawal
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
        if (event.getOriginalVolume() < DestroyAllConfigs.SERVER.substances.soundSourceThresholds.get(event.getSource()).getF()) return;
        Vec3 pos = event.getPosition();
        float radius = DestroyAllConfigs.SERVER.substances.hangoverNoiseTriggerRadius.getF();
        List<Entity> nearbyEntities = event.getLevel().getEntities(null, new AABB(pos.add(new Vec3(-radius,-radius,-radius)), pos.add(new Vec3(radius, radius, radius))));
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity.hasEffect(DestroyMobEffects.HANGOVER.get())) {
                    livingEntity.hurt(DestroyDamageSources.headache(livingEntity.level()), DestroyAllConfigs.SERVER.substances.soundSourceDamage.get(event.getSource()).getF());
                };
            };
        }; 
    };

    /**
     * Disable eating if the Player is in Baby Blue withdrawal or wearing a Gas Mask,
     * cancel the action of Flint and Steel if it's been made fireproof
     */
    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();

        // Preventing eating
        if (stack.isEdible()) {
            if (ChemistryDamageHelper.Protection.MOUTH_COVERED.isProtected(player)) {
                player.displayClientMessage(DestroyLang.translate("tooltip.eating_prevented.mouth_protected").component(), true);
                event.setCanceled(true);
                return;
            };
            if (DestroySubstancesConfigs.babyBlueEnabled() && stack.getItem() != DestroyItems.BABY_BLUE_POWDER.get() && player.hasEffect(DestroyMobEffects.BABY_BLUE_WITHDRAWAL.get()) && !stack.getFoodProperties(player).canAlwaysEat()) {
                player.displayClientMessage(DestroyLang.translate("tooltip.eating_prevented.baby_blue").component(), true);
                event.setCanceled(true);
            };
        };

        // Fireproof Flint and Steel
        if (stack.getItem() == Items.FLINT_AND_STEEL && FireproofingHelper.isFireproof(player.level().registryAccess(), stack)) {
            DestroyAdvancementTrigger.FIREPROOF_FLINT_AND_STEEL.award(player.level(), player);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
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
                player.addEffect(new MobEffectInstance(DestroyMobEffects.HANGOVER.get(), DestroyAllConfigs.SERVER.substances.hangoverDuration.get() * (effect.getAmplifier() + 1)));
                player.removeEffect(DestroyMobEffects.INEBRIATION.get());
                DestroyAdvancementTrigger.HANGOVER.award(player.level(), player);
            };
        };
    };

    /**
     * Award an Advancement for shooting Hefty Beetroots, allow Baby Villagers to build sandcastles,
     * and let lightning regenerate ozone
     */
    @SubscribeEvent
    public static void onJoinEntity(EntityJoinLevelEvent event) {

        // Award achievement for shooting a Hefty Beetroot
        if (event.getEntity() instanceof PotatoProjectileEntity projectile && projectile.getOwner() instanceof ServerPlayer player && DestroyItemTags.HEFTY_BEETROOTS.matches(projectile.getItem().getItem())) {
            DestroyAdvancementTrigger.SHOOT_HEFTY_BEETROOT.award(player.level(), player);
        };

        // Attach new AI to Villagers
        if (event.getEntity() instanceof Villager villager && villager.isBaby()) {
            villager.goalSelector.addGoal(0, new BuildSandCastleGoal(villager, true)); // It would be cleaner to use a Behavior rather than a Goal here but what you have failed to consider with that option is that I am lazy
        };

        // Regenerate ozone
        if (event.getEntity().getType() == EntityType.LIGHTNING_BOLT && PollutionHelper.pollutionEnabled() && DestroyAllConfigs.SERVER.pollution.lightningRegeneratesOzone.get()) {
            PollutionHelper.changePollution(event.getLevel(), event.getEntity().getOnPos(), PollutionType.OZONE_DEPLETION, -50);
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

            DestroyAdvancementTrigger.CAPTURE_STRAY.award(event.getLevel(), player);

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

            DestroyAdvancementTrigger.COLLECT_TEARS.award(event.getLevel(), player);

            event.setResult(Result.DENY);
            return;
        };
    };

    /**
     * Transfer from {@link IMixtureStorageItem Mixture storage Items}
     * and instantly pick up {@link IPickUpPutDown} Blocks
     * and allow Blowpipes to break off the finished Item
     */
    @SubscribeEvent
	public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = event.getItemStack();

        // Filling placed Measuring Cylinders
        if (state.getBlock() instanceof MeasuringCylinderBlock) {
            InteractionResult result = MeasuringCylinderBlockItem.tryOpenTransferScreen(world, pos, state, event.getFace(), player, event.getHand(), stack, true);
            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
                event.setCanceled(true);
                return;
            };
        };
        
        // Emptying other glassware
        if (stack.getItem() instanceof IMixtureStorageItem mixtureItem) {
            InteractionResult result = mixtureItem.attack(world, pos, state, event.getFace(), player, event.getHand(), stack);
            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
                event.setCanceled(true);
                return;
            };
        };

        // Instantly picking up blocks
        if (state.getBlock() instanceof IPickUpPutDownBlock) {
            if (!(player instanceof FakePlayer)) {
                ItemStack cloneItemStack = state.getCloneItemStack(new BlockHitResult(Vec3.ZERO, event.getFace(), event.getPos(), false), world, pos, player);
                world.destroyBlock(pos, false);
                if (world.getBlockState(pos) != state && !world.isClientSide()) player.getInventory().placeItemBackInInventory(cloneItemStack);
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            };
        };

        // Blowpipes
        if (event.getItemStack().getItem() instanceof BlowpipeItem blowpipe) {
            if (blowpipe.finishBlowing(stack, world, player)) {
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            };
        };
	};

    /**
     * Allow Redstone Link Frequencies to be added to Redstone Programmers without setting the Programmer itself as a Frequency,
     * and allow IPickUpPutDownBlock's Items to be consumed even if in Creative
     * and allow empty Test Tubes to be filled from Fluid Tanks
     */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();

        // Redstone Programmers
        if (event.getItemStack().getItem() instanceof RedstoneProgrammerBlockItem) {
            LinkBehaviour link = BlockEntityBehaviour.get(level, pos, LinkBehaviour.TYPE);
            if (link != null && !player.isShiftKeyDown()) {
                RedstoneProgrammerBlockItem.getProgram(stack, level, player).ifPresent((program) -> {
                    Couple<Frequency> key = link.getNetworkKey();
                    if (program.getChannels().stream().anyMatch(channel -> channel.getNetworkKey().equals(key))) {
                        event.setCancellationResult(InteractionResult.FAIL);
                        if (level.isClientSide()) player.displayClientMessage(DestroyLang.translate("tooltip.redstone_programmer.add_frequency.failure.exists").style(ChatFormatting.RED).component(), true); 
                    } else if (program.getChannels().size() >= DestroyAllConfigs.SERVER.blocks.redstoneProgrammerMaxChannels.get()) {
                        event.setCancellationResult(InteractionResult.FAIL);
                        if (level.isClientSide()) player.displayClientMessage(DestroyLang.translate("tooltip.redstone_programmer.add_frequency.failure.full").style(ChatFormatting.RED).component(), true); 
                    } else {
                        program.addBlankChannel(link.getNetworkKey());
                        RedstoneProgrammerBlockItem.setProgram(stack, program);
                        event.setCancellationResult(InteractionResult.SUCCESS);
                        if (level.isClientSide()) player.displayClientMessage(DestroyLang.translate("tooltip.redstone_programmer.add_frequency.success", key.getFirst().getStack().getHoverName(), key.getSecond().getStack().getHoverName()).component(), true); 
                    };
                });
                event.setCanceled(true);
                return;
            };
        };

        // Consuming certain Items, even if in Creative
        if (!AllBlocks.DEPLOYER.has(state) && event.getItemStack().getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IPickUpPutDownBlock) {
            InteractionResult result = stack.useOn(new UseOnContext(player, event.getHand(), event.getHitVec()));
            if (result.consumesAction() && player instanceof ServerPlayer serverPlayer) CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
            event.setCancellationResult(result);
            if (result != InteractionResult.PASS) event.setCanceled(true);
            return;
        };
    };

    /**
     * Trigger Handheld Seismometers when there are nearby Explosions.
     */
    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Start event) {
        Level level = event.getLevel();
        level.getEntitiesOfClass(Player.class, AABB.ofSize(event.getExplosion().getPosition(), 16, 16, 16), player -> true).forEach(player -> {
            if (player.getInventory().hasAnyMatching(DestroyItems.SEISMOMETER::isIn)) {
                int chunkX = SectionPos.blockToSectionCoord(player.getOnPos().getX());
                int chunkZ = SectionPos.blockToSectionCoord(player.getOnPos().getZ());
                
                List<ItemStack> seismographs = ExtendedInventory.get(player).stream()
                    .filter(DestroyItems.SEISMOGRAPH::isIn)
                    .filter(stack -> {
                        MapItemSavedData mapData = MapItem.getSavedData(stack, level);
                        return (SeismographItem.mapChunkCenter(chunkX) * 16 == mapData.centerX && SeismographItem.mapChunkCenter(chunkZ) * 16 == mapData.centerZ);
                    })
                    .toList();

                // Generate the Oil in this chunk
                LevelChunk chunk = level.getChunk(chunkX, chunkZ);
                LazyOptional<ChunkCrudeOil> ccoOptional = chunk.getCapability(ChunkCrudeOil.Provider.CHUNK_CRUDE_OIL);
                int newOilGenerated = 0;
                if (ccoOptional.isPresent()) {
                    ChunkCrudeOil cco = ccoOptional.resolve().get();
                    if (!cco.isGenerated()) {
                        cco.generate(chunk, player);
                        newOilGenerated = cco.getAmount();
                    };
                };         

                boolean newInfo = false; // Whether new information was added to any Seismographs

                // Add information to Seismographs and display information to the player
                if (level instanceof ServerLevel serverLevel) {
                    byte xSignals = ChunkCrudeOil.getSignals(serverLevel, chunkX, chunkZ, true);
                    byte zSignals = ChunkCrudeOil.getSignals(serverLevel, chunkX, chunkZ, false);
                    int modX = chunkX - SeismographItem.mapChunkLowerCorner(chunkX);
                    int modZ = chunkZ - SeismographItem.mapChunkLowerCorner(chunkZ);
                    for (ItemStack stack : seismographs) {
                        Seismograph seismograph = SeismographItem.readSeismograph(stack);
                        // Mark this chunk as definitively seismically active or not on the Seismograph
                        newInfo |= seismograph.mark(modX, modZ, (zSignals & 1 << modZ) != 0 ? Seismograph.Mark.TICK : Seismograph.Mark.CROSS);
                        // Add nonogram info
                        newInfo |= seismograph.discoverColumn(modX, level, player);
                        newInfo |= seismograph.discoverRow(modZ, level, player);
                        seismograph.getColumns()[modX] = zSignals;
                        seismograph.getRows()[modZ] = xSignals;
                        SeismographItem.writeSeismograph(stack, seismograph);
                    };
                    // Show message (and award XP if necessary)
                    if (newOilGenerated > 0) {
                        player.displayClientMessage(DestroyLang.translate("tooltip.seismometer.struck_oil", newOilGenerated / 1000).component(), true);
                        ExperienceOrb.award(serverLevel, player.position(), newOilGenerated / 10000);  
                    } else if (seismographs.isEmpty()) player.displayClientMessage(DestroyLang.translate("tooltip.seismometer.no_seismograph").style(ChatFormatting.RED).component(), true);
                    else if (newInfo) player.displayClientMessage(DestroyLang.translate("tooltip.seismometer.added_info").component(), true);
                    else player.displayClientMessage(DestroyLang.translate("tooltip.seismometer.no_new_info").style(ChatFormatting.RED).component(), true);
                };
                
                // Update the animation of the Seismometer(s)
                if (player instanceof ServerPlayer serverPlayer) DestroyMessages.sendToClient(new SeismometerSpikeS2CPacket(), serverPlayer);
                // Award Advancement if some Seismograph info was filled in
                if (newInfo) DestroyAdvancementTrigger.USE_SEISMOMETER.award(level, player);
            };
        });
    };

    /**
     * Add a chance for birth failures depending on the level of smog in the world and whether either parent is infertile.
     */
    @SubscribeEvent
    public static void onBabyBirthed(BabyEntitySpawnEvent event) {
        Level level = event.getParentA().level();
        RandomSource random = event.getParentA().getRandom();
        List<Mob> parents = List.of(event.getParentA(), event.getParentB());
        boolean failure = false;

        // Failure due to infertility
        for (Mob parent : parents) {
            if (parent.getActiveEffects().stream().anyMatch(DestroyMobEffectTags.CAUSES_INFERTILITY::matches)) {
                failure = true;
                break;
            };
        };

        // Failure due to smog
        if (!failure && PollutionHelper.pollutionEnabled() && DestroyAllConfigs.SERVER.pollution.breedingAffected.get() && event.getParentA().getRandom().nextInt(PollutionType.SMOG.max) <= PollutionHelper.getPollution(level, event.getParentA().getOnPos(), PollutionType.SMOG)) { // 0% chance of failure for 0 smog, 100% chance for full smog
            failure = true;
        };

        if (failure) { 
            if (level instanceof ServerLevel serverLevel) {
                for (Mob parent : parents) {
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
        BlockPos pos = event.getPos();
        for (PollutionType pollutionType : new PollutionType[]{PollutionType.SMOG, PollutionType.GREENHOUSE, PollutionType.ACID_RAIN}) {
            if (level.random.nextInt(pollutionType.max) <= PollutionHelper.getPollution(level, pos, pollutionType)) {
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(PollutionHelper.cropGrowthFailureParticles(), pos.getX() + 0.5d, pos.getY() + level.random.nextDouble() * event.getState().getShape(level, pos).max(Axis.Y), pos.getZ() + 0.5d, 10, 0.25d, 0.25d, 0.25d, 0.02d);
                };
                event.setResult(Result.DENY);
                return;
            };
        };
    };

    /**
     * Add a chance for crop bonemealing failures depending on the level of smog, greenhouse gas and acid rain.
     */
    @SubscribeEvent
    public static void onCropBonemealed(BonemealEvent event) {
        if (!PollutionHelper.pollutionEnabled() || !DestroyAllConfigs.SERVER.pollution.growingAffected.get() || !DestroyAllConfigs.SERVER.pollution.bonemealingAffected.get() || event.getStack().is(DestroyItemTags.BONEMEAL_BYPASSES_POLLUTION.tag)) return;
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        for (PollutionType pollutionType : new PollutionType[]{PollutionType.SMOG, PollutionType.GREENHOUSE, PollutionType.ACID_RAIN}) {
            if (level.random.nextInt(pollutionType.max) <= PollutionHelper.getPollution(level, pos, pollutionType)) {
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(PollutionHelper.cropGrowthFailureParticles(), pos.getX() + 0.5d, pos.getY() + level.random.nextDouble() * event.getBlock().getShape(level, pos).max(Axis.Y), pos.getZ() + 0.5d, 10, 0.25d, 0.25d, 0.25d, 0.02d);
                };
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
        if (event.getSlot() == EquipmentSlot.MAINHAND || event.getSlot() == EquipmentSlot.OFFHAND || !event.getFrom().hasTag()) return;
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
        if (!(event.getLevel() instanceof Level level) || !PollutionHelper.pollutionEnabled() || !DestroyAllConfigs.SERVER.pollution.growingTreesDecreasesPollution.get()) return;
        BlockPos pos = event.getPos();
        if (level.random.nextInt(3) == 0) PollutionHelper.changePollution(level, pos, PollutionType.GREENHOUSE, -1);
        if (level.random.nextInt(3) == 0) PollutionHelper.changePollution(level, pos, PollutionType.SMOG, -1);
        if (level.random.nextInt(3) == 0) PollutionHelper.changePollution(level, pos, PollutionType.ACID_RAIN, -1);
    };

    /**
     * Remove dead Redstone Programmer items, naturally decrease Pollution over time, and tick decaying Items.
     */
    @SubscribeEvent
    public static void onTick(TickEvent.LevelTickEvent event) {

        Level level = event.level;

        // Redstone Programmers
        RedstoneProgrammerItemHandler.tick(level);

        // Global Pollution
        for (PollutionType pollutionType : PollutionType.values()) {
            if (PollutionHelper.pollutionEnabled() && !pollutionType.local && level.random.nextFloat() <= DestroyAllConfigs.SERVER.pollution.pollutionDecreaseRates.get(pollutionType).getF()) PollutionHelper.changePollutionGlobal(event.level, pollutionType, -1);
        };

    };

    @SubscribeEvent
    public static void onGetDeployerRecipes(DeployerRecipeSearchEvent event) {
        RecipeWrapper inv = event.getInventory();

        ItemStack appliedStack = inv.getItem(1);

        // Disc Stamping
        if (appliedStack.getItem() instanceof DiscStamperItem && inv.getItem(0).is(DestroyItems.BLANK_MUSIC_DISC.get())) {
            event.addRecipe(() -> Optional.ofNullable(DiscStampingRecipe.create(appliedStack)), 75);
        };

        // Circuit deployer application
        if (inv.hasAnyMatching(stack -> stack.getItem() instanceof CircuitPatternItem)) {
            Recipe<?> recipe = event.getRecipe() instanceof CircuitDeployerApplicationRecipe ? event.getRecipe() : null;
            if (recipe == null) recipe = DestroyRecipeTypes.CIRCUIT_DEPLOYING.find(event.getInventory(), event.getBlockEntity().getLevel()).orElse(null);
            if (recipe == null) recipe = SequencedAssemblyRecipe.getRecipe(event.getBlockEntity().getLevel(), event.getInventory(), DestroyRecipeTypes.CIRCUIT_DEPLOYING.getType(), CircuitDeployerApplicationRecipe.class).orElse(null);
            if (recipe != null && recipe instanceof CircuitDeployerApplicationRecipe circuitRecipe) event.addRecipe(() -> Optional.of(circuitRecipe.specify(inv)), 150);
        };
    };

    /**
     * Reward the Player with an advancement for assembling a full periodic table,
     * and add the player to the RememberPlacerBehaviours of non-Destroy Block Entities.
     */
    @SubscribeEvent
    public static void onPlayerPlacesBlock(EntityPlaceEvent event) {
        BlockState state = event.getPlacedBlock();
        Level level = event.getEntity().level();

        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // Periodic Table advancement
            if (PeriodicTableBlock.isPeriodicTableBlock(state)) {
                int[] thisPos = PeriodicTableBlock.getXY(state.getBlock());
                for (Direction direction : Iterate.horizontalDirections) {
                    boolean allPresent = true;
                    checkEachBlock: for (PeriodicTableEntry entry : PeriodicTableBlock.ELEMENTS) {
                        if (!entry.blocks().contains(level.getBlockState(event.getPos().offset(PeriodicTableBlock.relative(thisPos, new int[]{entry.x(), entry.y()}, direction))).getBlock())) {
                            allPresent = false;
                            break checkEachBlock;
                        };
                    };
                    if (allPresent) {
                        DestroyAdvancementTrigger.PERIODIC_TABLE.award(level, serverPlayer);
                        return;
                    };
                };
            };
        };

    };

    @SubscribeEvent
    public static void registerReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new PeriodicTableBlock.Listener(event.getConditionContext()));
        event.addListener(Destroy.CIRCUIT_PATTERN_HANDLER.RELOAD_LISTENER);
        event.addListener(new ExplosiveProperties.Listener(event.getConditionContext()));
        VatMaterialResourceListener vatMaterialListener = new VatMaterialResourceListener(event.getConditionContext());
        event.addListener(vatMaterialListener);
    };

    @SubscribeEvent
	public static void onLoadWorld(LevelEvent.Load event) {
        LevelAccessor level = event.getLevel();
		Destroy.CIRCUIT_PUNCHER_HANDLER.onLoadWorld(level);
        Destroy.CIRCUIT_PATTERN_HANDLER.onLevelLoaded(level);
	};

	@SubscribeEvent
	public static void onUnloadWorld(LevelEvent.Unload event) {
		Destroy.CIRCUIT_PUNCHER_HANDLER.onUnloadWorld(event.getLevel());
        Destroy.CIRCUIT_PATTERN_HANDLER.onLevelUnloaded(event.getLevel());
	};

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
	public static class ModBusEvents {
        @SubscribeEvent
        public static void onCreateAttributes(EntityAttributeModificationEvent event) {
            event.add(EntityType.PLAYER, DestroyAttributes.EXTRA_INVENTORY_SIZE.get());
            event.add(EntityType.PLAYER, DestroyAttributes.EXTRA_HOTBAR_SLOTS.get());
        };

        /**
         * Copied from the {@link com.simibubi.create.events.CommonEvents.ModBusEvents#addPackFinders Create source code}.
         * Add the Schematicannon Tooltip resource pack, which replaces the text of tooltips in Schematicannons
         * to reflect that they can accept any Destroy explosive, not just gunpowder.
         */
		@SubscribeEvent
		public static void addPackFinders(AddPackFindersEvent event) {
            IModFileInfo modFileInfo = ModList.get().getModFileById(Destroy.MOD_ID);
            if (modFileInfo == null) {
                Destroy.LOGGER.error("Could not find Destroy mod file info; built-in resource packs will be missing!");
                return;
            };
            IModFile modFile = modFileInfo.getFile();
			if (event.getPackType() == PackType.CLIENT_RESOURCES) {
                // Resource packs
                event.addRepositorySource(consumer -> {
					Pack pack = Pack.readMetaAndCreate(Destroy.asResource("create_patches").toString(), Components.literal("Destroy Patches For Create"), true, id -> new ModFilePackResources(id, modFile, "resourcepacks/create_patches"), PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN);
					if (pack != null) consumer.accept(pack);
				});
			} else {
                // Datapacks
                event.addRepositorySource(consumer -> {
                    Pack pack = Pack.readMetaAndCreate(Destroy.asResource("tfmg_compat").toString(), Components.literal("Destroy Compat With Create: TFMG"), false, id -> new ModFilePackResources(id, modFile, "datapacks/tfmg_compat"), PackType.SERVER_DATA, Pack.Position.TOP, PackSource.DEFAULT);
                    if (pack != null) consumer.accept(pack);
                });
            };
		};

        @SubscribeEvent
        public static void registerIngredientTypes(RegisterEvent event) {
            if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
                // Ingredient types
                CraftingHelper.register(Destroy.asResource("circuit_pattern_item"), CircuitPatternIngredient.SERIALIZER);
                CraftingHelper.register(Destroy.asResource("example_circuit_mask"), CircuitDeployerApplicationRecipe.ExampleMaskIngredient.SERIALIZER); // Should never actually appear in a JSON recipe
            };
        };

        @SubscribeEvent
        public static void registerCommandArgumentTypes(RegisterEvent event) {
            event.register(Registries.COMMAND_ARGUMENT_TYPE, Destroy.asResource("circuit_pattern_resource_location"), () -> {
                return ArgumentTypeInfos.registerByClass(CircuitPatternIdArgument.class, SingletonArgumentInfo.contextFree(CircuitPatternIdArgument::create));
            });
	    };
	};
};
