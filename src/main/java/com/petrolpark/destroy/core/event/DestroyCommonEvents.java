package com.petrolpark.destroy.core.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyAdvancementTrigger;
import com.petrolpark.destroy.DestroyAttributes;
import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.DestroyItems;
import com.petrolpark.destroy.DestroyMessages;
import com.petrolpark.destroy.DestroyMobEffects;
import com.petrolpark.destroy.DestroyTags;
import com.petrolpark.destroy.DestroyTags.MobEffects;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.DestroyTrades;
import com.petrolpark.destroy.DestroyVillagers;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.content.oil.ChunkCrudeOil;
import com.petrolpark.destroy.content.oil.CrudeOilCommand;
import com.petrolpark.destroy.content.processing.glassblowing.BlowpipeItem;
import com.petrolpark.destroy.content.processing.trypolithography.CircuitPatternsS2CPacket;
import com.petrolpark.destroy.content.processing.trypolithography.RegenerateCircuitPatternCommand;
import com.petrolpark.destroy.content.product.CreatineItem;
import com.petrolpark.destroy.content.product.babyblue.BabyBlueAddictionCommand;
import com.petrolpark.destroy.content.product.babyblue.PlayerBabyBlueAddictionCapability;
import com.petrolpark.destroy.content.product.fireretardant.FireproofingHelper;
import com.petrolpark.destroy.content.product.periodictable.PeriodicTableBlock;
import com.petrolpark.destroy.content.product.periodictable.RefreshPeriodicTablePonderSceneS2CPacket;
import com.petrolpark.destroy.content.redstone.programmer.RedstoneProgrammerBlockItem;
import com.petrolpark.destroy.content.sandcastle.BuildSandCastleGoal;
import com.petrolpark.destroy.core.DestroyVillageAddition;
import com.petrolpark.destroy.core.block.IPickUpPutDownBlock;
import com.petrolpark.destroy.core.chemistry.hazard.EntityChemicalPoisonCapability;
import com.petrolpark.destroy.core.chemistry.novelcompounds.PlayerNovelCompoundsSynthesizedCapability;
import com.petrolpark.destroy.core.chemistry.storage.IMixtureStorageItem;
import com.petrolpark.destroy.core.chemistry.storage.measuringcylinder.MeasuringCylinderBlock;
import com.petrolpark.destroy.core.chemistry.storage.measuringcylinder.MeasuringCylinderBlockItem;
import com.petrolpark.destroy.core.chemistry.vat.material.SyncVatMaterialsS2CPacket;
import com.petrolpark.destroy.core.chemistry.vat.material.VatMaterial;
import com.petrolpark.destroy.core.chemistry.vat.material.VatMaterialResourceListener;
import com.petrolpark.destroy.core.debug.AttachedCheckCommand;
import com.petrolpark.destroy.core.explosion.mixedexplosive.ExplosiveProperties;
import com.petrolpark.destroy.core.extendedinventory.ExtendedInventory;
import com.petrolpark.destroy.core.player.PlayerCrouchingCapability;
import com.petrolpark.destroy.core.player.PlayerPreviousPositionsCapability;
import com.petrolpark.destroy.core.pollution.LevelPollutionS2CPacket;
import com.petrolpark.destroy.core.pollution.Pollution;
import com.petrolpark.destroy.core.pollution.Pollution.PollutionType;
import com.petrolpark.destroy.core.pollution.PollutionCommand;
import com.petrolpark.destroy.core.pollution.PollutionHelper;
import com.petrolpark.destroy.core.pollution.SyncChunkPollutionS2CPacket;
import com.petrolpark.recipe.ingredient.BlockIngredient;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockItem;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.createmod.catnip.data.Couple;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent.CropGrowEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Destroy.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class DestroyCommonEvents {

    @SubscribeEvent
    public static final void onAttachCapabilitiesLevel(AttachCapabilitiesEvent<Level> event) {
        Level level = event.getObject();
        if (!level.getCapability(Pollution.CAPABILITY).isPresent()) {
            event.addCapability(Destroy.asResource("pollution"), level instanceof PonderLevel ? new Pollution.PonderCapabilityProvider() : new Pollution.Level.Provider());
        };
    };

    @SubscribeEvent
    public static final void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof LivingEntity) {
            // Add Chemical Poison Capability
            if (!entity.getCapability(EntityChemicalPoisonCapability.Provider.ENTITY_CHEMICAL_POISON).isPresent()) {
                event.addCapability(Destroy.asResource("chemical_poison"), new EntityChemicalPoisonCapability.Provider());
            };
        };
        if (event.getObject() instanceof Player player) {
            // Add Baby Blue Addiction Capability
            if (!player.getCapability(PlayerBabyBlueAddictionCapability.CAPABILITY).isPresent()) {
                event.addCapability(Destroy.asResource("baby_blue_addiction"), new PlayerBabyBlueAddictionCapability.Provider());
            };
            // Add Previous Positions Capability
            if (!player.getCapability(PlayerPreviousPositionsCapability.CAPABILITY).isPresent()) {
                event.addCapability(Destroy.asResource("previous_positions"), new PlayerPreviousPositionsCapability.Provider());
            };
            // Add Crouching Capability
            if (!player.getCapability(PlayerCrouchingCapability.CAPABILITY).isPresent()) {
                event.addCapability(Destroy.asResource("crouching"), new PlayerCrouchingCapability.Provider());
            };
            // Add Novel compound Capability
            if (!player.getCapability(PlayerNovelCompoundsSynthesizedCapability.CAPABILITY).isPresent()) {
                event.addCapability(Destroy.asResource("novel_compounds_synthesized"), new PlayerNovelCompoundsSynthesizedCapability.Provider());
            };
        };
    };

    @SubscribeEvent
    public static final void onAttachCapabilitiesChunk(AttachCapabilitiesEvent<LevelChunk> event) {
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
    public static final void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
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
    public static final void onPlayerLoadsChunk(ChunkWatchEvent.Watch event) {
        event.getChunk().getCapability(Pollution.CAPABILITY).ifPresent(pollution -> {
            DestroyMessages.sendToClient(new SyncChunkPollutionS2CPacket(event.getPos(), pollution), event.getPlayer());
        });
    };

    /**
     * Refresh the Pollution the Player sees and remove information on their previous positions.
     */
    @SubscribeEvent
    public static final void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
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
        player.getCapability(PlayerPreviousPositionsCapability.CAPABILITY).ifPresent(previousPositions -> {
            previousPositions.clearPositions();
        });
    };

    /**
     * Conserve Baby Blue addiction etc. across death.
     */
    @SubscribeEvent
    public static final void onPlayerCloned(PlayerEvent.Clone event) {
        boolean keepInv = event.getEntity().level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        if (event.isWasDeath()) {
            // Copy Baby Blue Addiction Data
            if (DestroyAllConfigs.SERVER.substances.keepBabyBlueAddictionOnDeath.get() || keepInv) event.getOriginal().getCapability(PlayerBabyBlueAddictionCapability.CAPABILITY).ifPresent(oldStore -> {
                event.getEntity().getCapability(PlayerBabyBlueAddictionCapability.CAPABILITY).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });

            // Copy Novel Compound Data
            event.getOriginal().getCapability(PlayerNovelCompoundsSynthesizedCapability.CAPABILITY).ifPresent(oldStore -> {
                event.getEntity().getCapability(PlayerNovelCompoundsSynthesizedCapability.CAPABILITY).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        } else if (!event.isWasDeath() || DestroyAllConfigs.SERVER.substances.keepCreatineExtraInventorySizeOnDeath.get() || keepInv) {
            // Copy Extra Inventory due to Creatine
            @Nullable AttributeModifier extraInventoryModifier = event.getOriginal().getAttribute(DestroyAttributes.EXTRA_INVENTORY_SIZE.get()).getModifier(CreatineItem.EXTRA_INVENTORY_ATTRIBUTE_MODIFIER);
            if (extraInventoryModifier != null) event.getEntity().getAttribute(DestroyAttributes.EXTRA_INVENTORY_SIZE.get()).addPermanentModifier(extraInventoryModifier);
            @Nullable AttributeModifier extraHotbarModifier = event.getOriginal().getAttribute(DestroyAttributes.EXTRA_HOTBAR_SLOTS.get()).getModifier(CreatineItem.EXTRA_HOTBAR_ATTRIBUTE_MODIFIER);
            if (extraHotbarModifier != null) event.getEntity().getAttribute(DestroyAttributes.EXTRA_HOTBAR_SLOTS.get()).addPermanentModifier(extraHotbarModifier);
            ExtendedInventory.get(event.getEntity()).updateSize();
            if (keepInv) event.getEntity().getInventory().replaceWith(event.getOriginal().getInventory()); // Do this again as this Event is fired after it occurs
        };
    };

    @SubscribeEvent
    public static final void onRegisterCommands(RegisterCommandsEvent event) {
        new CrudeOilCommand(event.getDispatcher());
        new BabyBlueAddictionCommand(event.getDispatcher());
        new PollutionCommand(event.getDispatcher());
        new RegenerateCircuitPatternCommand(event.getDispatcher());
        new AttachedCheckCommand(event.getDispatcher());
    };

    @SubscribeEvent
    public static final void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ChunkCrudeOil.class);
        event.register(Pollution.class);
        event.register(PlayerBabyBlueAddictionCapability.class);
        event.register(PlayerPreviousPositionsCapability.class);
        event.register(PlayerCrouchingCapability.class);
        event.register(EntityChemicalPoisonCapability.class);
    };

    /**
     * Add trades to the Innkeeper.
     */
    @SubscribeEvent
    public static final void onVillagerTrades(VillagerTradesEvent event) {
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
    public static final void onServerAboutToStart(ServerAboutToStartEvent event) {
        Registry<StructureTemplatePool> templatePoolRegistry = event.getServer().registryAccess().registry(Registries.TEMPLATE_POOL).orElseThrow();
        Registry<StructureProcessorList> processorListRegistry = event.getServer().registryAccess().registry(Registries.PROCESSOR_LIST).orElseThrow();
        
        DestroyVillageAddition.addBuildingToPool(templatePoolRegistry, processorListRegistry, new ResourceLocation("minecraft:village/plains/houses"), "destroy:plains_inn", 5);
        DestroyVillageAddition.addBuildingToPool(templatePoolRegistry, processorListRegistry, new ResourceLocation("minecraft:village/desert/houses"), "destroy:desert_inn", 5);
    };

    /**
     * Award an Advancement for shooting Hefty Beetroots, allow Baby Villagers to build sandcastles,
     * and let lightning regenerate ozone
     */
    @SubscribeEvent
    public static final void onEntityJoinLevel(EntityJoinLevelEvent event) {

        // Award achievement for shooting a Hefty Beetroot
        if (event.getEntity() instanceof PotatoProjectileEntity projectile && projectile.getOwner() instanceof ServerPlayer player && DestroyTags.Items.HEFTY_BEETROOTS.matches(projectile.getItem().getItem())) {
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
    public static final void onPlayerEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
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
	public static final void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
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
     * and allow IPickUpPutDownBlock's Items to be consumed even if in Creative,
     * and allow empty Test Tubes to be filled from Fluid Tanks,
     * and prevent Flint and Steel from working if its Fireproof
     */
    @SubscribeEvent
    public static final void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
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

        // Fireproof Flint and Steel
        if (stack.getItem() == Items.FLINT_AND_STEEL && FireproofingHelper.isFireproof(player.level().registryAccess(), stack)) {
            DestroyAdvancementTrigger.FIREPROOF_FLINT_AND_STEEL.award(player.level(), player);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        };
    };

    /**
     * Add a chance for birth failures depending on the level of Smog in the world and whether either parent is infertile.
     */
    @SubscribeEvent
    public static final void onBabyEntitySpawn(BabyEntitySpawnEvent event) {
        Level level = event.getParentA().level();
        RandomSource random = event.getParentA().getRandom();
        List<Mob> parents = List.of(event.getParentA(), event.getParentB());
        boolean failure = false;

        // Failure due to infertility
        for (Mob parent : parents) {
            if (parent.getActiveEffects().stream().anyMatch(MobEffects.CAUSES_INFERTILITY::matches)) {
                failure = true;
                break;
            };
        };

        // Failure due to smog
        if (!failure && PollutionHelper.pollutionEnabled() && DestroyAllConfigs.SERVER.pollution.breedingAffected.get() && event.getParentA().getRandom().nextInt(PollutionType.SMOG.max) <= PollutionHelper.getPollution(level, event.getParentA().getOnPos(), PollutionType.SMOG)) failure = true; // 0% chance of failure for 0 smog, 100% chance for full smog

        if (failure) { 
            if (level instanceof ServerLevel serverLevel) {
                for (Mob parent : parents) for (int i = 0; i < 7; i++) serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, parent.getRandomX(1d), parent.getRandomY() + 0.5d, parent.getRandomZ(1d), 1, random.nextGaussian() * 0.5d, random.nextGaussian() * 0.5d, random.nextGaussian() * 0.5d, 0.02d);
            };
            event.setCanceled(true);
        };
    };

    /**
     * Add a chance for crop growth failures depending on the level of smog, greenhouse gas and acid rain.
     */
    @SubscribeEvent
    public static final void onCropGrowPre(CropGrowEvent.Pre event) {
        if (!PollutionHelper.pollutionEnabled() || !DestroyAllConfigs.SERVER.pollution.growingAffected.get()) return;
        if (!(event.getLevel() instanceof Level level)) return;
        BlockPos pos = event.getPos();
        for (PollutionType pollutionType : new PollutionType[]{PollutionType.SMOG, PollutionType.GREENHOUSE, PollutionType.ACID_RAIN}) {
            if (level.random.nextInt(pollutionType.max) <= PollutionHelper.getPollution(level, pos, pollutionType)) {
                if (level instanceof ServerLevel serverLevel) serverLevel.sendParticles(PollutionHelper.cropGrowthFailureParticles(), pos.getX() + 0.5d, pos.getY() + level.random.nextDouble() * event.getState().getShape(level, pos).max(Axis.Y), pos.getZ() + 0.5d, 10, 0.25d, 0.25d, 0.25d, 0.02d);
                event.setResult(Result.DENY);
                return;
            };
        };
    };

    /**
     * Add a chance for crop bonemealing failures depending on the level of smog, greenhouse gas and acid rain.
     */
    @SubscribeEvent
    public static final void onBonemeal(BonemealEvent event) {
        if (!PollutionHelper.pollutionEnabled() || !DestroyAllConfigs.SERVER.pollution.growingAffected.get() || !DestroyAllConfigs.SERVER.pollution.bonemealingAffected.get() || event.getStack().is(DestroyTags.Items.BONEMEAL_BYPASSES_POLLUTION.tag)) return;
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        for (PollutionType pollutionType : new PollutionType[]{PollutionType.SMOG, PollutionType.GREENHOUSE, PollutionType.ACID_RAIN}) {
            if (level.random.nextInt(pollutionType.max) <= PollutionHelper.getPollution(level, pos, pollutionType)) {
                if (level instanceof ServerLevel serverLevel) serverLevel.sendParticles(PollutionHelper.cropGrowthFailureParticles(), pos.getX() + 0.5d, pos.getY() + level.random.nextDouble() * event.getBlock().getShape(level, pos).max(Axis.Y), pos.getZ() + 0.5d, 10, 0.25d, 0.25d, 0.25d, 0.02d);
                event.setResult(Result.DENY);
                return;
            };
        };
    };

    /**
     * Decrease Pollution when a tree is grown.
     */
    @SubscribeEvent
    public static final void onSaplingGrowTree(SaplingGrowTreeEvent event) {
        if (!(event.getLevel() instanceof Level level) || !PollutionHelper.pollutionEnabled() || !DestroyAllConfigs.SERVER.pollution.growingTreesDecreasesPollution.get()) return;
        BlockPos pos = event.getPos();
        if (level.random.nextInt(3) == 0) PollutionHelper.changePollution(level, pos, PollutionType.GREENHOUSE, -1);
        if (level.random.nextInt(3) == 0) PollutionHelper.changePollution(level, pos, PollutionType.SMOG, -1);
        if (level.random.nextInt(3) == 0) PollutionHelper.changePollution(level, pos, PollutionType.ACID_RAIN, -1);
    };

    /**
     * Naturally decrease Pollution over time.
     */
    @SubscribeEvent
    public static final void onLevelTick(TickEvent.LevelTickEvent event) {
        Level level = event.level;

        // Global Pollution
        for (PollutionType pollutionType : PollutionType.values()) {
            if (PollutionHelper.pollutionEnabled() && !pollutionType.local && level.random.nextFloat() <= DestroyAllConfigs.SERVER.pollution.pollutionDecreaseRates.get(pollutionType).getF()) PollutionHelper.changePollutionGlobal(event.level, pollutionType, -1);
        };

    };

    @SubscribeEvent
    public static final void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new PeriodicTableBlock.Listener(event.getConditionContext()));
        event.addListener(Destroy.CIRCUIT_PATTERN_HANDLER.RELOAD_LISTENER);
        event.addListener(new ExplosiveProperties.Listener(event.getConditionContext()));
        VatMaterialResourceListener vatMaterialListener = new VatMaterialResourceListener(event.getConditionContext());
        event.addListener(vatMaterialListener);
    };

    @SubscribeEvent
	public static final void onLoadWorld(LevelEvent.Load event) {
        LevelAccessor level = event.getLevel();
		Destroy.CIRCUIT_PUNCHER_HANDLER.onLoadWorld(level);
        Destroy.CIRCUIT_PATTERN_HANDLER.onLevelLoaded(level);
	};

	@SubscribeEvent
	public static final void onUnloadWorld(LevelEvent.Unload event) {
		Destroy.CIRCUIT_PUNCHER_HANDLER.onUnloadWorld(event.getLevel());
        Destroy.CIRCUIT_PATTERN_HANDLER.onLevelUnloaded(event.getLevel());
	};
};
