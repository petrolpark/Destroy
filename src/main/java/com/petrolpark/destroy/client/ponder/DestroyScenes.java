package com.petrolpark.destroy.client.ponder;

import java.util.List;

import com.petrolpark.destroy.block.AgingBarrelBlock;
import com.petrolpark.destroy.block.BubbleCapBlock;
import com.petrolpark.destroy.block.CoaxialGearBlock;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.DirectionalRotatedPillarKineticBlock;
import com.petrolpark.destroy.block.DoubleCardanShaftBlock;
import com.petrolpark.destroy.block.entity.AgingBarrelBlockEntity;
import com.petrolpark.destroy.block.entity.BubbleCapBlockEntity;
import com.petrolpark.destroy.block.entity.CentrifugeBlockEntity;
import com.petrolpark.destroy.block.entity.DynamoBlockEntity;
import com.petrolpark.destroy.block.entity.PumpjackBlockEntity;
import com.petrolpark.destroy.block.entity.VatSideBlockEntity;
import com.petrolpark.destroy.block.entity.VatSideBlockEntity.DisplayType;
import com.petrolpark.destroy.block.entity.behaviour.ChargingBehaviour;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.client.particle.DestroyParticleTypes;
import com.petrolpark.destroy.client.particle.data.GasParticleData;
import com.petrolpark.destroy.client.ponder.instruction.HighlightTagInstruction;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.fluid.MixtureFluid;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.world.village.DestroyVillagers;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.chassis.StickerBlock;
import com.simibubi.create.content.contraptions.chassis.StickerBlockEntity;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import com.simibubi.create.foundation.ponder.element.EntityElement;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import com.simibubi.create.foundation.ponder.instruction.EmitParticlesInstruction.Emitter;
import com.simibubi.create.foundation.utility.Pointing;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class DestroyScenes {

    private static final FluidStack purpleFluid, blueFluid, redFluid;

    // Define coloured Fluids
    static {
        purpleFluid = new FluidStack(PotionFluid.withEffects(1000, new Potion(), List.of(new MobEffectInstance(MobEffects.REGENERATION))), 1000);
        blueFluid = new FluidStack(PotionFluid.withEffects(500, new Potion(), List.of(new MobEffectInstance(MobEffects.NIGHT_VISION))), 1000);
        redFluid = new FluidStack(PotionFluid.withEffects(500, new Potion(), List.of(new MobEffectInstance(MobEffects.DAMAGE_BOOST))), 1000);
    };

    private static FluidStack clearMixture(int amount) {
        ReadOnlyMixture mixture = Mixture.pure(DestroyMolecules.WATER);
        return MixtureFluid.of(amount, mixture);
    };

    public static void agingBarrel(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("aging_barrel", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();

        BlockPos barrel = util.grid.at(1, 1, 1);

        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(10);
        scene.world.showSection(util.select.position(barrel), Direction.DOWN);
        scene.idle(10);

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(barrel, Direction.UP));
        scene.idle(100);

        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(barrel, Direction.UP), Pointing.DOWN)
            .rightClick()
            .withItem(new ItemStack(Items.WATER_BUCKET)),
            30
        );
        scene.world.modifyBlockEntity(barrel, AgingBarrelBlockEntity.class, be -> {
            be.getTank().fill(new FluidStack(Fluids.WATER, 1000), FluidAction.EXECUTE);
        });
        scene.idle(50);

        ItemStack yeast = DestroyItems.YEAST.asStack();
        scene.world.createItemEntity(util.vector.centerOf(barrel.above(2)), Vec3.ZERO, yeast);
        scene.idle(10);
        scene.world.modifyBlockEntity(barrel, AgingBarrelBlockEntity.class, be -> {
            be.inventory.insertItem(0, yeast, false);
        });
        scene.world.createItemEntity(util.vector.centerOf(barrel.above(2)), Vec3.ZERO, new ItemStack(Items.WHEAT));
        scene.idle(10);

        scene.world.setBlock(barrel, DestroyBlocks.AGING_BARREL.getDefaultState().setValue(AgingBarrelBlock.IS_OPEN, false), false);
        scene.world.modifyBlockEntity(barrel, AgingBarrelBlockEntity.class, be -> {
            be.inventory.clearContent();
            be.getTank().drain(1000, FluidAction.EXECUTE);
            be.getTank().fill(new FluidStack(DestroyFluids.UNDISTILLED_MOONSHINE.get(), 1000), FluidAction.EXECUTE);
        });
        scene.idle(20);

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(barrel, Direction.UP));

        for (int i = 1; i <= 4; i++) {
            BlockState state = DestroyBlocks.AGING_BARREL.getDefaultState()
                .setValue(AgingBarrelBlock.IS_OPEN, false)
                .setValue(AgingBarrelBlock.PROGRESS, i);
            scene.world.setBlock(barrel, state, false);
            scene.idle(20);
        };
        scene.idle(20);

        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(barrel, Direction.UP), Pointing.DOWN)
            .rightClick(),
            30
        );
        scene.idle(50);
        scene.world.setBlock(barrel, DestroyBlocks.AGING_BARREL.getDefaultState().setValue(AgingBarrelBlock.IS_OPEN, true), false);
        scene.idle(50);

        scene.world.createEntity(w -> {
			Villager villagerEntity = EntityType.VILLAGER.create(w);
            if (villagerEntity == null) return villagerEntity; // This should never occur
			Vec3 v = util.vector.topOf(util.grid.at(1, 0, 0));
            villagerEntity.setVillagerData(new VillagerData(VillagerType.PLAINS, DestroyVillagers.INNKEEPER.get(), 0));
			villagerEntity.setPos(v.x, v.y, v.z);
            villagerEntity.xo = v.x;
            villagerEntity.yo = v.y;
            villagerEntity.zo = v.z;
			return villagerEntity;
		});
        scene.idle(10);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.topOf(util.grid.at(1, 1, 0)))
            .attachKeyFrame();
        scene.idle(120); 

        scene.markAsFinished();
    };

    public static void centrifuge(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("centrifuge", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        // Define Selections
        Selection pipework = util.select.fromTo(2, 1, 1, 2, 5, 3);
        Selection kinetics = util.select.fromTo(1, 1, 2, 1, 3, 5)
            .add(util.select.fromTo(2, 1, 4, 2, 5, 4))
            .add(util.select.position(2, 0, 5));
        BlockPos centrifuge = util.grid.at(2, 3, 3);
        BlockPos denseOutputPump = util.grid.at(2, 3, 2);
        BlockPos lightOutputPump = util.grid.at(2, 2, 3);

        // Pre-fill the input Tank
        scene.world.modifyBlockEntity(util.grid.at(2, 5, 3), FluidTankBlockEntity.class, be -> {
            be.getTankInventory().fill(purpleFluid, FluidAction.EXECUTE);
        });
        // Ensure the Centrifuge faces the right way
        scene.world.modifyBlockEntity(centrifuge, CentrifugeBlockEntity.class, be -> {
            be.setPondering();
            be.attemptRotation(false);
        });

        scene.world.showSection(util.select.fromTo(0, 0, 0, 4, 0, 4), Direction.UP);
        scene.world.showSection(pipework, Direction.DOWN);
        scene.idle(10);
        scene.rotateCameraY(90);
        scene.idle(10);
        scene.world.showSection(kinetics, Direction.NORTH);
        scene.idle(10);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(centrifuge, Direction.EAST))
            .attachKeyFrame();
        scene.world.propagatePipeChange(util.grid.at(2, 4, 3));
        scene.idle(120);
        scene.world.modifyBlockEntity(centrifuge, CentrifugeBlockEntity.class, be -> {
            be.getInputTank().drain(4000, FluidAction.EXECUTE);
            be.sendData();
        });
        scene.world.modifyBlockEntity(centrifuge, CentrifugeBlockEntity.class, be -> be.getDenseOutputTank().fill(blueFluid, FluidAction.EXECUTE));
        scene.world.propagatePipeChange(denseOutputPump);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(denseOutputPump, Direction.EAST))
            .attachKeyFrame();
        scene.idle(120);
        scene.world.modifyBlockEntity(centrifuge, CentrifugeBlockEntity.class, be -> be.getLightOutputTank().fill(redFluid, FluidAction.EXECUTE));
        scene.world.propagatePipeChange(lightOutputPump);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(lightOutputPump, Direction.EAST));
        scene.idle(120);
        scene.markAsFinished();

        // TODO lubrication
    };

    public static void bubbleCapGeneric(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("bubble_cap_generic", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        //Define Selections
        Selection distillationTower = util.select.fromTo(2, 1, 1, 2, 3, 1);
        Selection kinetics = util.select.fromTo(2, 1, 2, 3, 3, 5).add(util.select.position(2, 0, 5));
        BlockPos blazeBurner = util.grid.at(1, 1, 1);
        BlockPos bottomBubbleCap = util.grid.at(2, 1, 1);
        BlockPos middleBubbleCap = util.grid.at(2, 2, 1);

        GasParticleData particleData = new GasParticleData(DestroyParticleTypes.DISTILLATION.get(), purpleFluid, 1.7f);

        // Pre-fill the input Tank
        scene.world.modifyBlockEntity(util.grid.at(2, 1, 3), FluidTankBlockEntity.class, be -> {
            be.getTankInventory().fill(purpleFluid, FluidAction.EXECUTE);
        });

        scene.world.showSection(util.select.fromTo(0, 0, 0, 4, 0, 4), Direction.UP);
        ElementLink<WorldSectionElement> distillationTowerElement = scene.world.showIndependentSection(distillationTower, Direction.DOWN);
        scene.idle(10);
        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(middleBubbleCap, Direction.WEST));
        scene.idle(80);
        scene.world.showSection(kinetics, Direction.NORTH);
        scene.world.propagatePipeChange(util.grid.at(2, 1, 2));
        scene.idle(100);
        scene.world.modifyBlockEntity(bottomBubbleCap, BubbleCapBlockEntity.class, be -> {
            be.getTank().drain(2000, FluidAction.EXECUTE);
        });
        scene.effects.emitParticles(VecHelper.getCenterOf(bottomBubbleCap), Emitter.simple(particleData, new Vec3(0f, 0f, 0f)), 1.0f, 10);
        scene.world.modifyBlockEntity(util.grid.at(2, 2, 1), BubbleCapBlockEntity.class, be -> {
            be.getInternalTank().fill(blueFluid, FluidAction.EXECUTE);
        });
        scene.world.modifyBlockEntity(util.grid.at(2, 3, 1), BubbleCapBlockEntity.class, be -> {
            be.getInternalTank().fill(redFluid, FluidAction.EXECUTE);
            be.setTicksToFill(BubbleCapBlockEntity.getTankCapacity() / BubbleCapBlockEntity.getTransferRate());
        });
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(middleBubbleCap, Direction.WEST));
        scene.idle(120);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(util.grid.at(2, 3, 1), Direction.WEST))
            .attachKeyFrame();
        scene.idle(120);
        scene.world.hideSection(kinetics, Direction.SOUTH);
        scene.world.moveSection(distillationTowerElement, new Vec3(0, 1, 0), 20);
        scene.idle(10);
        scene.world.moveSection(scene.world.showIndependentSection(util.select.position(blazeBurner), Direction.EAST), new Vec3(1, 0, 0), 20);
        scene.idle(30);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(bottomBubbleCap, Direction.WEST));
        scene.idle(120);
        scene.markAsFinished();
    };

    public static void bubbleCapMixtures(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("bubble_cap_mixtures", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();

        BlockPos reboiler = util.grid.at(1, 1, 1);
        Vec3 reboilerFront = util.vector.blockSurface(reboiler, Direction.NORTH);

        GasParticleData particleData = new GasParticleData(DestroyParticleTypes.DISTILLATION.get(), clearMixture(1000), 2.4f);

        scene.idle(10);
        scene.world.modifyBlockEntity(reboiler, BubbleCapBlockEntity.class, be -> {
            be.getTank().fill(clearMixture(1000), FluidAction.EXECUTE);
        });
        scene.idle(20);
        ElementLink<WorldSectionElement> tower = scene.world.showIndependentSection(util.select.fromTo(1, 1, 1, 1, 4, 1), Direction.DOWN);
        scene.idle(20);

        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(reboilerFront)
            .attachKeyFrame();
        scene.idle(40);

        scene.world.modifyBlockEntity(reboiler, BubbleCapBlockEntity.class, be -> {
            be.getTank().drain(800, FluidAction.EXECUTE);
        });
        scene.effects.emitParticles(VecHelper.getCenterOf(reboiler), Emitter.simple(particleData, new Vec3(0f, 0f, 0f)), 1.0f, 10);
        scene.world.modifyBlockEntity(util.grid.at(1, 2, 1), BubbleCapBlockEntity.class, be -> {
            be.getInternalTank().fill(clearMixture(400), FluidAction.EXECUTE);
        });
        scene.world.modifyBlockEntity(util.grid.at(1, 3, 1), BubbleCapBlockEntity.class, be -> {
            be.getInternalTank().fill(clearMixture(150), FluidAction.EXECUTE);
            be.setTicksToFill(BubbleCapBlockEntity.getTankCapacity() / BubbleCapBlockEntity.getTransferRate());
        });
        scene.world.modifyBlockEntity(util.grid.at(1, 4, 1), BubbleCapBlockEntity.class, be -> {
            be.getInternalTank().fill(clearMixture(250), FluidAction.EXECUTE);
            be.setTicksToFill(2 * BubbleCapBlockEntity.getTankCapacity() / BubbleCapBlockEntity.getTransferRate());
        });
        scene.idle(80);

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .pointAt(reboilerFront)
            .attachKeyFrame();
        scene.overlay.showControls(new InputWindowElement(reboilerFront, Pointing.RIGHT)
            .withItem(AllItems.GOGGLES.asStack())
        , 80);
        scene.idle(100);

        scene.world.moveSection(tower, new Vec3(0, 1, 0), 10);
        scene.idle(20);
        ElementLink<WorldSectionElement> burner = scene.world.showIndependentSection(util.select.position(2, 1, 1), Direction.WEST);
        scene.world.moveSection(burner, new Vec3(-1, 0, 0), 0);
        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .pointAt(reboilerFront);
        scene.idle(80);

        BlockPos topCap = util.grid.at(1, 5, 1);
        scene.overlay.showText(160)
            .text("This text is defined in a lamguage file.")
            .colored(PonderPalette.RED)
            .pointAt(util.vector.blockSurface(util.grid.at(1, 2, 1), Direction.WEST))
            .attachKeyFrame();
        scene.idle(80);
        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .pointAt(util.vector.blockSurface(topCap, Direction.WEST));
        scene.idle(100);
        
        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.GREEN)
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(util.grid.at(1, 2, 1), Direction.UP));
        scene.idle(80);

        scene.scaleSceneView(0.75f);
        scene.idle(10);
        scene.world.showSection(util.select.fromTo(1, 5, 1, 1, 7, 1), Direction.DOWN);
        scene.idle(10);
        scene.world.setBlock(util.grid.at(1, 4, 1), DestroyBlocks.BUBBLE_CAP
            .getDefaultState()
            .setValue(BubbleCapBlock.BOTTOM, false)
            .setValue(BubbleCapBlock.TOP, false)
            .setValue(BubbleCapBlock.PIPE_FACE, Direction.EAST)
        , false);
        scene.world.setBlock(util.grid.at(1, 6, 1), DestroyBlocks.BUBBLE_CAP
            .getDefaultState()
            .setValue(BubbleCapBlock.BOTTOM, false)
            .setValue(BubbleCapBlock.TOP, false)
            .setValue(BubbleCapBlock.PIPE_FACE, Direction.EAST)
        , false);
        scene.idle(20);

        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(util.grid.at(1, 6, 1), Direction.WEST));
        scene.idle(80);

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        scene.idle(100);

        BlockPos displayLink = util.grid.at(0, 4, 1);

        scene.world.showSection(util.select.position(displayLink), Direction.EAST);
        scene.idle(10);
        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(displayLink, Direction.SOUTH))
            .attachKeyFrame();
        scene.idle(100);

        scene.markAsFinished();
    };

    public static void coaxialGearShaftless(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("coaxial_gear_shaftless", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        scene.idle(5);
        scene.world.showSection(util.select.position(2, 0, 5), Direction.NORTH);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(3, 1, 2, 3, 1, 5), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(2, 1, 2), Direction.EAST);
        scene.idle(5);
        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(util.grid.at(2, 1, 2), Direction.WEST));
        scene.idle(80);

        scene.world.hideSection(util.select.position(3, 1, 4), Direction.EAST);
        scene.idle(15);
        ElementLink<WorldSectionElement> belt = scene.world.showIndependentSection(util.select.fromTo(3, 3, 4, 4, 3, 4), Direction.DOWN);
        scene.world.moveSection(belt, new Vec3(0d, -2d, 0d), 10);
        scene.idle(10);
        scene.world.showSection(util.select.fromTo(4, 1, 1, 4, 1, 4), Direction.SOUTH);
        scene.idle(5);

        int[][] cogs = new int[][]{new int[]{3, 1}, new int[]{2, 1}, new int[]{1, 1}, new int[]{1, 2}};
        for (int[] cog : cogs) {
            scene.idle(5);
            scene.world.showSection(util.select.position(cog[0], 1, cog[1]), Direction.EAST);
        };

        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(util.grid.at(1, 1, 2), Direction.UP));
        scene.idle(20);

        scene.effects.rotationDirectionIndicator(util.grid.at(1, 1, 1));
		scene.effects.rotationDirectionIndicator(util.grid.at(1, 1, 2));
        scene.idle(100);

        scene.markAsFinished();
    };

    public static void coaxialGearThrough(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("coaxial_gear_through", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        Selection verticalShaft1 = util.select.fromTo(3, 2, 2, 3, 3, 2);
        scene.world.setKineticSpeed(verticalShaft1, 0);
        scene.world.showSection(verticalShaft1, Direction.DOWN);
        scene.idle(30);

        BlockPos coaxialGear1 = util.grid.at(3, 2, 2);
        BlockPos longShaft1 = util.grid.at(3, 3, 2);

        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(coaxialGear1, Direction.UP));
        scene.idle(20);
        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(coaxialGear1, Direction.NORTH), Pointing.RIGHT)
            .withItem(DestroyBlocks.COAXIAL_GEAR.asStack())
        , 60);
        scene.idle(5);
        scene.world.setBlock(coaxialGear1, DestroyBlocks.COAXIAL_GEAR.getDefaultState().setValue(CoaxialGearBlock.HAS_SHAFT, true), false);
        scene.world.setBlock(longShaft1, DestroyBlocks.LONG_SHAFT.getDefaultState().setValue(RotatedPillarKineticBlock.AXIS, Axis.Y).setValue(DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION, false), false);
        scene.idle(55);

        scene.world.showSection(util.select.fromTo(1, 2, 2, 1, 3, 2), Direction.DOWN);
        scene.idle(20);

        BlockPos coaxialGear2 = util.grid.at(1, 2, 2);
        BlockPos longShaft2 = util.grid.at(1, 3, 2);

        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(coaxialGear2, Direction.UP));
        scene.idle(20);
        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(coaxialGear2, Direction.NORTH), Pointing.RIGHT)
            .withItem(AllBlocks.SHAFT.asStack())
        , 60);
        scene.idle(5);
        scene.world.setBlock(coaxialGear2, DestroyBlocks.COAXIAL_GEAR.getDefaultState().setValue(CoaxialGearBlock.HAS_SHAFT, true), false);
        scene.world.setBlock(longShaft2, DestroyBlocks.LONG_SHAFT.getDefaultState().setValue(RotatedPillarKineticBlock.AXIS, Axis.Y).setValue(DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION, false), false);
        scene.idle(65);

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .placeNearTarget()
            .attachKeyFrame()
            .colored(PonderPalette.RED);
        scene.idle(100);

        scene.world.showSection(util.select.fromTo(1, 0, 5, 3, 1, 5), Direction.NORTH);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(1, 1, 4, 3, 1, 4), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(1, 1, 3, 3, 1, 3), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(1, 1, 2, 3, 1, 2), Direction.SOUTH);
        scene.world.setKineticSpeed(util.select.position(longShaft1), -32);
        scene.world.setKineticSpeed(util.select.position(longShaft2), -32);
        scene.idle(10);

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(coaxialGear2, Direction.UP));
        scene.idle(100);

        BlockPos cogwheel = util.grid.at(2, 2, 2);

        scene.world.showSection(util.select.position(5, 0, 2), Direction.WEST);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(4, 1, 2, 4, 2, 2), Direction.DOWN);
        scene.world.setKineticSpeed(util.select.position(coaxialGear1), 8);
        scene.idle(5);
        scene.world.setKineticSpeed(util.select.position(cogwheel), -8);
        scene.world.showSection(util.select.position(cogwheel), Direction.DOWN);
        scene.world.setKineticSpeed(util.select.position(coaxialGear2), 8);
        scene.idle(25);

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(coaxialGear2, Direction.EAST));
        scene.idle(20);

        scene.effects.rotationDirectionIndicator(coaxialGear1);
		scene.effects.rotationDirectionIndicator(coaxialGear2);
        scene.effects.rotationDirectionIndicator(longShaft1);
		scene.effects.rotationDirectionIndicator(longShaft2);

        scene.idle(80);

        scene.markAsFinished();
    };

    public static void cooler(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("cooler", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos center = util.grid.at(2, 0, 2);
        BlockPos cooler = util.grid.at(1, 2, 2);

        scene.idle(10);
        scene.world.createEntity(w -> {
			Stray strayEntity = EntityType.STRAY.create(w);
            if (strayEntity == null) return strayEntity; // This should never occur
			Vec3 v = util.vector.topOf(center);
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
		scene.overlay
			.showControls(new InputWindowElement(util.vector.centerOf(center.above(2)), Pointing.DOWN).rightClick()
				.withItem(AllItems.EMPTY_BLAZE_BURNER.asStack()), 40);
		scene.idle(10);
		scene.overlay.showText(60)
			.text("This text is defined in a language file.")
			.attachKeyFrame()
			.pointAt(util.vector.blockSurface(center.above(2), Direction.WEST))
			.placeNearTarget();
		scene.idle(70);

        scene.world.modifyEntities(Stray.class, Entity::discard);
		scene.idle(20);

        scene.world.showSection(util.select.fromTo(1, 2, 2, 1, 3, 2), Direction.DOWN);
        scene.idle(10);

        scene.overlay.showText(40)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(cooler, Direction.WEST));
        scene.idle(50);
        
        scene.world.showSection(util.select.position(5, 0, 2), Direction.WEST);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(2, 1, 3, 5, 1, 3), Direction.NORTH);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(1, 1, 2, 3, 1, 2).add(util.select.position(3, 2, 2)), Direction.SOUTH);
        scene.idle(20);

        Vec3 tankFace = util.vector.blockSurface(util.grid.at(3, 1, 2), Direction.NORTH);

        scene.world.propagatePipeChange(util.grid.at(2, 1, 2));
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(tankFace);
        scene.idle(120);
        scene.overlay.showText(80)
            .attachKeyFrame()
            .text("This text is defined in a language file.");
        scene.idle(10);
        scene.overlay.showText(190)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.BLUE)
            .pointAt(tankFace);
        scene.idle(90);
        scene.overlay.showText(80)  
            .text("This text is defined in a language file.");
        scene.idle(100);

        scene.world.showSection(util.select.fromTo(1, 1, 3, 1, 5, 3), Direction.NORTH);
        scene.idle(5);
        scene.world.showSection(util.select.position(1, 5, 2), Direction.DOWN);
        scene.idle(15);
        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(cooler, Direction.WEST));
        scene.idle(100);

        scene.markAsFinished();
    };

    public static void differential(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("differential", "This text is defined in a language file.");
        scene.configureBasePlate(1, 0, 5);
        scene.showBasePlate();
        
        BlockPos westBigGear = util.grid.at(0, 0, 3);
        BlockPos eastBigGear = util.grid.at(6, 0, 1);
        BlockPos westBottomSmallGear = util.grid.at(0, 1, 2);
        BlockPos westTopSmallGear = util.grid.at(0, 2, 2);
        BlockPos eastBottomSmallGear = util.grid.at(6, 1, 2);
        BlockPos eastTopSmallGear = util.grid.at(6, 2, 2);
        BlockPos westOuterShaft = util.grid.at(1, 2, 2);
        BlockPos westInnerShaft = util.grid.at(2, 2, 2);
        BlockPos eastOuterShaft = util.grid.at(5, 2, 2);
        BlockPos eastInnerShaft = util.grid.at(4, 2, 2);
        BlockPos differential = util.grid.at(3, 2, 2);
        BlockPos westSpeedometer = util.grid.at(1, 1, 2);
        BlockPos eastSpeedometer = util.grid.at(5, 1, 2);
        BlockPos middleSmallGear = util.grid.at(3, 3, 3);
        BlockPos middleSpeedometer = util.grid.at(2, 3, 3);

        Selection west = util.select.position(westBottomSmallGear)
            .add(util.select.position(westTopSmallGear))
            .add(util.select.position(westOuterShaft))
            .add(util.select.position(westInnerShaft))
            .add(util.select.position(westSpeedometer));

        Selection east = util.select.position(eastBigGear)
            .add(util.select.position(eastBottomSmallGear))
            .add(util.select.position(eastTopSmallGear))
            .add(util.select.position(eastOuterShaft))
            .add(util.select.position(eastInnerShaft))
            .add(util.select.position(eastSpeedometer));

        Selection center = util.select.position(differential)
            .add(util.select.position(middleSmallGear))
            .add(util.select.position(middleSpeedometer));

        scene.idle(10);
        ElementLink<WorldSectionElement> bigGearElement = scene.world.showIndependentSection(util.select.position(westBigGear), Direction.EAST);
        scene.world.showSection(util.select.position(eastBigGear), Direction.WEST);
        scene.idle(5);
        scene.world.showSection(util.select.position(westBottomSmallGear), Direction.EAST);
        scene.world.showSection(util.select.position(eastBottomSmallGear), Direction.WEST);
        scene.idle(5);
        scene.world.showSection(util.select.position(westTopSmallGear), Direction.EAST);
        scene.world.showSection(util.select.position(eastTopSmallGear), Direction.WEST);
        scene.idle(5);
        scene.world.showSection(util.select.position(westOuterShaft), Direction.DOWN);
        scene.world.showSection(util.select.position(eastOuterShaft), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(westInnerShaft), Direction.DOWN);
        scene.world.showSection(util.select.position(eastInnerShaft), Direction.DOWN);
        scene.idle(5);
        ElementLink<WorldSectionElement> differentialElement = scene.world.showIndependentSection(util.select.position(differential), Direction.DOWN);
        scene.idle(10);

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.topOf(differential))
            .attachKeyFrame();
        scene.idle(20);
        scene.effects.rotationSpeedIndicator(westOuterShaft);
        scene.effects.rotationSpeedIndicator(eastOuterShaft);
        scene.idle(20);
        scene.effects.rotationSpeedIndicator(differential);
        scene.idle(60);

        scene.world.showSection(util.select.position(eastSpeedometer), Direction.EAST);
        scene.idle(5);
        scene.world.showSection(util.select.position(westSpeedometer), Direction.WEST);
        scene.idle(5);
        scene.world.showSection(util.select.position(middleSmallGear), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(middleSpeedometer), Direction.DOWN);
        scene.idle(10);

        scene.overlay.showText(120)
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        scene.idle(20);
        scene.overlay.showOutline(PonderPalette.BLUE, "east", util.select.position(eastSpeedometer), 100);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.BLUE)
            .independent(40);
        scene.idle(20);
        scene.overlay.showOutline(PonderPalette.RED, "west", util.select.position(westSpeedometer), 80);
        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .independent(60);
        scene.idle(20);
        scene.overlay.showOutline(PonderPalette.FAST, "total", util.select.position(middleSpeedometer), 60);
        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.FAST)
            .independent(80);
        scene.idle(80);

        scene.overlay.showText(170)
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        scene.idle(10);
        scene.world.multiplyKineticSpeed(center, 8 / 14f);
        scene.world.multiplyKineticSpeed(west, 1 / 1000f);
        scene.world.moveSection(bigGearElement, util.vector.of(-1d, 0d, 0d), 10);
        scene.idle(15);
        scene.world.rotateSection(bigGearElement, 0d, 0d, 180d, 10);
        scene.idle(15);
        scene.world.moveSection(bigGearElement, util.vector.of(1d, 0d, 0d), 10);
        scene.idle(10);
        scene.world.rotateSection(bigGearElement, 0d, 0d, 180d, 0);
        scene.world.setKineticSpeed(util.select.position(westBigGear), -3f);
        scene.world.multiplyKineticSpeed(center, 2 / 8f);
        scene.world.multiplyKineticSpeed(west, -1000f);
        scene.idle(20);
        scene.overlay.showOutline(PonderPalette.BLUE, "east", util.select.position(eastSpeedometer), 100);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.BLUE)
            .independent(40);
        scene.idle(20);
        scene.overlay.showOutline(PonderPalette.RED, "west", util.select.position(westSpeedometer), 80);
        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .independent(60);
        scene.idle(20);
        scene.overlay.showOutline(PonderPalette.FAST, "total", util.select.position(middleSpeedometer), 60);
        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.FAST)
            .independent(80);
        scene.idle(70);
        scene.world.hideSection(util.select.position(middleSmallGear).add(util.select.position(middleSpeedometer)), Direction.SOUTH);
        scene.idle(10);

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .attachKeyFrame();
        scene.idle(100);

        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.GREEN)
            .pointAt(util.vector.blockSurface(differential, Direction.WEST));
        scene.idle(80);
        scene.world.setKineticSpeed(util.select.position(differential), 0f);
        scene.world.hideSection(east, Direction.EAST);
        scene.world.hideSection(west, Direction.WEST);
        scene.world.hideIndependentSection(bigGearElement, Direction.WEST);
        scene.idle(10);
        scene.world.setKineticSpeed(east, 0f);
        scene.world.setKineticSpeed(west, 0f);
        scene.idle(10);
        scene.world.rotateSection(differentialElement, 0d, 90d, 0d, 10);
        scene.idle(10);
        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .pointAt(util.vector.blockSurface(differential, Direction.NORTH));
        scene.idle(80);

        scene.markAsFinished();
    };

    public static void doubleCardanShaft(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("double_cardan_shaft", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos dcs = util.grid.at(2, 1, 2);

        scene.world.showSection(util.select.position(1, 0, 5), Direction.NORTH);
        int[][] shafts = new int[][]{new int[]{2, 5}, new int[]{2, 4}, new int[]{2, 3}, new int[]{2, 2}, new int[]{3, 2}, new int[]{4, 2}};
        for (int[] shaft : shafts) {
            scene.idle(5);
            scene.world.showSection(util.select.position(shaft[0], 1, shaft[1]), Direction.DOWN);
        };

        scene.idle(10);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.centerOf(dcs))
            .attachKeyFrame();
        scene.idle(120);

        Selection secondShaft = util.select.fromTo(0, 1, 2, 1, 1, 2);
        scene.world.showSection(secondShaft, Direction.DOWN);
        scene.idle(20);

        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        scene.idle(20);
        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(dcs, Direction.NORTH), Pointing.RIGHT)
            .withItem(AllItems.WRENCH.asStack())
        , 20);
        scene.idle(5);
        scene.world.setBlock(dcs, DoubleCardanShaftBlock.getBlockstateConnectingDirections(Direction.SOUTH, Direction.UP), false);
        scene.world.setKineticSpeed(util.select.fromTo(3, 1, 2, 4, 1, 2), 0);
        scene.idle(25);
        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(dcs, Direction.NORTH), Pointing.RIGHT)
            .withItem(AllItems.WRENCH.asStack())
        , 20);
        scene.idle(5);
        scene.world.setBlock(dcs, DoubleCardanShaftBlock.getBlockstateConnectingDirections(Direction.SOUTH, Direction.WEST), false);
        scene.world.setKineticSpeed(secondShaft, 16);
        scene.idle(15);

        scene.markAsFinished();
    };  

    public static void dynamoRedstone(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("dynamo_redstone", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos dynamo = util.grid.at(2, 2, 3);
        BlockPos redStoneDust = util.grid.at(2, 1, 3);

        scene.world.showSection(util.select.fromTo(0, 1, 0, 4, 2, 5).add(util.select.position(2, 0, 5)), Direction.UP);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(dynamo, Direction.WEST));
        scene.idle(120);
        scene.world.setKineticSpeed(util.select.everywhere(), 256);
        scene.world.setKineticSpeed(util.select.position(2, 4, 2), -256); // Set the one cog which should be going the other way to the correct speed
        scene.effects.indicateRedstone(redStoneDust);
        scene.world.modifyBlock(redStoneDust, state -> state.setValue(BlockStateProperties.POWER, 7), false);
        scene.world.modifyBlockEntityNBT(util.select.position(2, 1, 1), NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 7));
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(dynamo, Direction.WEST))
            .attachKeyFrame();
        scene.idle(120);
        scene.markAsFinished();
    };

    public static void dynamoCharging(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("dynamo_charging", "This text is defined in a language file.");
		scene.configureBasePlate(0, 0, 5);
		scene.world.showSection(util.select.layer(0), Direction.UP);
		scene.idle(5);

        BlockPos dynamo = util.grid.at(2, 3, 2);
        BlockPos depot = util.grid.at(2, 1, 1);
        Selection kinetics = util.select.fromTo(2, 3, 3, 2, 3, 5).add(util.select.fromTo(2, 0, 5, 2, 2, 5));

		ElementLink<WorldSectionElement> depotElement = scene.world.showIndependentSection(util.select.position(depot), Direction.DOWN);
		scene.world.moveSection(depotElement, util.vector.of(0, 0, 1), 0);
		scene.idle(10);

        scene.world.showSection(util.select.position(dynamo), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(kinetics, Direction.NORTH);
        scene.idle(10);

        Vec3 dynamoSide = util.vector.blockSurface(dynamo, Direction.WEST);
		scene.overlay.showText(60)
			.pointAt(dynamoSide)
			.placeNearTarget()
			.attachKeyFrame()
			.text("This text is defined in a language file.");
		scene.idle(70);
		scene.overlay.showText(60)
			.pointAt(dynamoSide.subtract(0, 2, 0))
			.placeNearTarget()
			.text("This text is defined in a language file.");
		scene.idle(50);
		ItemStack cell = DestroyItems.DISCHARGED_VOLTAIC_PILE.asStack();
		scene.world.createItemOnBeltLike(depot, Direction.NORTH, cell);
		Vec3 depotCenter = util.vector.centerOf(depot.south());
		scene.overlay.showControls(new InputWindowElement(depotCenter, Pointing.UP).withItem(cell), 30);
		scene.idle(10);

        scene.world.modifyBlockEntity(dynamo, DynamoBlockEntity.class, be -> 
            be.chargingBehaviour.start(ChargingBehaviour.Mode.BELT, util.vector.blockSurface(depot, Direction.UP))
        );
        scene.idle(60);
        //TODO make dynamo actually render in ponder

        scene.world.modifyBlockEntity(dynamo, DynamoBlockEntity.class, be -> 
            be.chargingBehaviour.running = false
        );
        ItemStack chargedCell = DestroyItems.VOLTAIC_PILE.asStack();
        scene.world.removeItemsFromBelt(depot);
		scene.world.createItemOnBeltLike(depot, Direction.UP, chargedCell);
		scene.idle(10);
		scene.overlay.showControls(new InputWindowElement(depotCenter, Pointing.UP).withItem(chargedCell), 50);
		scene.idle(60);

        scene.markAsFinished();
    };

    public static void dynamoElectrolysis(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("dynamo_electrolysis", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        //TODO
        scene.markAsFinished();
    };

    public static void extrusionDie(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("extrusion_die", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos extrusionDie = util.grid.at(1, 1, 2);

        scene.world.showSection(util.select.position(extrusionDie), Direction.DOWN);
        scene.idle(10);
        ElementLink<WorldSectionElement> contraption = scene.world.showIndependentSection(util.select.position(3, 2, 1), Direction.DOWN);
        scene.world.moveSection(contraption, new Vec3(0, 0, 1), 0);
        scene.world.showSectionAndMerge(util.select.fromTo(4, 2, 1, 5, 2, 1).add(util.select.fromTo(2, 2, 1, 2, 3, 1)), Direction.DOWN, contraption);
        scene.world.showSection(util.select.position(3, 2, 2), Direction.DOWN);
        scene.idle(10);
        scene.world.showSection(util.select.fromTo(3, 1, 3, 3, 3, 5).add(util.select.position(2, 0, 5)), Direction.NORTH);
        scene.idle(30);

        BlockPos quartz = util.grid.at(2, 1, 1);
        scene.world.showSectionAndMerge(util.select.position(quartz), Direction.SOUTH, contraption);
        scene.idle(10);
        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(quartz, Direction.SOUTH), Pointing.UP)
            .withItem(new ItemStack(Blocks.QUARTZ_BLOCK))
        , 60);
        scene.idle(80);

        scene.overlay.showText(200)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(extrusionDie, Direction.SOUTH));
        scene.idle(20);

        Selection redstone1 = util.select.fromTo(2, 2, 1, 2, 3, 1);
        BlockPos sticker = util.grid.at(2, 2, 1);

        scene.world.toggleRedstonePower(redstone1);
        scene.world.modifyBlock(sticker, s -> s.setValue(StickerBlock.EXTENDED, true), false);
		scene.effects.indicateRedstone(util.grid.at(2, 3, 2));
		scene.world.modifyBlockEntityNBT(util.select.position(sticker), StickerBlockEntity.class, nbt -> {});
		scene.idle(20);
		scene.world.toggleRedstonePower(redstone1);
		scene.idle(20);

        scene.world.toggleRedstonePower(util.select.fromTo(3, 2, 4, 3, 3, 4));
        scene.effects.indicateRedstone(util.grid.at(3, 3, 4));
        scene.world.setKineticSpeed(util.select.fromTo(3, 2, 2, 3, 2, 3), 16f);
        scene.world.moveSection(contraption, new Vec3(-2, 0, 0), 60);
        scene.idle(45);
        scene.world.setBlock(quartz, Blocks.QUARTZ_PILLAR.defaultBlockState().setValue(BlockStateProperties.AXIS, Axis.X), false);
        scene.effects.emitParticles(util.vector.centerOf(0, 1, 2), Emitter.withinBlockSpace(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.QUARTZ_BLOCK.defaultBlockState()), Vec3.ZERO), 10f, 3);
        scene.idle(35);
        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(util.grid.at(0, 1, 2), Direction.NORTH), Pointing.UP)
            .withItem(new ItemStack(Blocks.QUARTZ_PILLAR))
        , 60);
        scene.idle(80);

        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .attachKeyFrame()
            .pointAt(util.vector.centerOf(extrusionDie));
        scene.idle(80);

        scene.markAsFinished();
    };

    public static void planetaryGearset(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("planetary_gearset", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();

        scene.world.showSection(util.select.position(1, 0, 3), Direction.NORTH);
        scene.idle(5);
        scene.world.showSection(util.select.position(1, 1, 2), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(1, 2, 0, 1, 2, 1), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(2, 3, 1), Direction.DOWN);
        scene.idle(5);

        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(util.grid.at(1, 2, 1), Direction.WEST));
        scene.idle(120);

        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(util.grid.at(1, 2, 0), Direction.NORTH));
        scene.idle(20);

        scene.effects.rotationDirectionIndicator(util.grid.at(1, 2, 1));
		scene.effects.rotationDirectionIndicator(util.grid.at(1, 2, 0));
        scene.idle(100);

        scene.markAsFinished();
    };

    public static void pumpjack(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("pumpjack", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        Selection pumpjack = util.select.fromTo(1, 1, 3, 3, 2, 3);
        Selection kinetics = util.select.position(3, 1, 4).add(util.select.fromTo(2, 0, 5, 3, 1, 5));
        Selection pipes = util.select.fromTo(2, 1, 1, 3, 1, 2);
        BlockPos pumpjackPos = util.grid.at(2, 1, 3);
        BlockPos pumpPos = util.grid.at(2, 1, 2);

        // Add player
        ElementLink<EntityElement> playerElement = scene.world.createEntity(w -> {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer localPlayer = minecraft.player;
            if (localPlayer == null) return null;
            PonderPlayer player = new PonderPlayer(w, localPlayer.getScoreboardName());
            Vec3 v = util.vector.topOf(2, 0, 0);
            player.setPos(v.x, v.y, v.z);
            player.xo = v.x;
            player.yo = v.y;
            player.zo = v.z;
            player.setInvisible(true);
            return player;
        });

        // Add TNT
        scene.world.createEntity(w -> {
            PrimedTnt tnt = new PrimedTnt(EntityType.TNT, w);
            Vec3 v = util.vector.topOf(2, 0, 2);
            tnt.setPos(v.x, v.y, v.z);
            tnt.xo = v.x;
            tnt.yo = v.y;
            tnt.zo = v.z;
            tnt.setFuse(80);
            return tnt;
        });

        // Set and then un-set the Player invisible so it it doesn't awkwardly jerk when added to the scene
        scene.world.modifyEntity(playerElement, entity -> {
            if (!(entity instanceof PonderPlayer player)) return;
            player.setItemInHand(InteractionHand.MAIN_HAND, DestroyItems.SEISMOMETER.asStack());
            player.setInvisible(false);
        });

        scene.overlay.showText(60)
            .text("This text is defined in a language file.");
        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(util.grid.at(2, 2, 0), Direction.UP), Pointing.DOWN)
            .withItem(DestroyItems.SEISMOMETER.asStack()),
            60
        );
        scene.idle(80);

        scene.effects.emitParticles(VecHelper.getCenterOf(pumpPos), Emitter.withinBlockSpace(ParticleTypes.EXPLOSION_EMITTER, Vec3.ZERO), 1f, 1);
        scene.world.modifyEntity(playerElement, Entity::discard);
        scene.idle(20);

        scene.world.showSection(kinetics, Direction.NORTH);
        scene.idle(10);
        scene.world.showSection(pumpjack, Direction.DOWN);
        scene.idle(10);

        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(pumpjackPos, Direction.NORTH))
            .attachKeyFrame();
        scene.idle(120);
        
        scene.world.showSection(pipes, Direction.SOUTH);
        scene.idle(10);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(pumpPos, Direction.UP))
            .attachKeyFrame();
        scene.world.modifyBlockEntity(pumpjackPos, PumpjackBlockEntity.class, be -> {
            be.tank.allowInsertion();
            be.tank.getPrimaryHandler().fill(new FluidStack(DestroyFluids.CRUDE_OIL.get(), 1000), FluidAction.EXECUTE);
            be.tank.forbidInsertion();
        });
        scene.idle(20);
        scene.world.propagatePipeChange(pumpPos);
        scene.idle(100);

        scene.markAsFinished();
    };

    public static void phytomining(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("phytomining", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();

        BlockPos ore = util.grid.at(1, 1, 1);
        BlockPos farmland = util.grid.at(1, 2, 1);

        scene.world.showSection(util.select.fromTo(1, 1, 1, 1, 3, 1), Direction.DOWN);
        scene.idle(10);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(farmland, Direction.UP));
        scene.idle(120);
        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(util.grid.at(1, 2, 1), Direction.UP), Pointing.DOWN)
            .rightClick()
            .withItem(new ItemStack(DestroyItems.HYPERACCUMULATING_FERTILIZER.get())),
            30
        );
        scene.idle(60);
        scene.effects.emitParticles(util.vector.topOf(farmland).add(0, 0.25f, 0), Emitter.withinBlockSpace(ParticleTypes.HAPPY_VILLAGER, Vec3.ZERO), 1.0f, 15);
        scene.world.modifyBlock(ore, s -> Blocks.STONE.defaultBlockState(), false);
        scene.world.modifyBlock(util.grid.at(1, 3, 1), s -> DestroyBlocks.GOLDEN_CARROTS.get().defaultBlockState(), false);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(ore, Direction.WEST))
            .attachKeyFrame();
        scene.idle(120);
        scene.markAsFinished();
    };

    public static void vatConstruction(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("vat_construction", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 9);
        scene.scaleSceneView(.5f);
        scene.showBasePlate();

        // Vat 1
        BlockPos vat1ControllerPos = util.grid.at(4, 2, 2);
        Selection vat1Controller = util.select.position(vat1ControllerPos);
        Selection vat1 = util.select.fromTo(2, 1, 2, 6, 5, 6);
        Selection vat1Floor = util.select.fromTo(2, 1, 2, 6, 1, 6);
        Selection vat1South = util.select.fromTo(2, 2, 6, 6, 4, 6);
        Selection vat1North = util.select.fromTo(2, 2, 2, 6, 4, 2).substract(vat1Controller);
        Selection vat1East = util.select.fromTo(6, 2, 3, 6, 4, 5);
        Selection vat1West = util.select.fromTo(2, 2, 3, 2, 4, 5);
        BlockPos vat1WestCenter = util.grid.at(2, 3, 4);
        Selection vat1Roof = util.select.fromTo(2, 5, 2, 6, 5, 6);
        Selection vat1CopperWall = util.select.fromTo(1, 2, 3, 1, 4, 5);
        Selection vat1IronWall = util.select.fromTo(0, 2, 3, 0, 4, 5);

        // Vat 2
        Selection vat2 = util.select.fromTo(3, 6, 3, 5, 8, 5);

        // Vat 3
        Selection vat3 = util.select.fromTo(1, 9, 1, 7, 15, 7);

        // Vat 4
        Selection vat4 = util.select.fromTo(3, 17, 1, 5, 21, 7);
        Selection vat4West = util.select.fromTo(3, 17, 1, 3, 21, 7).substract(util.select.fromTo(3, 18, 2, 3, 20, 6));
        Selection vat4East = util.select.fromTo(5, 17, 1, 5, 21, 7).substract(util.select.fromTo(5, 18, 2, 5, 20, 6));
        Selection vat4North = util.select.position(4, 17, 1).add(util.select.position(4, 21, 1));
        Selection vat4South = util.select.position(4, 17, 7).add(util.select.position(4, 21, 7));
        Selection vat4Remainder = vat4.copy().substract(vat4West).substract(vat4East).substract(vat4North).substract(vat4South);

        scene.idle(10);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        scene.idle(20);
        scene.world.showSection(vat1Floor, Direction.DOWN);
        scene.idle(10);
        scene.world.showSection(vat1South, Direction.NORTH);
        scene.idle(10);
        scene.world.showSection(vat1East, Direction.WEST);
        scene.idle(10);
        scene.world.showSection(vat1West, Direction.EAST);
        scene.idle(10);
        scene.world.showSection(vat1North, Direction.SOUTH);
        scene.idle(10);
        scene.world.showSection(vat1Roof, Direction.DOWN);
        scene.idle(50);

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(vat1ControllerPos, Direction.WEST));
        scene.overlay.showOutline(PonderPalette.RED, "missing_space", vat1Controller, 40);
        scene.idle(50);
        scene.world.showSection(vat1Controller, Direction.SOUTH);
        scene.idle(5);
        scene.overlay.showOutline(PonderPalette.GREEN, "missing_space", vat1Controller, 40);
        scene.idle(60);

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .pointAt(util.vector.blockSurface(vat1WestCenter, Direction.WEST))
            .attachKeyFrame();
        scene.idle(100);

        scene.overlay.showText(80)
            .text("This text is defined in a language file");
        scene.addInstruction(new HighlightTagInstruction(DestroyPonderTags.VAT_SIDE_BLOCKS, 80));
        scene.idle(100);

        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        scene.world.hideSection(vat1West, Direction.WEST);
        scene.idle(20);
        ElementLink<WorldSectionElement> copperWallLink = scene.world.showIndependentSection(vat1CopperWall, Direction.EAST);
        scene.world.moveSection(copperWallLink, new Vec3(1, 0, 0), 0);
        scene.idle(40);
        scene.world.hideIndependentSection(copperWallLink, Direction.WEST);
        scene.idle(20);
        ElementLink<WorldSectionElement> ironWallLink = scene.world.showIndependentSection(vat1IronWall, Direction.EAST);
        scene.world.moveSection(ironWallLink, new Vec3(2, 0, 0), 0);
        scene.idle(60);

        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        scene.idle(20);
        scene.world.hideSection(vat1.substract(vat1West), Direction.UP);
        scene.world.hideIndependentSection(ironWallLink, Direction.UP);
        scene.idle(20);
        ElementLink<WorldSectionElement> vat2Link = scene.world.showIndependentSection(vat2, Direction.DOWN);
        scene.world.moveSection(vat2Link, new Vec3(0, -5, 0), 0);
        scene.idle(60);
        scene.world.hideIndependentSection(vat2Link, Direction.UP);
        scene.idle(20);
        scene.overlay.showText(40)
            .text("This text is defined in a language file.");
        ElementLink<WorldSectionElement> vat3Link = scene.world.showIndependentSection(vat3, Direction.DOWN);
        scene.world.moveSection(vat3Link, new Vec3(0, -8, 0), 0);
        scene.idle(60);

        scene.world.hideIndependentSection(vat3Link, Direction.UP);
        scene.overlay.showText(60)
            .text("This text is defined in a language file.");
        scene.idle(20);
        ElementLink<WorldSectionElement> vat4NorthLink = scene.world.showIndependentSection(vat4North, Direction.DOWN);
        ElementLink<WorldSectionElement> vat4EastLink = scene.world.showIndependentSection(vat4East, Direction.DOWN);
        ElementLink<WorldSectionElement> vat4SouthLink = scene.world.showIndependentSection(vat4South, Direction.DOWN);
        ElementLink<WorldSectionElement> vat4WestLink = scene.world.showIndependentSection(vat4West, Direction.DOWN);
        ElementLink<WorldSectionElement> vat4RemainderLink = scene.world.showIndependentSection(vat4Remainder, Direction.DOWN);
        scene.world.moveSection(vat4NorthLink, new Vec3(0, -16, 0), 0);
        scene.world.moveSection(vat4EastLink, new Vec3(0, -16, 0), 0);
        scene.world.moveSection(vat4SouthLink, new Vec3(0, -16, 0), 0);
        scene.world.moveSection(vat4WestLink, new Vec3(0, -16, 0), 0);
        scene.world.moveSection(vat4RemainderLink, new Vec3(0, -16, 0), 0);
        scene.idle(60);
        scene.overlay.showText(80)
            .pointAt(util.vector.topOf(util.grid.at(4, 5, 4)))
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        scene.idle(100);

        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.topOf(util.grid.at(3, 5, 4)))
            .attachKeyFrame();
        scene.idle(20);
        scene.world.moveSection(vat4NorthLink, new Vec3(0, 0, -2), 10);
        scene.world.moveSection(vat4EastLink, new Vec3(2, 0, 0), 10);
        scene.world.moveSection(vat4SouthLink, new Vec3(0, 0, 2), 10);
        scene.world.moveSection(vat4WestLink, new Vec3(-2, 0, 0), 10);
        scene.idle(60);
        scene.world.moveSection(vat4NorthLink, new Vec3(0, 0, 2), 10);
        scene.world.moveSection(vat4EastLink, new Vec3(-2, 0, 0), 10);
        scene.world.moveSection(vat4SouthLink, new Vec3(0, 0, -2), 10);
        scene.world.moveSection(vat4WestLink, new Vec3(2, 0, 0), 10);
        scene.idle(20);

        scene.markAsFinished();
    };

    public static void vatInteraction(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("vat_interaction", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 6);
        scene.scaleSceneView(0.8f);
        scene.showBasePlate();

        Selection vat = util.select.fromTo(1, 1, 1, 4, 4, 4);
        BlockPos dialBlock = util.grid.at(3, 3, 1);
        BlockPos pipeBlock = util.grid.at(1, 2, 3);
        BlockPos pipe = util.grid.at(0, 2, 3);
        BlockState pipeState = AllBlocks.FLUID_PIPE.getDefaultState()
            .setValue(FluidPipeBlock.DOWN, false)
            .setValue(FluidPipeBlock.UP, false)
            .setValue(FluidPipeBlock.NORTH, false)
            .setValue(FluidPipeBlock.SOUTH, false)
            .setValue(FluidPipeBlock.EAST, true)
            .setValue(FluidPipeBlock.WEST, true);
        BlockPos bottomFunnel = util.grid.at(0, 2, 2);
        BlockPos vent = util.grid.at(3, 4, 2);
        BlockPos lever = util.grid.at(3, 4, 0);
        Selection everything = util.select.fromTo(0, 1, 0, 4, 5, 4);

        scene.idle(10);
        scene.world.showSection(util.select.fromTo(1, 1, 1, 4, 4, 4), Direction.DOWN);
        scene.idle(10);
        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        scene.idle(20);
        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(dialBlock, Direction.NORTH), Pointing.RIGHT)
            .withWrench(),
            20
        );
        scene.idle(5);
        scene.world.modifyBlockEntity(dialBlock, VatSideBlockEntity.class, vatSide -> vatSide.setDisplayType(DisplayType.THERMOMETER));
        scene.idle(75);

        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .attachKeyFrame();
        scene.overlay.showOutline(PonderPalette.RED, "vat_outside", vat.substract(util.select.fromTo(2, 2, 1, 3, 3, 4)).substract(util.select.fromTo(2, 1, 2, 3, 4, 3)).substract(util.select.fromTo(1, 2, 2, 4, 3, 3)), 60);
        scene.idle(80);

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        scene.idle(20);
        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(dialBlock, Direction.NORTH), Pointing.RIGHT)
            .withWrench(),
            20
        );
        scene.idle(5);
        scene.world.modifyBlockEntity(dialBlock, VatSideBlockEntity.class, vatSide -> vatSide.setDisplayType(DisplayType.BAROMETER));
        scene.idle(75);

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        scene.idle(20);
        scene.world.setBlock(pipe, pipeState, false);
        scene.world.showSection(util.select.position(pipe), Direction.EAST);
        scene.idle(12);
        scene.world.modifyBlockEntity(pipeBlock, VatSideBlockEntity.class, vatSide -> vatSide.setDisplayType(DisplayType.PIPE));
        scene.idle(68);

        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .pointAt(util.vector.blockSurface(pipeBlock, Direction.WEST));
        scene.idle(120);

        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        scene.idle(10);
        scene.world.showSection(util.select.position(2, 5, 3), Direction.DOWN);
        scene.idle(20);
        ElementLink<EntityElement> itemEntity = scene.world.createItemEntity(util.vector.centerOf(util.grid.at(2, 9, 3)), util.vector.of(0f, -0.4f, 0f), DestroyItems.PLATINUM_INGOT.asStack());
        scene.idle(8);
        scene.world.modifyEntity(itemEntity, Entity::discard);
        scene.idle(22);
        scene.world.showSection(util.select.position(bottomFunnel), Direction.EAST);
        scene.idle(20);
        scene.world.flapFunnel(bottomFunnel, true);
        scene.world.createItemEntity(util.vector.centerOf(bottomFunnel).add(0.15f, -0.45f, 0), Vec3.ZERO, DestroyItems.PLATINUM_INGOT.asStack());
        scene.idle(40);

        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        scene.idle(20);
        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(vent, Direction.UP), Pointing.DOWN)
            .withWrench(),
            20
        );
        scene.idle(5);
        scene.world.modifyBlockEntity(vent, VatSideBlockEntity.class, vatSide -> vatSide.setDisplayType(DisplayType.OPEN_VENT));
        scene.idle(95);

        scene.overlay.showText(80)
            .text("This text is defined in a language file.");
        scene.idle(10);
        scene.world.showSection(util.select.position(lever), Direction.SOUTH);
        scene.idle(20);
        scene.world.toggleRedstonePower(util.select.position(lever));
        scene.effects.indicateRedstone(lever);
        scene.world.modifyBlockEntity(vent, VatSideBlockEntity.class, vatSide -> vatSide.setDisplayType(DisplayType.CLOSED_VENT));
        scene.idle(50);

        ElementLink<WorldSectionElement> everythingLink = scene.world.makeSectionIndependent(everything);
        scene.world.moveSection(everythingLink, util.vector.of(0, 3, 0), 10);
        scene.idle(20);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        scene.idle(10);
        ElementLink<WorldSectionElement> blazeBurners = scene.world.showIndependentSection(util.select.fromTo(5, 1, 2, 6, 1, 3), Direction.WEST);
        scene.world.moveSection(blazeBurners, util.vector.of(-3, 0, 0), 0);
        scene.idle(20);
        scene.world.hideIndependentSection(blazeBurners, Direction.WEST);
        scene.idle(20);
        ElementLink<WorldSectionElement> coolers = scene.world.showIndependentSection(util.select.fromTo(7, 1, 2, 8, 1, 3), Direction.WEST);
        scene.world.moveSection(coolers, util.vector.of(-5, 0, 0), 0);
        scene.idle(20);
        scene.world.moveSection(everythingLink, util.vector.of(0, -2, 0), 10);
        scene.idle(40);

        scene.markAsFinished();
    };

    public static void uv(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("uv", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 6);
        scene.scaleSceneView(0.8f);
        scene.showBasePlate();


        scene.idle(10);
        scene.world.showSection(util.select.fromTo(1, 1, 1, 4, 4, 4), Direction.DOWN);
        scene.overlay.showText(60)
            .text("This text is defined in a language file.");
        scene.idle(80);

        scene.idle(10);
        scene.overlay.showOutline(PonderPalette.WHITE, "top_glass", util.select.fromTo(2, 4, 2, 3, 4, 3), 80);
        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.of(3, 5, 3))
            .attachKeyFrame();
        scene.idle(100);

        scene.world.showSection(util.select.fromTo(0, 2, 2, 0, 3, 3), Direction.EAST);
        scene.idle(10);
        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(util.grid.at(0, 3, 2), Direction.WEST))
            .attachKeyFrame();
        scene.idle(100);

        scene.markAsFinished();
    };

};
