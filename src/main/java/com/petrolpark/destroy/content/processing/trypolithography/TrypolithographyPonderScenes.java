package com.petrolpark.destroy.content.processing.trypolithography;

import com.petrolpark.destroy.DestroyItems;
import com.petrolpark.destroy.content.processing.trypolithography.keypunch.KeypunchBlockEntity;
import com.petrolpark.util.BinaryMatrix4x4;
import com.simibubi.create.content.logistics.depot.EjectorBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.simibubi.create.foundation.ponder.element.BeltItemElement;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.element.InputWindowElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class TrypolithographyPonderScenes {
    
    public static void intro(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("trypolithography_intro", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 9);
        scene.showBasePlate();
        scene.scaleSceneView(0.75f);

        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame();

        scene.idle(5);
        scene.world().showSection(util.select().position(6, 0, 9), Direction.NORTH);
        scene.idle(5);
        for (int z = 9; z >= 5; z--) {
            scene.world().showSection(util.select().position(7, 1, z), Direction.DOWN);
            scene.idle(5);
        };

        BlockPos firstBelt = util.grid().at(7, 1, 4);
        BlockPos secondBelt = util.grid().at(5, 1, 4);
        
        scene.world().showSection(util.select().fromTo(firstBelt, secondBelt), Direction.SOUTH);
        scene.world().showSection(util.select().position(7, 2, 5), Direction.DOWN);
        scene.idle(5);

        ElementLink<WorldSectionElement> brassCasing = scene.world().showIndependentSection(util.select().position(4, 1, 3), Direction.WEST);
        scene.world().moveSection(brassCasing, util.vector().of(0d, 0d, 1d), 0);

        scene.world().showSection(util.select().position(7, 3, 5), Direction.DOWN);
        scene.idle(5);

        BlockPos firstKeypunch = util.grid().at(7, 3, 4);
        BlockPos secondKeypunch = util.grid().at(5, 3, 4);

        scene.world().showSection(util.select().fromTo(firstKeypunch, secondKeypunch), Direction.DOWN);
        scene.idle(80);

        scene.overlay().showText(130)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(firstBelt, Direction.UP))
            .attachKeyFrame();
        ElementLink<BeltItemElement> circuit1Element = scene.world().createItemOnBelt(firstBelt, Direction.DOWN, DestroyItems.CIRCUIT_MASK.asStack());

        scene.idle(40);
        ItemStack circuit1 = DestroyItems.CIRCUIT_MASK.asStack();
        int pattern1 = BinaryMatrix4x4.set1(0, 0);
        CircuitMaskItem.putPattern(circuit1, pattern1);
        scene.world().changeBeltItemTo(circuit1Element, circuit1);
        scene.idle(40);
        scene.addInstruction(new ShowCircuitPatternPonderInstruction(Pointing.UP, util.vector().blockSurface(firstBelt, Direction.DOWN), pattern1, 50));
        scene.idle(70);
        scene.world().stallBeltItem(circuit1Element, false);
        scene.idle(95);
        scene.world().stallBeltItem(circuit1Element, true);
        scene.idle(20);

        scene.overlay().showText(100)
            .pointAt(util.vector().blockSurface(secondKeypunch, Direction.WEST))
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        scene.idle(20);
        scene.overlay().showControls(
            util.vector().blockSurface(secondKeypunch, Direction.UP), Pointing.DOWN, 80)
            .rightClick();
        scene.idle(20);
        scene.world().modifyBlockEntity(secondKeypunch, KeypunchBlockEntity.class, be -> { be.setPistonPosition(6); });
        scene.idle(80);

        scene.world().removeItemsFromBelt(secondBelt);
        ElementLink<BeltItemElement> circuit2Element = scene.world().createItemOnBelt(secondBelt, Direction.DOWN, circuit1);
        scene.world().stallBeltItem(circuit2Element, false);
        scene.idle(40);

        ItemStack circuit2 = DestroyItems.CIRCUIT_MASK.asStack();
        int pattern2 = BinaryMatrix4x4.set1(pattern1, 6);
        CircuitMaskItem.putPattern(circuit2, pattern2);
        scene.world().changeBeltItemTo(circuit2Element, circuit2);
        scene.idle(40);
        scene.addInstruction(new ShowCircuitPatternPonderInstruction(Pointing.UP, util.vector().blockSurface(secondBelt, Direction.DOWN), pattern2, 50));
        scene.idle(70);

        scene.world().hideIndependentSection(brassCasing, Direction.NORTH);
        scene.idle(10);

        BlockPos thirdBelt = util.grid().at(3, 1, 4);
        BlockPos fourthBelt = util.grid().at(1, 1, 4);

        scene.world().showSection(util.select().fromTo(4, 1, 5, 5, 1, 5).add(util.select().fromTo(fourthBelt, util.grid().at(4, 1, 4))), Direction.NORTH);
        scene.idle(10);
        scene.world().stallBeltItem(circuit2Element, false);
        scene.world().showSection(util.select().fromTo(1, 3, 4, 4, 3, 4), Direction.DOWN);
        scene.world().modifyBlockEntity(util.grid().at(3, 3, 4), KeypunchBlockEntity.class, be -> { be.setPistonPosition(9); });
        scene.world().modifyBlockEntity(util.grid().at(1, 3, 4), KeypunchBlockEntity.class, be -> { be.setPistonPosition(15); });
        scene.idle(35);
        scene.world().stallBeltItem(circuit2Element, true);
        scene.idle(80);

        ItemStack circuit3 = DestroyItems.CIRCUIT_MASK.asStack();
        CircuitMaskItem.putPattern(circuit3, BinaryMatrix4x4.set1(pattern2, 9));
        scene.world().removeItemsFromBelt(thirdBelt);
        ElementLink<BeltItemElement> circuit3Element = scene.world().createItemOnBelt(thirdBelt, Direction.DOWN, circuit3);
        scene.idle(40);
        scene.world().stallBeltItem(circuit3Element, false);
        scene.idle(70);
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .pointAt(util.vector().blockSurface(fourthBelt, Direction.WEST))
            .attachKeyFrame();
        scene.idle(25);

        scene.world().removeItemsFromBelt(fourthBelt);
        ElementLink<BeltItemElement> circuit4Element = scene.world().createItemOnBelt(fourthBelt, Direction.DOWN, circuit3);
        scene.idle(35);
        scene.world().changeBeltItemTo(circuit4Element, DestroyItems.RUINED_CIRCUIT_MASK.asStack());
        ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, DestroyItems.CIRCUIT_MASK.asStack());
        scene.effects().emitParticles(util.vector().blockSurface(fourthBelt, Direction.UP), scene.effects().particleEmitterWithinBlockSpace(particle, Vec3.ZERO), 5f, 5);
        scene.idle(60);

        scene.markAsFinished();
    };

    public static void rotating(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("trypolithography_rotating", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();

        BlockPos bottomBearing = util.grid().at(1, 1, 1);
        BlockPos depot = util.grid().at(1, 2, 1);
        BlockPos keypunch = util.grid().at(1, 4, 1);
        BlockPos topBearing = util.grid().at(1, 5, 1);

        scene.idle(10);
        scene.world().showSection(util.select().position(bottomBearing), Direction.DOWN);
        scene.idle(10);
        ElementLink<WorldSectionElement> depotSection = scene.world().showIndependentSection(util.select().position(depot), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(1, 0, 3, 1, 4, 3), Direction.NORTH);
        scene.idle(10);
        scene.world().showSection(util.select().position(1, 4, 2), Direction.DOWN);
        scene.idle(10);
        ElementLink<WorldSectionElement> keypunchSection = scene.world().showIndependentSection(util.select().position(keypunch), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(160)
            .text("This text is defined in a language file.");
        scene.idle(20);
        scene.world().createItemOnBeltLike(depot, Direction.EAST, DestroyItems.CIRCUIT_MASK.asStack());
        scene.idle(20);
        scene.world().modifyBlockEntity(keypunch, KeypunchBlockEntity.class, be -> be.punchingBehaviour.start());
        scene.idle(40);
        scene.world().removeItemsFromBelt(depot);

        int pattern1 = BinaryMatrix4x4.set1(0, 0);
        ItemStack circuit1 = DestroyItems.CIRCUIT_MASK.asStack();
        CircuitMaskItem.putPattern(circuit1, pattern1);
        scene.world().createItemOnBeltLike(depot, Direction.DOWN, circuit1);
        scene.idle(20);
        scene.addInstruction(new ShowCircuitPatternPonderInstruction(Pointing.UP, util.vector().blockSurface(depot, Direction.DOWN), pattern1, 60));
        scene.idle(80);

        scene.overlay().showText(120)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(depot, Direction.NORTH));
        scene.idle(20);
        scene.world().rotateBearing(bottomBearing, 90, 20);
        scene.world().rotateSection(depotSection, 0d, 90d, 0d, 20);
        scene.idle(20);
        scene.world().modifyBlockEntity(keypunch, KeypunchBlockEntity.class, be -> be.punchingBehaviour.start());
        scene.idle(40);
        scene.world().removeItemsFromBelt(depot);

        int pattern2 = BinaryMatrix4x4.set1(pattern1, 3);
        ItemStack circuit2 = DestroyItems.CIRCUIT_MASK.asStack();
        CircuitMaskItem.putPattern(circuit2, pattern2);
        scene.world().createItemOnBeltLike(depot, Direction.DOWN, circuit2);
        scene.idle(20);
        scene.addInstruction(new ShowCircuitPatternPonderInstruction(Pointing.UP, util.vector().blockSurface(depot, Direction.DOWN), pattern2, 120));
        scene.idle(40);

        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.GREEN)
            .pointAt(util.vector().blockSurface(depot, Direction.UP))
            .attachKeyFrame();
        scene.idle(100);

        scene.world().showSection(util.select().position(topBearing), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(160)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(keypunch, Direction.WEST))
            .attachKeyFrame();
        scene.idle(20);
        scene.world().rotateBearing(topBearing, -90, 20);
        scene.world().rotateSection(keypunchSection, 0d, -90d, 0d, 20);
        scene.idle(20);
        scene.world().modifyBlockEntity(keypunch, KeypunchBlockEntity.class, be -> be.punchingBehaviour.start());
        scene.idle(40);
        scene.world().removeItemsFromBelt(depot);

        int pattern3 = BinaryMatrix4x4.set1(pattern2, 15);
        ItemStack circuit3 = DestroyItems.CIRCUIT_MASK.asStack();
        CircuitMaskItem.putPattern(circuit3, pattern3);
        scene.world().createItemOnBeltLike(depot, Direction.DOWN, circuit3);
        scene.idle(20);
        scene.addInstruction(new ShowCircuitPatternPonderInstruction(Pointing.UP, util.vector().blockSurface(depot, Direction.DOWN), pattern3, 60));
        scene.idle(80);

        scene.markAsFinished();
    };

    public static void flipping(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("trypolithography_flipping", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos ejector = util.grid().at(3, 1, 2);
        Selection ejectorSelection = util.select().position(ejector);
        BlockPos lever = util.grid().at(4, 1, 2);
        Selection leverSelection = util.select().position(lever);
        BlockPos keypunch = util.grid().at(3, 3, 2);
        BlockPos rightBelt = util.grid().at(1, 1, 2);
        Selection belt = util.select().fromTo(rightBelt, util.grid().at(2, 1, 2));

        scene.idle(10);
        scene.world().showSection(util.select().position(2, 0, 5), Direction.NORTH);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(3, 1, 3, 3, 1, 5), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(leverSelection, Direction.WEST);
        scene.idle(10);
        scene.world().showSection(util.select().position(ejector), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(3, 2, 3, 3, 3, 3), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(keypunch), Direction.SOUTH);
        scene.idle(20);
        scene.world().toggleRedstonePower(leverSelection);
        scene.effects().indicateRedstone(lever);
        scene.world().modifyBlockEntityNBT(ejectorSelection, EjectorBlockEntity.class, nbt -> nbt.putBoolean("Powered", true));
        scene.idle(20);

        scene.world().createItemOnBeltLike(ejector, Direction.EAST, DestroyItems.CIRCUIT_MASK.asStack());
        scene.idle(20);
        scene.world().modifyBlockEntity(keypunch, KeypunchBlockEntity.class, be -> be.punchingBehaviour.start());
        scene.idle(40);
        scene.world().removeItemsFromBelt(ejector);

        int pattern1 = BinaryMatrix4x4.set1(0, 1);
        ItemStack circuit1 = DestroyItems.CIRCUIT_MASK.asStack();
        CircuitMaskItem.putPattern(circuit1, pattern1);
        scene.world().createItemOnBeltLike(ejector, Direction.DOWN, circuit1);
        scene.idle(20);
        scene.addInstruction(new ShowCircuitPatternPonderInstruction(Pointing.UP, util.vector().blockSurface(ejector, Direction.DOWN), pattern1, 60));
        scene.idle(80);

        scene.world().showSection(belt, Direction.DOWN);
        scene.world().setKineticSpeed(belt, 1 / 256f);
        scene.idle(20);

        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(ejector, Direction.NORTH));
        scene.idle(20);
        scene.effects().rotationDirectionIndicator(util.grid().at(2, 2, 2));
        scene.idle(60);

        scene.world().toggleRedstonePower(leverSelection);
        scene.effects().indicateRedstone(lever);
        scene.idle(10);
        scene.world().modifyBlockEntity(ejector, EjectorBlockEntity.class, be -> {
            be.activate();
        });
        scene.idle(20);

        scene.world().modifyEntities(ItemEntity.class, Entity::discard);
        ItemStack circuit1Flipped = circuit1.copy();
        circuit1Flipped.getOrCreateTag().putBoolean("Flipped", true);
        scene.idle(5);
        scene.world().removeItemsFromBelt(rightBelt);
        scene.idle(5);
        scene.world().createItemOnBeltLike(rightBelt, Direction.DOWN, circuit1Flipped);
        scene.idle(20);
        scene.world().toggleRedstonePower(leverSelection);
        scene.effects().indicateRedstone(lever);
        scene.idle(40);
        scene.world().showSection(util.select().fromTo(2, 1, 1, 3, 1, 1), Direction.SOUTH);
        scene.world().setKineticSpeed(belt, -16f);
        scene.world().modifyBlockEntityNBT(ejectorSelection, EjectorBlockEntity.class, nbt -> nbt.putBoolean("Powered", true));
        scene.idle(40);

        scene.overlay().showText(160)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.GREEN)
            .pointAt(util.vector().blockSurface(keypunch, Direction.WEST))
            .attachKeyFrame();
        scene.idle(40);
        scene.world().modifyBlockEntity(keypunch, KeypunchBlockEntity.class, be -> be.punchingBehaviour.start());
        scene.idle(40);
        scene.world().removeItemsFromBelt(ejector);
        
        int pattern2 = BinaryMatrix4x4.set1(pattern1, 2);
        ItemStack circuit2 = circuit1Flipped.copy();
        CircuitMaskItem.putPattern(circuit2, pattern2);
        scene.world().createItemOnBeltLike(ejector, Direction.DOWN, circuit2);
        scene.idle(20);
        scene.addInstruction(new ShowCircuitPatternPonderInstruction(Pointing.UP, util.vector().blockSurface(ejector, Direction.DOWN), pattern2, 60));
        scene.idle(80);
    };
};
