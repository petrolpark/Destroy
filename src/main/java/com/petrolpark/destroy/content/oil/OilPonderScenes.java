package com.petrolpark.destroy.content.oil;

import com.petrolpark.client.ponder.PonderPlayer;
import com.petrolpark.destroy.DestroyFluids;
import com.petrolpark.destroy.DestroyItems;
import com.petrolpark.destroy.content.oil.pumpjack.PumpjackBlockEntity;
import com.petrolpark.destroy.content.oil.seismology.ShowSeismographPonderInstruction;
import com.petrolpark.destroy.content.oil.seismology.SeismographItem.Seismograph;
import com.petrolpark.destroy.content.oil.seismology.SeismographItem.Seismograph.Mark;
import com.petrolpark.destroy.content.oil.seismology.ShowSeismographPonderInstruction.SeismographElement;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.math.VecHelper;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.element.InputWindowElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class OilPonderScenes {

    public static void seismometer(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("seismometer", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();

        BlockPos cartographyTablePos = util.grid().at(1, 1, 1);
        Selection cartographyTableSelection = util.select().position(cartographyTablePos);
        scene.idle(10);
        scene.world().showSection(cartographyTableSelection, Direction.DOWN);

        scene.overlay().showText(220)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(cartographyTablePos, Direction.WEST));
        scene.idle(60);
        Vec3 cartographyTableTop = util.vector().blockSurface(cartographyTablePos, Direction.UP);
        scene.overlay().showText(160)
            .text("This text is defined in a language file")
            .colored(PonderPalette.RED)
            .independent(0);
        scene.overlay().showControls(cartographyTableTop, Pointing.DOWN, 40)
            .withItem(new ItemStack(Items.FILLED_MAP));
        scene.idle(60);
        scene.overlay().showText(100)
            .text("This text is defined in a language file")
            .colored(PonderPalette.GREEN)
            .independent(50);
        scene.overlay().showControls(cartographyTableTop, Pointing.DOWN, 40)
            .withItem(DestroyItems.SEISMOMETER.asStack());
        scene.idle(60);
        scene.overlay().showControls(cartographyTableTop, Pointing.DOWN, 40)
            .withItem(DestroyItems.SEISMOGRAPH.asStack());
        scene.idle(60);
        scene.world().hideSection(cartographyTableSelection, Direction.UP);
        scene.idle(20);

        scene.overlay().showText(160)
            .text("This text is defined in a language file.")
            .independent()
            .attachKeyFrame();
        scene.idle(20);
        addTNTAndPlayer(scene, util, cartographyTablePos, util.grid().at(1, 1, 0));
        scene.idle(40);

        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .independent();
        scene.idle(20);
            scene.overlay().showControls(util.vector().topOf(util.grid().at(1, 2, 0)), Pointing.DOWN, 80)
            .withItem(DestroyItems.TOUCH_POWDER.asStack());
        scene.idle(40);
        scene.world().createItemEntity(util.vector().of(1.5d, 2.2d, 0.75d), util.vector().of(0d, 0.1d, 0.2d), DestroyItems.TOUCH_POWDER.asStack());
        scene.idle(80);

        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .independent();
        scene.idle(100);

        scene.markAsFinished();
    };
    
    public static void seismograph(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("seismograph", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 3);
        ElementLink<WorldSectionElement> firstBasePlate = scene.world().showIndependentSection(util.select().fromTo(0, 0, 0, 2, 0, 2), Direction.UP);

        BlockPos playerPos = util.grid().at(1, 1, 0);

        Seismograph seismograph = newSeismograph(scene);
        markSeismograph(scene, seismograph, 3, 5, Mark.CROSS);
        setSeismographRow(scene, seismograph, 5, (byte)0b111);
        setSeismographColumn(scene, seismograph, 3, (byte)0b1011);

        scene.idle(20);
        scene.overlay().showText(220)
            .text("This text is defined in a language file.")
            .independent();
        ElementLink<EntityElement> player = addTNTAndPlayer(scene, util, util.grid().at(1, 1, 1), playerPos);
        scene.idle(20);

        SeismographElement element = ShowSeismographPonderInstruction.add(scene, Pointing.RIGHT, util.vector().topOf(playerPos), seismograph);
        scene.idle(20);
        scene.addKeyframe();
        element.highlightCell(scene, PonderPalette.WHITE, 3, 5, 70);
        scene.idle(110);

        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .independent();
        scene.idle(20);
        element.highlightRow(scene, PonderPalette.RED, 5, 40);
        scene.idle(40);
        element.highlightColumn(scene, PonderPalette.RED, 3, 40);
        scene.idle(60);

        scene.overlay().showText(120)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .independent();
        element.highlightCell(scene, PonderPalette.WHITE, 5, 1, 120);
        for (Mark mark : new Mark[]{Mark.GUESSED_TICK, Mark.GUESSED_CROSS, Mark.NONE}) {
            scene.idle(30);
            markSeismograph(scene, seismograph, 5, 1, mark);
        };
        scene.idle(60);

        scene.overlay().showText(310)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .independent();
        element.highlightRow(scene, PonderPalette.RED, 5, 310);
        scene.idle(40);
        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.INPUT)
            .independent(60);
        scene.idle(120);
        scene.overlay().showText(150)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .independent(60);
        scene.idle(20);
        for (int i : new int[]{0, 4, 5}) {
            for (int j = 0; j < 8; j++) {
                if (j != 3) markSeismograph(scene, seismograph, j, 5, (j >= i && j <= i + 2) ? Mark.GUESSED_TICK : Mark.GUESSED_CROSS);
            };
            scene.idle(40);
        };
        for (int j = 0; j < 8; j++) {
            if (j != 3) markSeismograph(scene, seismograph, j, 5, Mark.NONE);
        };
        scene.idle(30);

        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .independent();
        element.highlightColumn(scene, PonderPalette.GREEN, 3, 560);
        scene.idle(100);
        scene.overlay().showText(280)
            .text("This text is defined in a language file.")
            .independent();
        scene.idle(20);
        for (int i = 3; i >= 0; i--) {
            for (int j = 0; j <= 4; j++) {
                markSeismograph(scene, seismograph, 3, j, (j == i || j == i + 1) ? Mark.GUESSED_TICK : Mark.GUESSED_CROSS);
            };
            scene.idle(30);
        };
        scene.overlay().showText(140)
            .text("This text is defined in a language file.")
            .independent(50);
        scene.idle(20);
        for (int i : new int[]{7, 6, 4, 3}) {
            for (int j = 3; j <= 8; j++) {
                if (j != 5) markSeismograph(scene, seismograph, 3, j, (j == i) ? Mark.GUESSED_TICK : Mark.GUESSED_CROSS);
            };
            scene.idle(30);
        };
        scene.idle(20);
        scene.overlay().showText(240)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .independent();
        scene.idle(60);
        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.GREEN)
            .independent(50);
        scene.idle(100);
        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .independent(50);
        element.highlightColumn(scene, PonderPalette.RED, 3, 80);
        markSeismograph(scene, seismograph, 3, 2, Mark.GUESSED_TICK);
        markSeismograph(scene, seismograph, 3, 3, Mark.GUESSED_CROSS);
        scene.idle(100);
        for (int j = 0; j < 8; j++) {
            if (j != 5) markSeismograph(scene, seismograph, 3, j, Mark.NONE);
        };
        scene.idle(20);

        scene.overlay().showText(180)
            .text("This text is defined in a language file.")
            .attachKeyFrame();
        ElementLink<WorldSectionElement> grass = scene.world().showIndependentSection(util.select().fromTo(0, 0, 3, 2, 1, 14), Direction.UP);
        ElementLink<WorldSectionElement> secondBasePlate = scene.world().showIndependentSection(util.select().fromTo(0, 0, 15, 2, 0, 17), Direction.UP);
        scene.idle(20);
        scene.world().modifyEntity(player, e -> {
            e.zo = -1d;
        });
        scene.world().moveSection(firstBasePlate, util.vector().of(0d, 0d, -15d), 200);
        scene.world().moveSection(grass, util.vector().of(0d, 0d, -15d), 200);
        scene.world().moveSection(secondBasePlate, util.vector().of(0d, 0d, -15d), 200);
        for (byte[] numbers : new byte[][]{
            new byte[]{0, 0, 1, 0b10111, 0b101},
            new byte[]{5, 6, 0, 0b10101, 0b101},
            new byte[]{7, 1, 1, 0b110111, 0b11111},
            new byte[]{1, 7, 1, 0b1101, -1},
            new byte[]{2, 4, 0, 0b101101, 0b101111},
            new byte[]{4, 3, 0, 0b1100011, 0b101},
            new byte[]{6, 2, 1, 0b1110111, 0b110111}
        }) {
            byte x = numbers[0];
            byte z = numbers[1];
            boolean tick = numbers[2] == 1;
            byte row = numbers[3];
            byte column = numbers[4];
            scene.idle(20);
            scene.effects().emitParticles(VecHelper.getCenterOf(util.grid().at(tick ? 0 : 2, 1, 1)), scene.effects().particleEmitterWithinBlockSpace(ParticleTypes.EXPLOSION, Vec3.ZERO), 1f, 1);
            scene.idle(10);
            element.highlightRow(scene, PonderPalette.WHITE, z, 20);
            element.highlightColumn(scene, PonderPalette.WHITE, x, 20);
            markSeismograph(scene, seismograph, x, z, tick ? Mark.TICK : Mark.CROSS);
            setSeismographRow(scene, seismograph, z, row);
            setSeismographColumn(scene, seismograph, x, column);
        };
        scene.world().modifyEntity(player, e -> {
            e.zo = e.position().z;
        });
        scene.idle(20);
        scene.world().hideIndependentSection(firstBasePlate, Direction.DOWN);
        scene.world().hideIndependentSection(grass, Direction.DOWN);
        scene.idle(40);

        scene.overlay().showText(500)
            .text("This text is defined in a language file.");
        scene.idle(20);
        element.highlightColumn(scene, PonderPalette.GREEN, 1, 30);
        scene.idle(5);
        for (int i = 0; i < 7; i++) {
            markSeismograph(scene, seismograph, 1, i, Mark.GUESSED_TICK);
            scene.idle(3);
        };
        scene.idle(15);
        element.highlightRow(scene, PonderPalette.GREEN, 5, 30);
        scene.idle(10);
        for (int i = 0; i < 3; i++) {
            markSeismograph(scene, seismograph, i, 5, Mark.GUESSED_TICK);
            scene.idle(3);
        };
        for (int i = 4; i < 8; i++) {
            markSeismograph(scene, seismograph, i, 5, Mark.GUESSED_CROSS);
            scene.idle(3);
        };
        scene.idle(15);
        element.highlightColumn(scene, PonderPalette.GREEN, 7, 30);
        for (int i = 0; i < 8; i++) {
            if (i != 1) markSeismograph(scene, seismograph, 7, i, i <= 4 ? Mark.GUESSED_TICK : Mark.GUESSED_CROSS);
            scene.idle(3);
        };
        scene.idle(15);
        element.highlightRow(scene, PonderPalette.GREEN, 0, 30);
        for (int i = 2; i < 7; i++) {
            markSeismograph(scene, seismograph, i, 0, i == 2 ? Mark.GUESSED_TICK : Mark.GUESSED_CROSS);
            scene.idle(3);
        };
        scene.idle(15);
        element.highlightColumn(scene, PonderPalette.GREEN, 2, 30);
        for (int i : new int[]{1, 2, 3, 6, 7}) {
            markSeismograph(scene, seismograph, 2, i, i <= 3 ? Mark.GUESSED_TICK : Mark.GUESSED_CROSS);
            scene.idle(3);
        };
        scene.idle(21);
        element.highlightColumn(scene, PonderPalette.GREEN, 0, 30);
        for (int i = 1; i < 8; i++) {
            if (i != 5) markSeismograph(scene, seismograph, 0, i, Mark.GUESSED_CROSS);
            scene.idle(3);
        };
        scene.idle(18);
        element.highlightRow(scene, PonderPalette.GREEN, 1, 30);
        for (int i = 3; i < 7; i++) {
            markSeismograph(scene, seismograph, i, 1, i % 3 == 0 ? Mark.GUESSED_TICK : Mark.GUESSED_CROSS);
            scene.idle(3);
        };
        scene.idle(21);
        element.highlightRow(scene, PonderPalette.GREEN, 2, 30);
        for (int i = 3; i < 6; i++) {
            markSeismograph(scene, seismograph, i, 2, i != 4 ? Mark.GUESSED_TICK : Mark.GUESSED_CROSS);
            scene.idle(3);
        };
        scene.idle(27);
        element.highlightColumn(scene, PonderPalette.GREEN, 6, 30);
        for (int i : new int[]{3, 4, 6, 7}) {
            markSeismograph(scene, seismograph, 6, i, i != 4 ? Mark.GUESSED_TICK : Mark.GUESSED_CROSS);
            scene.idle(3);
        };
        scene.idle(27);
        element.highlightRow(scene, PonderPalette.GREEN, 7, 30);
        for (int i = 3; i < 6; i++) {
            markSeismograph(scene, seismograph, i, 7, i == 5 ? Mark.GUESSED_TICK : Mark.GUESSED_CROSS);
            scene.idle(3);
        };
        scene.idle(27);
        element.highlightColumn(scene, PonderPalette.GREEN, 4, 30);
        for (int i : new int[]{4, 6}) {
            markSeismograph(scene, seismograph, 4, i, Mark.GUESSED_TICK);
            scene.idle(3);
        };
        scene.idle(30);
        element.highlightColumn(scene, PonderPalette.GREEN, 3, 30);
        for (int i : new int[]{3, 4, 6}) {
            markSeismograph(scene, seismograph, 3, i, i == 4 ? Mark.GUESSED_TICK : Mark.GUESSED_CROSS);
            scene.idle(3);
        };
        scene.idle(27);
        element.highlightColumn(scene, PonderPalette.GREEN, 5, 30);
        for (int i : new int[]{3, 4}) {
            markSeismograph(scene, seismograph, 5, i, Mark.GUESSED_CROSS);
            scene.idle(3);
        };
        scene.idle(60);

        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .independent();
        scene.idle(100);
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.GREEN)
            .independent();
        scene.idle(40);
        element.highlightPlus(scene, PonderPalette.WHITE, 2, 1, 60);
        element.highlightCell(scene, PonderPalette.GREEN, 2, 1, 60);
        scene.idle(80);

        scene.overlay().showText(240)
            .text("This text is defined in a language file.")
            .independent();
        scene.idle(60);
        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .independent(70)
            .colored(PonderPalette.GREEN);
        element.highlightPlus(scene, PonderPalette.WHITE, 1, 5, 80);
        element.highlightCell(scene, PonderPalette.GREEN, 1, 5, 80);
        scene.idle(100);
        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .independent(70)
            .colored(PonderPalette.RED);
        element.highlightPlus(scene, PonderPalette.WHITE, 5, 3, 80);
        element.highlightCell(scene, PonderPalette.RED, 5, 3, 80);
        scene.idle(100);

        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .independent();
        scene.idle(120);


        scene.markAsFinished();
    };

    public static final Seismograph newSeismograph(SceneBuilder scene) {
        Seismograph seismograph = new Seismograph();
        scene.addInstruction(s -> seismograph.clear());
        return seismograph;
    };

    public static final void markSeismograph(SceneBuilder scene, Seismograph seismograph, int x, int z, Mark mark) {
        scene.addInstruction(s -> seismograph.mark(x, z, mark));
    };

    public static final void setSeismographRow(SceneBuilder scene, Seismograph seismograph, int row, byte data) {
        if (row >= 0 && row < 8) scene.addInstruction(s -> {
            seismograph.getRows()[row] = data;
            seismograph.discoverRow(row, null, null);
        });
    };

    public static final void setSeismographColumn(SceneBuilder scene, Seismograph seismograph, int column, byte data) {
        if (column >= 0 && column < 8) scene.addInstruction(s -> {
            seismograph.getColumns()[column] = data;
            seismograph.discoverColumn(column, null, null);
        });
    };

    public static void pumpjack(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("pumpjack", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        Selection pumpjack = util.select().fromTo(1, 1, 3, 3, 2, 3);
        Selection kinetics = util.select().position(3, 1, 4).add(util.select().fromTo(2, 0, 5, 3, 1, 5));
        Selection pipes = util.select().fromTo(2, 1, 1, 3, 1, 2);
        BlockPos pumpjackPos = util.grid().at(2, 1, 3);
        BlockPos pumpPos = util.grid().at(2, 1, 2);

        scene.idle(10);
        scene.world().showSection(kinetics, Direction.NORTH);
        scene.idle(10);
        scene.world().showSection(pumpjack, Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(pumpjackPos, Direction.NORTH))
            .attachKeyFrame();
        scene.idle(120);
        
        scene.world().showSection(pipes, Direction.SOUTH);
        scene.idle(10);
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(pumpPos, Direction.UP))
            .attachKeyFrame();
        scene.world().modifyBlockEntity(pumpjackPos, PumpjackBlockEntity.class, be -> {
            be.tank.allowInsertion();
            be.tank.getPrimaryHandler().fill(new FluidStack(DestroyFluids.CRUDE_OIL.get(), 1000), FluidAction.EXECUTE);
            be.tank.forbidInsertion();
        });
        scene.idle(20);
        scene.world().propagatePipeChange(pumpPos);
        scene.idle(100);

        scene.markAsFinished();
    };
    
    public static ElementLink<EntityElement> addTNTAndPlayer(SceneBuilder scene, SceneBuildingUtil util, BlockPos tntPos, BlockPos playerPos) {
        // Add player
        ElementLink<EntityElement> playerElement = scene.world().createEntity(w -> {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer localPlayer = minecraft.player;
            if (localPlayer == null) return null;
            PonderPlayer player = new PonderPlayer(w, localPlayer.getScoreboardName());
            Vec3 v = util.vector().topOf(playerPos.below());
            player.setPos(v.x, v.y, v.z);
            player.xo = v.x;
            player.yo = v.y;
            player.zo = v.z;
            player.setInvisible(true);
            return player;
        });

        // Add TNT
        scene.world().createEntity(w -> {
            PrimedTnt tnt = new PrimedTnt(EntityType.TNT, w);
            Vec3 v = util.vector().topOf(tntPos.below());
            tnt.setPos(v.x, v.y, v.z);
            tnt.xo = v.x;
            tnt.yo = v.y;
            tnt.zo = v.z;
            tnt.setFuse(120);
            return tnt;
        });

        // Set and then un-set the Player invisible so it it doesn't awkwardly jerk when added to the scene
        scene.world().modifyEntity(playerElement, entity -> {
            if (!(entity instanceof PonderPlayer player)) return;
            player.setItemInHand(InteractionHand.MAIN_HAND, DestroyItems.SEISMOMETER.asStack());
            player.setItemInHand(InteractionHand.OFF_HAND, DestroyItems.SEISMOGRAPH.asStack());
            player.setInvisible(false);
        });

        scene.overlay().showControls(util.vector().blockSurface(playerPos.above(), Direction.UP), Pointing.DOWN, 50)
            .withItem(DestroyItems.SEISMOMETER.asStack());
        scene.idle(60);

        scene.overlay().showControls(util.vector().blockSurface(playerPos.above(), Direction.UP), Pointing.DOWN, 50)
            .withItem(DestroyItems.SEISMOGRAPH.asStack());
        scene.idle(60);

        scene.effects().emitParticles(VecHelper.getCenterOf(tntPos), scene.effects().particleEmitterWithinBlockSpace(ParticleTypes.EXPLOSION_EMITTER, Vec3.ZERO), 1f, 1);
        return playerElement;
    };
};
