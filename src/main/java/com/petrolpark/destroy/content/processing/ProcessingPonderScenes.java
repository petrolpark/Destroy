package com.petrolpark.destroy.content.processing;

import java.util.List;

import com.petrolpark.client.ponder.PonderPlayer;
import com.petrolpark.client.ponder.instruction.LivingEntitySwingInstruction;
import com.petrolpark.client.ponder.instruction.OutlineAABBInstruction;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.DestroyFluids;
import com.petrolpark.destroy.DestroyItems;
import com.petrolpark.destroy.DestroyVillagers;
import com.petrolpark.destroy.chemistry.legacy.LegacyMixture;
import com.petrolpark.destroy.chemistry.legacy.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.minecraft.MixtureFluid;
import com.petrolpark.destroy.client.DestroyParticleTypes;
import com.petrolpark.destroy.content.logistics.siphon.SiphonBlockEntity;
import com.petrolpark.destroy.content.processing.ageing.AgeingBarrelBlockEntity;
import com.petrolpark.destroy.content.processing.ageing.AgingBarrelBlock;
import com.petrolpark.destroy.content.processing.centrifuge.CentrifugeBlockEntity;
import com.petrolpark.destroy.content.processing.distillation.BubbleCapBlock;
import com.petrolpark.destroy.content.processing.distillation.BubbleCapBlockEntity;
import com.petrolpark.destroy.content.processing.glassblowing.BlowpipeBlockEntity;
import com.petrolpark.destroy.content.processing.treetap.BlockTapping;
import com.petrolpark.destroy.content.processing.treetap.TreeTapBlockEntity;
import com.petrolpark.destroy.core.fluid.gasparticle.GasParticleData;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.chassis.StickerBlock;
import com.simibubi.create.content.contraptions.chassis.StickerBlockEntity;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.math.VecHelper;
import net.createmod.ponder.api.ParticleEmitter;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.PonderWorldParticles;
import net.createmod.ponder.foundation.element.InputWindowElement;
import net.createmod.ponder.foundation.instruction.FadeOutOfSceneInstruction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class ProcessingPonderScenes {
    
    public static final FluidStack PURPLE_FLUID, BLUE_FLUID, RED_FLUID;

    // Define coloured Fluids
    static {
        PURPLE_FLUID = new FluidStack(PotionFluid.withEffects(1000, Potions.TURTLE_MASTER, List.of()), 1000);
        BLUE_FLUID = new FluidStack(PotionFluid.withEffects(500, Potions.AWKWARD, List.of()), 1000);
        RED_FLUID = new FluidStack(PotionFluid.withEffects(500, Potions.HEALING, List.of()), 1000);
    };

    private static FluidStack clearMixture(int amount) {
        ReadOnlyMixture mixture = LegacyMixture.pure(DestroyMolecules.WATER);
        return MixtureFluid.of(amount, mixture);
    };

    public static void agingBarrel(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("aging_barrel", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();

        BlockPos barrel = util.grid().at(1, 1, 1);

        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.idle(10);
        scene.world().showSection(util.select().position(barrel), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(barrel, Direction.UP));
        scene.idle(100);

        scene.overlay().showControls(util.vector().blockSurface(barrel, Direction.UP), Pointing.DOWN, 30)
            .rightClick()
            .withItem(new ItemStack(Items.WATER_BUCKET));
        scene.world().modifyBlockEntity(barrel, AgeingBarrelBlockEntity.class, be -> {
            be.getTank().fill(new FluidStack(Fluids.WATER, 1000), FluidAction.EXECUTE);
        });
        scene.idle(50);

        ItemStack yeast = DestroyItems.YEAST.asStack();
        scene.world().createItemEntity(util.vector().centerOf(barrel.above(2)), Vec3.ZERO, yeast);
        scene.idle(10);
        scene.world().modifyBlockEntity(barrel, AgeingBarrelBlockEntity.class, be -> {
            be.inventory.insertItem(0, yeast, false);
        });
        scene.world().createItemEntity(util.vector().centerOf(barrel.above(2)), Vec3.ZERO, new ItemStack(Items.WHEAT));
        scene.idle(10);

        scene.world().setBlock(barrel, DestroyBlocks.AGING_BARREL.getDefaultState().setValue(AgingBarrelBlock.IS_OPEN, false), false);
        scene.world().modifyBlockEntity(barrel, AgeingBarrelBlockEntity.class, be -> {
            be.inventory.clearContent();
            be.getTank().drain(1000, FluidAction.EXECUTE);
            be.getTank().fill(new FluidStack(DestroyFluids.UNDISTILLED_MOONSHINE.get(), 1000), FluidAction.EXECUTE);
        });
        scene.idle(20);

        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(barrel, Direction.UP));

        for (int i = 1; i <= 4; i++) {
            BlockState state = DestroyBlocks.AGING_BARREL.getDefaultState()
                .setValue(AgingBarrelBlock.IS_OPEN, false)
                .setValue(AgingBarrelBlock.PROGRESS, i);
            scene.world().setBlock(barrel, state, false);
            scene.idle(20);
        };
        scene.idle(20);

        scene.overlay().showControls(util.vector().blockSurface(barrel, Direction.UP), Pointing.DOWN, 30)
            .rightClick();
        scene.idle(50);
        scene.world().setBlock(barrel, DestroyBlocks.AGING_BARREL.getDefaultState().setValue(AgingBarrelBlock.IS_OPEN, true), false);
        scene.idle(50);

        scene.world().createEntity(w -> {
			Villager villagerEntity = EntityType.VILLAGER.create(w);
            if (villagerEntity == null) return villagerEntity; // This should never occur
			Vec3 v = util.vector().topOf(util.grid().at(1, 0, 0));
            villagerEntity.setVillagerData(new VillagerData(VillagerType.PLAINS, DestroyVillagers.INNKEEPER.get(), 0));
			villagerEntity.setPos(v.x, v.y, v.z);
            villagerEntity.xo = v.x;
            villagerEntity.yo = v.y;
            villagerEntity.zo = v.z;
			return villagerEntity;
		});
        scene.idle(10);
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().topOf(util.grid().at(1, 1, 0)))
            .attachKeyFrame();
        scene.idle(120); 

        scene.markAsFinished();
    };

    public static void centrifugeGeneric(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("centrifuge", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        // Define Selections
        Selection pipework = util.select().fromTo(2, 1, 1, 2, 5, 3);
        Selection kinetics = util.select().fromTo(1, 1, 2, 1, 3, 5)
            .add(util.select().fromTo(2, 1, 4, 2, 5, 4))
            .add(util.select().position(2, 0, 5));
        BlockPos centrifuge = util.grid().at(2, 3, 3);
        BlockPos denseOutputPump = util.grid().at(2, 3, 2);
        BlockPos lightOutputPump = util.grid().at(2, 2, 3);

        // Pre-fill the input Tank
        scene.world().modifyBlockEntity(util.grid().at(2, 5, 3), FluidTankBlockEntity.class, be -> {
            be.getTankInventory().fill(PURPLE_FLUID, FluidAction.EXECUTE);
        });
        // Ensure the Centrifuge faces the right way
        scene.world().modifyBlockEntity(centrifuge, CentrifugeBlockEntity.class, be -> {
            be.setPondering();
            be.attemptRotation(false);
        });

        scene.world().showSection(util.select().fromTo(0, 0, 0, 4, 0, 4), Direction.UP);
        scene.world().showSection(pipework, Direction.DOWN);
        scene.idle(10);
        scene.rotateCameraY(90);
        scene.idle(10);
        scene.world().showSection(kinetics, Direction.NORTH);
        scene.idle(10);
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(centrifuge, Direction.EAST))
            .attachKeyFrame();
        scene.world().propagatePipeChange(util.grid().at(2, 4, 3));
        scene.idle(120);
        scene.world().modifyBlockEntity(centrifuge, CentrifugeBlockEntity.class, be -> {
            be.getInputTank().drain(4000, FluidAction.EXECUTE);
            be.sendData();
        });
        scene.world().modifyBlockEntity(centrifuge, CentrifugeBlockEntity.class, be -> be.getDenseOutputTank().fill(BLUE_FLUID, FluidAction.EXECUTE));
        scene.world().propagatePipeChange(denseOutputPump);
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(denseOutputPump, Direction.EAST))
            .attachKeyFrame();
        scene.idle(120);
        scene.world().modifyBlockEntity(centrifuge, CentrifugeBlockEntity.class, be -> be.getLightOutputTank().fill(RED_FLUID, FluidAction.EXECUTE));
        scene.world().propagatePipeChange(lightOutputPump);
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(lightOutputPump, Direction.EAST));
        scene.idle(120);
        scene.markAsFinished();
    };

    public static void centrifugeMixture(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("centrifuge.mixture", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.world().showSection(util.select().everywhere(), Direction.DOWN);

        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(util.grid().at(3, 3, 2), Direction.WEST));
        scene.idle(120);
        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(util.grid().at(1, 1, 2), Direction.WEST));
        scene.idle(100);
        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(util.grid().at(3, 1, 2), Direction.WEST));
        scene.idle(100);

        scene.markAsFinished();
    };

    public static void bubbleCapGeneric(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("bubble_cap", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        //Define Selections
        Selection distillationTower = util.select().fromTo(2, 1, 1, 2, 3, 1);
        Selection kinetics = util.select().fromTo(2, 1, 2, 3, 3, 5).add(util.select().position(2, 0, 5));
        BlockPos blazeBurner = util.grid().at(1, 1, 1);
        BlockPos bottomBubbleCap = util.grid().at(2, 1, 1);
        BlockPos middleBubbleCap = util.grid().at(2, 2, 1);

        GasParticleData particleData = new GasParticleData(DestroyParticleTypes.DISTILLATION.get(), PURPLE_FLUID, 1.7f);

        // Pre-fill the input Tank
        scene.world().modifyBlockEntity(util.grid().at(2, 1, 3), FluidTankBlockEntity.class, be -> {
            be.getTankInventory().fill(PURPLE_FLUID, FluidAction.EXECUTE);
        });

        scene.world().showSection(util.select().fromTo(0, 0, 0, 4, 0, 4), Direction.UP);
        ElementLink<WorldSectionElement> distillationTowerElement = scene.world().showIndependentSection(distillationTower, Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(60)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(middleBubbleCap, Direction.WEST));
        scene.idle(80);
        scene.world().showSection(kinetics, Direction.NORTH);
        scene.world().propagatePipeChange(util.grid().at(2, 1, 2));
        scene.idle(100);
        scene.world().modifyBlockEntity(bottomBubbleCap, BubbleCapBlockEntity.class, be -> {
            be.getTank().drain(2000, FluidAction.EXECUTE);
        });
        scene.effects().emitParticles(VecHelper.getCenterOf(bottomBubbleCap), scene.effects().simpleParticleEmitter(particleData, new Vec3(0f, 0f, 0f)), 1.0f, 10);
        scene.world().modifyBlockEntity(util.grid().at(2, 2, 1), BubbleCapBlockEntity.class, be -> {
            be.getInternalTank().fill(BLUE_FLUID, FluidAction.EXECUTE);
        });
        scene.world().modifyBlockEntity(util.grid().at(2, 3, 1), BubbleCapBlockEntity.class, be -> {
            be.getInternalTank().fill(RED_FLUID, FluidAction.EXECUTE);
            be.setTicksToFill(BubbleCapBlockEntity.getTankCapacity() / BubbleCapBlockEntity.getTransferRate());
        });
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(middleBubbleCap, Direction.WEST));
        scene.idle(120);
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(util.grid().at(2, 3, 1), Direction.WEST))
            .attachKeyFrame();
        scene.idle(120);
        scene.world().hideSection(kinetics, Direction.SOUTH);
        scene.world().moveSection(distillationTowerElement, new Vec3(0, 1, 0), 20);
        scene.idle(10);
        scene.world().moveSection(scene.world().showIndependentSection(util.select().position(blazeBurner), Direction.EAST), new Vec3(1, 0, 0), 20);
        scene.idle(30);
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(bottomBubbleCap, Direction.WEST));
        scene.idle(120);
        scene.markAsFinished();
    };

    public static void bubbleCapMixtures(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("bubble_cap.mixtures", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();

        BlockPos reboiler = util.grid().at(1, 1, 1);
        Vec3 reboilerFront = util.vector().blockSurface(reboiler, Direction.NORTH);

        GasParticleData particleData = new GasParticleData(DestroyParticleTypes.DISTILLATION.get(), clearMixture(1000), 2.4f);

        scene.idle(10);
        scene.world().modifyBlockEntity(reboiler, BubbleCapBlockEntity.class, be -> {
            be.getTank().fill(clearMixture(1000), FluidAction.EXECUTE);
        });
        scene.idle(20);
        ElementLink<WorldSectionElement> tower = scene.world().showIndependentSection(util.select().fromTo(1, 1, 1, 1, 4, 1), Direction.DOWN);
        scene.idle(20);

        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .pointAt(reboilerFront)
            .attachKeyFrame();
        scene.idle(40);

        scene.world().modifyBlockEntity(reboiler, BubbleCapBlockEntity.class, be -> {
            be.getTank().drain(800, FluidAction.EXECUTE);
        });
        scene.effects().emitParticles(VecHelper.getCenterOf(reboiler), scene.effects().simpleParticleEmitter(particleData, new Vec3(0f, 0f, 0f)), 1.0f, 10);
        scene.world().modifyBlockEntity(util.grid().at(1, 2, 1), BubbleCapBlockEntity.class, be -> {
            be.getInternalTank().fill(clearMixture(400), FluidAction.EXECUTE);
        });
        scene.world().modifyBlockEntity(util.grid().at(1, 3, 1), BubbleCapBlockEntity.class, be -> {
            be.getInternalTank().fill(clearMixture(150), FluidAction.EXECUTE);
            be.setTicksToFill(BubbleCapBlockEntity.getTankCapacity() / BubbleCapBlockEntity.getTransferRate());
        });
        scene.world().modifyBlockEntity(util.grid().at(1, 4, 1), BubbleCapBlockEntity.class, be -> {
            be.getInternalTank().fill(clearMixture(250), FluidAction.EXECUTE);
            be.setTicksToFill(2 * BubbleCapBlockEntity.getTankCapacity() / BubbleCapBlockEntity.getTransferRate());
        });
        scene.idle(80);

        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .pointAt(reboilerFront)
            .attachKeyFrame();
        scene.overlay().showControls(reboilerFront, Pointing.RIGHT, 80)
            .withItem(AllItems.GOGGLES.asStack());
        scene.idle(100);

        scene.world().moveSection(tower, new Vec3(0, 1, 0), 10);
        scene.idle(20);
        ElementLink<WorldSectionElement> burner = scene.world().showIndependentSection(util.select().position(2, 1, 1), Direction.WEST);
        scene.world().moveSection(burner, new Vec3(-1, 0, 0), 0);
        scene.overlay().showText(60)
            .text("This text is defined in a language file.")
            .pointAt(reboilerFront);
        scene.idle(80);

        BlockPos topCap = util.grid().at(1, 5, 1);
        scene.overlay().showText(160)
            .text("This text is defined in a lamguage file.")
            .colored(PonderPalette.RED)
            .pointAt(util.vector().blockSurface(util.grid().at(1, 2, 1), Direction.WEST))
            .attachKeyFrame();
        scene.idle(80);
        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .pointAt(util.vector().blockSurface(topCap, Direction.WEST));
        scene.idle(100);
        
        scene.overlay().showText(60)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.GREEN)
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(util.grid().at(1, 2, 1), Direction.UP));
        scene.idle(80);

        scene.scaleSceneView(0.75f);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(1, 5, 1, 1, 7, 1), Direction.DOWN);
        scene.idle(10);
        scene.world().setBlock(util.grid().at(1, 4, 1), DestroyBlocks.BUBBLE_CAP
            .getDefaultState()
            .setValue(BubbleCapBlock.BOTTOM, false)
            .setValue(BubbleCapBlock.TOP, false)
        , false);
        scene.world().setBlock(util.grid().at(1, 6, 1), DestroyBlocks.BUBBLE_CAP
            .getDefaultState()
            .setValue(BubbleCapBlock.BOTTOM, false)
            .setValue(BubbleCapBlock.TOP, false)
        , false);
        scene.idle(20);

        scene.overlay().showText(60)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(util.grid().at(1, 6, 1), Direction.WEST));
        scene.idle(80);

        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        scene.idle(100);

        BlockPos displayLink = util.grid().at(0, 4, 1);

        scene.world().showSection(util.select().position(displayLink), Direction.EAST);
        scene.idle(10);
        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(displayLink, Direction.SOUTH))
            .attachKeyFrame();
        scene.idle(100);

        scene.markAsFinished();
    };

    private static ItemStack getFilledBlowpipe() {
        ItemStack filledPipe = DestroyBlocks.BLOWPIPE.asStack();
        FluidTank tank = new FluidTank(BlowpipeBlockEntity.TANK_CAPACITY);
        tank.fill(new FluidStack(DestroyFluids.MOLTEN_BOROSILICATE_GLASS.get(), 250), FluidAction.EXECUTE);
        filledPipe.getOrCreateTag().put("Tank", tank.writeToNBT(new CompoundTag()));
        filledPipe.getOrCreateTag().putString("Recipe", Destroy.asResource("glassblowing/round_bottomed_flask").toString());
        return filledPipe;
    };

    public static void blowpipe(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("blowpipe", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();

        Vec3 top = util.vector().topOf(2, 2, 1);
        BlockPos basin = util.grid().at(0, 1, 1);
        
        ElementLink<EntityElement> playerElement = scene.world().createEntity(w -> {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer localPlayer = minecraft.player;
            if (localPlayer == null) return null;
            PonderPlayer player = new PonderPlayer(w, localPlayer.getScoreboardName());
            Vec3 v = util.vector().topOf(2, 0, 1);
            player.setPos(v.x, v.y, v.z);
            player.xo = v.x;
            player.yo = v.y;
            player.zo = v.z;
            player.yBodyRot = player.yBodyRotO = player.yHeadRot = player.yHeadRotO = player.yRotO = 90;
            player.setYRot(90);
            player.setItemInHand(InteractionHand.MAIN_HAND, DestroyBlocks.BLOWPIPE.asStack());
            return player;
        });

        scene.idle(10);
        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .independent();
        scene.idle(20);
        scene.overlay().showControls(top, Pointing.DOWN, 60).rightClick().withItem(DestroyBlocks.BLOWPIPE.asStack());
        scene.idle(20);
        scene.addInstruction(new LivingEntitySwingInstruction(playerElement));
        scene.idle(60);

        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .independent();
        scene.idle(20);
        scene.world().showSection(util.select().position(basin), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showControls(top, Pointing.DOWN, 60).rightClick();
        scene.idle(20);
        scene.addInstruction(new LivingEntitySwingInstruction(playerElement, le -> le.setItemInHand(InteractionHand.MAIN_HAND, getFilledBlowpipe())));
        scene.idle(60);
        scene.world().hideSection(util.select().position(basin), Direction.UP);

        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .independent();
        for (int i = 0; i < BlowpipeBlockEntity.BLOWING_DURATION; i++) {
            int j = i;
            scene.world().modifyEntity(playerElement, e -> {
                if (!(e instanceof Player player)) return;
                CompoundTag tag = player.getItemInHand(InteractionHand.MAIN_HAND).getOrCreateTag();
                tag.putBoolean("Blowing", j < (int)(BlowpipeBlockEntity.BLOWING_TIME_PROPORTION * (float)BlowpipeBlockEntity.BLOWING_DURATION));
                int progress = tag.getInt("Progress");
                tag.putInt("LastProgress", progress);
                tag.putInt("Progress", progress + 1);
            });
            scene.idle(1);
        };
        scene.idle(20);

        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .independent();
        scene.overlay().showControls(top, Pointing.DOWN, 40).leftClick();
        scene.idle(20);
        scene.addInstruction(new LivingEntitySwingInstruction(playerElement, le -> {
            le.setItemInHand(InteractionHand.MAIN_HAND, DestroyBlocks.BLOWPIPE.asStack());
            le.setItemInHand(InteractionHand.OFF_HAND, DestroyBlocks.ROUND_BOTTOMED_FLASK.asStack());
        }));
        scene.idle(50);
        scene.overlay().showControls(top, Pointing.DOWN, 30).withItem(DestroyBlocks.ROUND_BOTTOMED_FLASK.asStack());
        scene.idle(50);

        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .independent();
        scene.idle(20);
        scene.overlay().showControls(top, Pointing.DOWN, 60).whileSneaking().rightClick().withItem(DestroyBlocks.BLOWPIPE.asStack());
        scene.world().modifyEntity(playerElement, e -> {
            if (!(e instanceof Player player)) return;
            player.setShiftKeyDown(true);
        });
        scene.idle(20);
        scene.addInstruction(new LivingEntitySwingInstruction(playerElement));
        scene.idle(60);

        scene.markAsFinished();
    };

    public static void blowpipeAutomation(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("blowpipe.automation", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos depot = util.grid().at(2, 1, 3);
        BlockPos spout = util.grid().at(2, 3, 3);
        ItemStack filledPipe = getFilledBlowpipe();
        BlockPos deployer = util.grid().at(3, 3, 2);
        Selection deployerS = util.select().position(deployer);
        BlockPos basin = util.grid().at(3, 1, 2);
        Selection gantryS = util.select().position(3, 4, 2);
        BlockPos deployerAfter = util.grid().at(1, 3, 2);
        Selection shaft = util.select().fromTo(1, 3, 3, 1, 3, 5);
        BlockPos pipe = util.grid().at(1, 1, 2);
        Selection pipeS = util.select().position(pipe);
        BlockPos fan = util.grid().at(1, 1, 4);

        scene.idle(10);
        scene.world().showSection(util.select().position(depot), Direction.DOWN);
        scene.world().showSection(util.select().position(spout), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(120)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(spout, Direction.WEST));
        scene.idle(20);
        scene.overlay().showControls(util.vector().blockSurface(depot, Direction.EAST), Pointing.RIGHT, 40).withItem(DestroyBlocks.BLOWPIPE.asStack());
        scene.world().createItemOnBeltLike(depot, Direction.EAST, DestroyBlocks.BLOWPIPE.asStack());
        scene.idle(20);
        scene.world().modifyBlockEntityNBT(util.select().position(spout), SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().removeItemsFromBelt(depot);
        scene.world().createItemOnBeltLike(depot, Direction.UP, filledPipe);
        scene.idle(20);
        //scene.overlay().showControls(util.vector().blockSurface(depot, Direction.EAST), Pointing.RIGHT).withItem(filledPipe), 40);
        scene.idle(60);
        scene.world().hideSection(util.select().position(depot), Direction.UP);
        scene.world().hideSection(util.select().position(spout), Direction.UP);
        scene.idle(20);

        scene.world().showSection(util.select().position(5, 0, 1), Direction.WEST);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(3, 1, 1, 4, 3, 1), Direction.SOUTH);
        scene.idle(10);
        scene.world().modifyBlockEntityNBT(deployerS, DeployerBlockEntity.class, nbt -> nbt.put("HeldItem", DestroyBlocks.BLOWPIPE.asStack().serializeNBT()));
        ElementLink<WorldSectionElement> deployerLink = scene.world().showIndependentSection(deployerS, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(basin), Direction.EAST);
        scene.idle(20);

        scene.overlay().showText(120)
            .text("This text is defined in a language file")
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(deployer, Direction.WEST));
        scene.idle(20);
        scene.overlay().showControls(util.vector().blockSurface(deployer, Direction.UP), Pointing.DOWN, 40).withItem(DestroyBlocks.BLOWPIPE.asStack());
        scene.idle(20);
        scene.world().moveDeployer(deployer, 1, 20);
        scene.idle(20);
        scene.world().modifyBlockEntityNBT(deployerS, DeployerBlockEntity.class, nbt -> nbt.put("HeldItem", filledPipe.serializeNBT()));
        scene.world().modifyBlockEntity(basin, BasinBlockEntity.class, be -> be.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(fh -> fh.drain(250, FluidAction.EXECUTE)));
        scene.idle(10);
        scene.world().moveDeployer(deployer, -1, 20);
        scene.idle(50);

        scene.world().showSection(util.select().fromTo(4, 4, 1, 4, 5, 2), Direction.WEST);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(1, 5, 2, 3, 5, 2), Direction.DOWN);
        ElementLink<WorldSectionElement> gantryLink = scene.world().showIndependentSection(util.select().position(3, 4, 2), Direction.DOWN);
        scene.idle(20);
        scene.world().multiplyKineticSpeed(deployerS, 1f / 256f / 256f);
        scene.world().setKineticSpeed(gantryS, 32f);
        scene.world().moveSection(deployerLink, util.vector().of(-2d, 0d, 0d), 40);
        scene.world().moveSection(gantryLink, util.vector().of(-2d, 0d, 0d), 40);
        scene.idle(40);
        scene.world().setKineticSpeed(gantryS, 0f);
        scene.idle(10);
        scene.world().hideSection(util.select().fromTo(3, 1, 1, 4, 5, 1), Direction.UP);
        scene.world().hideSection(util.select().fromTo(1, 5, 2, 3, 5, 2), Direction.UP);
        scene.world().hideIndependentSection(gantryLink, Direction.UP);
        scene.world().hideSection(util.select().position(basin), Direction.UP);
        scene.world().hideSection(util.select().position(5, 0, 1), Direction.UP);
        scene.idle(10);
        scene.world().showSection(util.select().position(2, 0, 5), Direction.NORTH);
        scene.idle(10);
        scene.world().showSection(util.select().position(1, 1, 5), Direction.NORTH);
        scene.idle(10);
        ElementLink<WorldSectionElement> cogLink = scene.world().showIndependentSection(util.select().position(0, 2, 5), Direction.NORTH);
        scene.idle(10);
        scene.world().showSection(shaft, Direction.DOWN);
        scene.idle(10);
        scene.world().multiplyKineticSpeed(deployerS, 256f * 256f);
        scene.idle(20);

        scene.overlay().showText(120)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(deployerAfter, Direction.WEST));
        scene.idle(20);
        scene.world().moveDeployer(deployer, 1, 20);
        scene.idle(20);
        scene.world().modifyBlockEntityNBT(util.select().position(deployer), DeployerBlockEntity.class, nbt -> nbt.put("HeldItem", ItemStack.EMPTY.serializeNBT()));
        ElementLink<WorldSectionElement> pipeLink = scene.world().showIndependentSectionImmediately(pipeS);
        scene.idle(10);
        scene.world().moveDeployer(deployer, -1, 20);
        scene.idle(40);
        //scene.effects().rotationSpeedIndicator(deployer, util.grid().at(1, 1, 2));
        //scene.effects().rotationSpeedIndicator(deployer, util.grid().at(1, 3, 1));
        scene.effects().rotationSpeedIndicator(deployer);
        scene.idle(70);

        scene.overlay().showText(220)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(deployerAfter, Direction.WEST));
        scene.addInstruction(new OutlineAABBInstruction(PonderPalette.RED, "pipe_end", new AABB(22 / 16f, 22 / 16f, 46 / 16f, 26 / 16f, 26 / 16f, 52 / 16f), 30));
        scene.idle(40);
        scene.world().modifyBlockEntityNBT(deployerS, DeployerBlockEntity.class, nbt -> nbt.putString("Mode", "PUNCH"));
        scene.idle(10);
        scene.world().moveDeployer(deployer, 1, 10);
        scene.idle(10);
        scene.world().modifyBlockEntityNBT(deployerS, DeployerBlockEntity.class, nbt -> nbt.put("HeldItem", filledPipe.serializeNBT()));
        scene.addInstruction(new FadeOutOfSceneInstruction<>(0, Direction.DOWN, pipeLink));
        scene.world().moveDeployer(deployer, -1, 10);
        scene.idle(20);
        scene.world().modifyBlockEntityNBT(deployerS, DeployerBlockEntity.class, nbt -> nbt.putString("Mode", "USE"));
        scene.idle(20);
        scene.world().hideIndependentSection(cogLink, Direction.SOUTH);
        scene.world().multiplyKineticSpeed(shaft, 1f / 256f / 256f);
        scene.world().multiplyKineticSpeed(deployerS,  1f / 256f / 256f);
        scene.idle(10);
        ElementLink<WorldSectionElement> belt = scene.world().showIndependentSection(util.select().fromTo(3, 1, 5, 3, 4, 6), Direction.NORTH);
        scene.world().moveSection(belt, util.vector().of(-2d, 0d, 0d), 0);
        scene.idle(10);
        scene.world().multiplyKineticSpeed(shaft, -256f * 256f);
        scene.world().multiplyKineticSpeed(deployerS, -256f * 256f);
        scene.idle(20);
        scene.world().moveDeployer(deployer, 1, 20);
        scene.idle(20);
        scene.world().modifyBlockEntityNBT(util.select().position(deployer), DeployerBlockEntity.class, nbt -> nbt.put("HeldItem", ItemStack.EMPTY.serializeNBT()));
        pipeLink = scene.world().showIndependentSectionImmediately(pipeS);
        scene.world().rotateSection(pipeLink, 0, 180d, 0, 0);
        scene.idle(10);
        scene.world().moveDeployer(deployer, -1, 20);
        scene.addInstruction(new OutlineAABBInstruction(PonderPalette.GREEN, "pipe_end", new AABB(22 / 16f, 22 / 16f, 28 / 16f, 26 / 16f, 26 / 16f, 34 / 16f), 60));
        scene.idle(80);

        scene.world().showSection(util.select().position(fan), Direction.EAST);
        scene.idle(10);
        scene.overlay().showText(120)
            .text("This text is defined in a language file")
            .pointAt(util.vector().blockSurface(fan, Direction.WEST))
            .attachKeyFrame();
        scene.idle(10);
        for (int i = 0; i <95; i++) {
            scene.world().modifyBlockEntity(pipe, BlowpipeBlockEntity.class, be -> {
                be.progressLastTick = be.progress;
                be.progress++;
            });
            scene.idle(1);
        };
        scene.world().createItemEntity(util.vector().blockSurface(pipe, Direction.NORTH), util.vector().of(0d, 0.1d, -0.1d), DestroyBlocks.ROUND_BOTTOMED_FLASK.asStack());
        scene.idle(20);

        scene.markAsFinished();
    };
    
    public static void cooler(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("cooler", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos center = util.grid().at(2, 0, 2);
        BlockPos cooler = util.grid().at(1, 2, 2);

        scene.idle(10);
        scene.world().createEntity(w -> {
			Stray strayEntity = EntityType.STRAY.create(w);
            if (strayEntity == null) return strayEntity; // This should never occur
			Vec3 v = util.vector().topOf(center);
			strayEntity.setPosRaw(v.x, v.y, v.z);
            strayEntity.xo = v.x;
            strayEntity.yo = v.y;
            strayEntity.zo = v.z;
			strayEntity.setYBodyRot(180);
            strayEntity.yBodyRotO = 180;
            strayEntity.setYHeadRot(180);
            strayEntity.yHeadRotO = 180;
			return strayEntity;
		});

        scene.idle(20);
		scene.overlay()
			.showControls(util.vector().centerOf(center.above(2)), Pointing.DOWN, 40).rightClick()
				.withItem(AllItems.EMPTY_BLAZE_BURNER.asStack());
		scene.idle(10);
		scene.overlay().showText(60)
			.text("This text is defined in a language file.")
			.attachKeyFrame()
			.pointAt(util.vector().blockSurface(center.above(2), Direction.WEST))
			.placeNearTarget();
		scene.idle(70);

        scene.world().modifyEntities(Stray.class, Entity::discard);
		scene.idle(20);

        scene.world().showSection(util.select().fromTo(1, 2, 2, 1, 3, 2), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(40)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(cooler, Direction.WEST));
        scene.idle(50);
        
        scene.world().showSection(util.select().position(5, 0, 2), Direction.WEST);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(2, 1, 3, 5, 1, 3), Direction.NORTH);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(1, 1, 2, 3, 1, 2).add(util.select().position(3, 2, 2)), Direction.SOUTH);
        scene.idle(20);

        Vec3 tankFace = util.vector().blockSurface(util.grid().at(3, 1, 2), Direction.NORTH);

        scene.world().propagatePipeChange(util.grid().at(2, 1, 2));
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(tankFace);
        scene.idle(120);
        scene.overlay().showText(80)
            .attachKeyFrame()
            .text("This text is defined in a language file.");
        scene.idle(10);
        scene.overlay().showText(190)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.BLUE)
            .pointAt(tankFace);
        scene.idle(90);
        scene.overlay().showText(80)  
            .text("This text is defined in a language file.");
        scene.idle(100);

        scene.world().showSection(util.select().fromTo(1, 1, 3, 1, 5, 3), Direction.NORTH);
        scene.idle(5);
        scene.world().showSection(util.select().position(1, 5, 2), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(cooler, Direction.WEST));
        scene.idle(100);

        scene.overlay().showControls(
            util.vector().topOf(cooler), Pointing.DOWN, 100)
                .withItem(AllItems.CREATIVE_BLAZE_CAKE.asStack());
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        scene.idle(120);

        scene.markAsFinished();
    };

    public static void extrusionDie(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("extrusion_die", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos extrusionDie = util.grid().at(1, 1, 2);

        scene.world().showSection(util.select().position(extrusionDie), Direction.DOWN);
        scene.idle(10);
        ElementLink<WorldSectionElement> contraption = scene.world().showIndependentSection(util.select().position(3, 2, 1), Direction.DOWN);
        scene.world().moveSection(contraption, new Vec3(0, 0, 1), 0);
        scene.world().showSectionAndMerge(util.select().fromTo(4, 2, 1, 5, 2, 1).add(util.select().fromTo(2, 2, 1, 2, 3, 1)), Direction.DOWN, contraption);
        scene.world().showSection(util.select().position(3, 2, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(3, 1, 3, 3, 3, 5).add(util.select().position(2, 0, 5)), Direction.NORTH);
        scene.idle(30);

        BlockPos quartz = util.grid().at(2, 1, 1);
        scene.world().showSectionAndMerge(util.select().position(quartz), Direction.SOUTH, contraption);
        scene.idle(10);
        scene.overlay().showControls(util.vector().blockSurface(quartz, Direction.SOUTH), Pointing.UP, 60)
            .withItem(new ItemStack(Blocks.QUARTZ_BLOCK));
        scene.idle(80);

        scene.overlay().showText(200)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(extrusionDie, Direction.SOUTH));
        scene.idle(20);

        Selection redstone1 = util.select().fromTo(2, 2, 1, 2, 3, 1);
        BlockPos sticker = util.grid().at(2, 2, 1);

        scene.world().toggleRedstonePower(redstone1);
        scene.world().modifyBlock(sticker, s -> s.setValue(StickerBlock.EXTENDED, true), false);
		scene.effects().indicateRedstone(util.grid().at(2, 3, 2));
		scene.world().modifyBlockEntityNBT(util.select().position(sticker), StickerBlockEntity.class, nbt -> {});
		scene.idle(20);
		scene.world().toggleRedstonePower(redstone1);
		scene.idle(20);

        scene.world().toggleRedstonePower(util.select().fromTo(3, 2, 4, 3, 3, 4));
        scene.effects().indicateRedstone(util.grid().at(3, 3, 4));
        scene.world().setKineticSpeed(util.select().fromTo(3, 2, 2, 3, 2, 3), 16f);
        scene.world().moveSection(contraption, new Vec3(-2, 0, 0), 60);
        scene.idle(45);
        scene.world().setBlock(quartz, Blocks.QUARTZ_PILLAR.defaultBlockState().setValue(BlockStateProperties.AXIS, Axis.X), false);
        scene.effects().emitParticles(util.vector().centerOf(0, 1, 2), scene.effects().particleEmitterWithinBlockSpace(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.QUARTZ_BLOCK.defaultBlockState()), Vec3.ZERO), 10f, 3);
        scene.idle(35);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(0, 1, 2), Direction.NORTH), Pointing.UP, 60)
            .withItem(new ItemStack(Blocks.QUARTZ_PILLAR));
        scene.idle(80);

        scene.overlay().showText(60)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .attachKeyFrame()
            .pointAt(util.vector().centerOf(extrusionDie));
        scene.idle(80);

        scene.markAsFinished();
    };

    public static void phytomining(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("phytomining", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();

        BlockPos ore = util.grid().at(1, 1, 1);
        BlockPos farmland = util.grid().at(1, 2, 1);

        scene.world().showSection(util.select().fromTo(1, 1, 1, 1, 3, 1), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(farmland, Direction.UP));
        scene.idle(120);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(1, 2, 1), Direction.UP), Pointing.DOWN, 30)
            .rightClick()
            .withItem(new ItemStack(DestroyItems.HYPERACCUMULATING_FERTILIZER.get()));
        scene.idle(60);
        scene.effects().emitParticles(util.vector().topOf(farmland).add(0, 0.25f, 0), scene.effects().particleEmitterWithinBlockSpace(ParticleTypes.HAPPY_VILLAGER, Vec3.ZERO), 1.0f, 15);
        scene.world().modifyBlock(ore, s -> Blocks.STONE.defaultBlockState(), false);
        scene.world().modifyBlock(util.grid().at(1, 3, 1), s -> DestroyBlocks.GOLDEN_CARROTS.get().defaultBlockState(), false);
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(ore, Direction.WEST))
            .attachKeyFrame();
        scene.idle(120);
        scene.markAsFinished();
    };

    public static void mechanicalSieve(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("mechanical_sieve", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos sieve = util.grid().at(2, 2, 2);

        scene.idle(10);
        scene.world().showSection(util.select().position(1, 0, 5), Direction.NORTH);
        scene.idle(10);
        scene.world().showSection(util.select().position(2, 1, 5), Direction.NORTH);
        for (int z = 5; z >= 2; z--) {
            scene.idle(5);
            scene.world().showSection(util.select().position(2, 2, z), Direction.DOWN);
        };
        scene.idle(10);

        scene.overlay().showText(160)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(sieve, Direction.WEST));
        scene.idle(20);
        ElementLink<EntityElement> ashes = scene.world().createItemEntity(util.vector().centerOf(2, 4, 2), Vec3.ZERO, DestroyItems.COPPER_INFUSED_BEETROOT_ASHES.asStack());
        scene.idle(20);
        scene.overlay().showControls(util.vector().blockSurface(sieve, Direction.UP), Pointing.DOWN, 40).withItem(DestroyItems.COPPER_INFUSED_BEETROOT_ASHES.asStack());
        scene.idle(60);
        scene.world().modifyEntity(ashes, Entity::kill);
        scene.world().createItemEntity(util.vector().topOf(2, 1, 2), Vec3.ZERO, AllItems.CRUSHED_COPPER.asStack());
        scene.world().createItemEntity(util.vector().topOf(2, 1, 2), Vec3.ZERO, DestroyItems.BEETROOT_ASHES.asStack());
        scene.idle(20);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 1, 2), Direction.NORTH), Pointing.RIGHT, 40).withItem(AllItems.CRUSHED_COPPER.asStack());
        scene.idle(60);

        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .independent()
            .attachKeyFrame()
            .colored(PonderPalette.GREEN);
        scene.idle(120);
        
        scene.markAsFinished();
    };
    
    public static void treeTap(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("tree_tap", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos tap = util.grid().at(3, 1, 2);

        scene.world().showSection(util.select().fromTo(4, 1, 2, 4, 3, 2), Direction.DOWN);
        scene.idle(20);
        scene.world().showSection(util.select().position(2, 0, 5), Direction.NORTH);
        for (int i = 5; i > 1; i--) {
            scene.idle(5);
            scene.world().showSection(util.select().position(3, 1, i), Direction.DOWN);
        };
        scene.idle(20);

        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().topOf(tap))
            .attachKeyFrame();
        BlockPos log = util.grid().at(4, 2, 2);
        for (int i = 0; i < 10; i++) {
            scene.world().incrementBlockBreakingProgress(log);
            scene.idle(10);
        };
        scene.world().destroyBlock(log);
        scene.world().modifyBlockEntity(tap, TreeTapBlockEntity.class, be -> {
            be.tank.getPrimaryHandler().setFluid(BlockTapping.latex);
        });
        scene.idle(20);

        scene.world().showSection(util.select().fromTo(2, 1, 1, 3, 1, 1), Direction.SOUTH);
        scene.idle(10);
        for (int i = 2; i >= 0; i--) {
            scene.world().showSection(util.select().position(i, 1, 2), Direction.DOWN);
            scene.idle(5);
        };
        scene.world().showSection(util.select().position(0, 1, 1), Direction.DOWN);
        scene.idle(20);

        BlockPos pipe = util.grid().at(2, 1, 2);
        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().topOf(pipe))
            .attachKeyFrame();
        scene.world().propagatePipeChange(pipe);
        scene.idle(100);


        scene.markAsFinished();
    };

    public static void siphon(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("siphon", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        
        BlockPos siphon = util.grid().at(2, 1, 2);
        scene.world().modifyBlockEntity(siphon, SiphonBlockEntity.class, be -> be.leftToDrain = 0);
        scene.idle(10);
        scene.world().showSection(util.select().position(siphon), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(2, 0, 5), Direction.NORTH);
        scene.idle(5);
        scene.world().showSection(util.select().position(1, 1, 5), Direction.NORTH);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(1, 1, 4, 2, 1, 4), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(1, 1, 3, 3, 1, 3), Direction.DOWN);
        scene.idle(10);
        BlockPos pump1 = util.grid().at(3, 1, 2);
        scene.world().showSection(util.select().position(pump1), Direction.DOWN);
        scene.idle(20);
        BlockPos tank1 = util.grid().at(4, 1, 2);
        scene.world().showSection(util.select().position(tank1), Direction.DOWN);
        scene.idle(20);
        scene.world().modifyBlockEntity(tank1, FluidTankBlockEntity.class, tank -> tank.getTankInventory().fill(new FluidStack(Fluids.WATER, 8000), FluidAction.EXECUTE));
        scene.world().propagatePipeChange(pump1);
        scene.idle(20);

        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().topOf(siphon));
        scene.idle(20);
        BlockPos pump2 = util.grid().at(1, 1, 2);
        scene.world().showSection(util.select().position(pump2), Direction.DOWN);
        scene.idle(10);
        BlockPos tank2 = util.grid().at(0, 1, 2);
        scene.world().showSection(util.select().position(tank2), Direction.DOWN);
        scene.idle(20);
        BlockPos lever = util.grid().at(2, 1, 1);
        scene.world().showSection(util.select().position(lever), Direction.DOWN);
        scene.idle(50);

        scene.overlay().showText(120)
            .independent(0)
            .text("This text is defined in a language file.");
        scene.idle(20);
        Selection redstone = util.select().fromTo(lever, siphon);
        scene.world().modifyBlocks(redstone, s -> s.cycle(BlockStateProperties.POWERED), false);
        scene.effects().indicateRedstone(lever);
        scene.world().modifyBlockEntity(siphon, SiphonBlockEntity.class, be -> be.leftToDrain = 1000);
        scene.idle(40);
        scene.overlay().showText(60)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(tank2, Direction.NORTH))
            .placeNearTarget()
            .colored(PonderPalette.MEDIUM);
        scene.idle(80);

        scene.overlay().showText(160)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(siphon, Direction.NORTH));
        scene.idle(20);
        scene.overlay().showControls(util.vector().topOf(siphon), Pointing.DOWN, 30).rightClick();
        scene.idle(50);
        scene.overlay().showText(30)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().topOf(siphon))
            .placeNearTarget();
        scene.idle(50);
        scene.world().modifyBlocks(redstone, s -> s.cycle(BlockStateProperties.POWERED), false);
        scene.effects().indicateRedstone(lever);
        scene.idle(20);
        scene.world().modifyBlocks(redstone, s -> s.cycle(BlockStateProperties.POWERED), false);
        scene.effects().indicateRedstone(lever);
        scene.world().modifyBlockEntity(siphon, SiphonBlockEntity.class, be -> be.leftToDrain = 200);
        scene.idle(40);

        scene.overlay().showText(160)
            .text("This text is defined in a language file.")
            .independent()
            .attachKeyFrame();
        scene.idle(40);
        for (int i = 0; i < 4; i++) {
            scene.world().modifyBlocks(redstone, s -> s.cycle(BlockStateProperties.POWERED), false);
            scene.effects().indicateRedstone(lever);
            if (i == 1) scene.world().modifyBlockEntity(siphon, SiphonBlockEntity.class, be -> be.leftToDrain = 400);
            scene.idle(10);
        };
        scene.idle(20);
        scene.overlay().showText(60)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(tank2, Direction.NORTH))
            .placeNearTarget()
            .colored(PonderPalette.MEDIUM);
        scene.idle(80);

        scene.markAsFinished();    
    };

    
};
