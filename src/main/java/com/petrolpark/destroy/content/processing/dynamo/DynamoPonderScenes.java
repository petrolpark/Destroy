package com.petrolpark.destroy.content.processing.dynamo;

import com.petrolpark.destroy.DestroyBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class DynamoPonderScenes {

    public static void arcFurnace(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("arc_furnace", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();
        scene.scaleSceneView(0.75f);

        BlockPos basin = util.grid().at(1, 2, 1);
        BlockPos underBasin = util.grid().at(1, 1, 1);
        BlockPos dynamo = util.grid().at(1, 4, 1);
        BlockPos furnaceLid = util.grid().at(1, 3, 1);
        scene.world().cycleBlockProperty(dynamo, DynamoBlock.ARC_FURNACE);

        scene.idle(10);
        scene.world().showSection(util.select().position(underBasin), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(basin), Direction.DOWN);
        scene.idle(10);
        for (int y = 0; y <= 5; y++) {
            scene.world().showSection(util.select().fromTo(1, y, 3, 2, y, 3), Direction.NORTH);
            scene.idle(5);
        };
        scene.world().showSection(util.select().position(1, 5, 2), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(1, 5, 1), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(dynamo), Direction.SOUTH);
        scene.idle(10);

        scene.overlay().showText(120)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().topOf(basin));
        scene.overlay().showOutline(PonderPalette.WHITE, "furnace lid", util.select().position(furnaceLid), 120);
        scene.idle(60);
        scene.overlay().showControls(util.vector().blockSurface(furnaceLid, Direction.NORTH), Pointing.RIGHT, 40).rightClick().withItem(DestroyBlocks.CARBON_FIBER_BLOCK.asStack());
        scene.idle(20);
        scene.world().showIndependentSectionImmediately(util.select().position(furnaceLid));
        scene.world().cycleBlockProperty(dynamo, DynamoBlock.ARC_FURNACE);
        scene.idle(60);
        
        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(dynamo, Direction.WEST));
        scene.idle(100);

        scene.overlay().showText(120)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(underBasin, Direction.WEST))
            .attachKeyFrame()
            .colored(PonderPalette.GREEN);
        scene.idle(20);
        scene.world().hideSection(util.select().position(underBasin), Direction.EAST);
        scene.idle(20);
        ElementLink<WorldSectionElement> burner = scene.world().showIndependentSection(util.select().position(1, 1, 0), Direction.EAST);
        scene.world().moveSection(burner, util.vector().of(0d, 0d, 1d), 0);
        scene.idle(10);
        scene.overlay().showOutline(PonderPalette.RED, "burner", util.select().position(underBasin), 40);
        scene.idle(50);
        scene.world().hideIndependentSection(burner, Direction.EAST);
        scene.idle(20);
        scene.world().showSection(util.select().position(underBasin), Direction.EAST);
        scene.idle(20);

        scene.world().showSection(util.select().fromTo(2, 1, 1, 2, 2, 1), Direction.WEST);
        scene.world().showSection(util.select().fromTo(0, 1, 1, 0, 2, 1), Direction.EAST);
        scene.idle(20);

        scene.overlay().showText(120)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(basin, Direction.NORTH))
            .attachKeyFrame();
        scene.idle(20);
        scene.overlay().showControls(util.vector().blockSurface(basin.east(), Direction.EAST), Pointing.RIGHT, 30).withItem(new ItemStack(Items.RAW_IRON));
        scene.idle(10);
        scene.world().createItemEntity(util.vector().of(2.5d, 4d, 1.5d), Vec3.ZERO, new ItemStack(Items.RAW_IRON));
        scene.idle(50);
        scene.world().flapFunnel(basin.west(), true);
        scene.world().createItemEntity(util.vector().of(0.25d, 2d, 1.5d), Vec3.ZERO, new ItemStack(Items.IRON_INGOT));
        scene.idle(10);
        scene.overlay().showControls(util.vector().blockSurface(basin.west(), Direction.WEST), Pointing.LEFT, 30).withItem(new ItemStack(Items.IRON_INGOT));
        scene.idle(50);

        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .independent()
            .colored(PonderPalette.RED);
        scene.idle(100);

        scene.markAsFinished();
    };
    
    public static void dynamoRedstone(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("dynamo.redstone", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos dynamo = util.grid().at(2, 2, 3);
        BlockPos redStoneDust = util.grid().at(2, 1, 3);

        scene.world().showSection(util.select().fromTo(0, 1, 0, 4, 2, 5).add(util.select().position(2, 0, 5)), Direction.UP);
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(dynamo, Direction.WEST));
        scene.idle(120);
        scene.world().setKineticSpeed(util.select().everywhere(), 256);
        scene.world().setKineticSpeed(util.select().position(2, 4, 2), -256); // Set the one cog which should be going the other way to the correct speed
        scene.effects().indicateRedstone(redStoneDust);
        scene.world().modifyBlock(redStoneDust, state -> state.setValue(BlockStateProperties.POWER, 7), false);
        scene.world().modifyBlockEntityNBT(util.select().position(2, 1, 1), NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 7));
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(dynamo, Direction.WEST))
            .attachKeyFrame();
        scene.idle(120);
        scene.markAsFinished();
    };

    public static void dynamoCharging(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("dynamo.charging", "This text is defined in a language file.");
		scene.configureBasePlate(0, 0, 5);
		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(5);

        BlockPos dynamo = util.grid().at(2, 3, 2);
        BlockPos depot = util.grid().at(2, 1, 1);
        Selection kinetics = util.select().fromTo(2, 3, 3, 2, 3, 5).add(util.select().fromTo(2, 0, 5, 2, 2, 5));

		ElementLink<WorldSectionElement> depotElement = scene.world().showIndependentSection(util.select().position(depot), Direction.DOWN);
		scene.world().moveSection(depotElement, util.vector().of(0, 0, 1), 0);
		scene.idle(10);

        scene.world().showSection(util.select().position(dynamo), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(kinetics, Direction.NORTH);
        scene.idle(10);

        Vec3 dynamoSide = util.vector().blockSurface(dynamo, Direction.WEST);
		scene.overlay().showText(60)
			.pointAt(dynamoSide)
			.placeNearTarget()
			.attachKeyFrame()
			.text("This text is defined in a language file.");
		scene.idle(70);
		scene.overlay().showText(60)
			.pointAt(dynamoSide.subtract(0, 2, 0))
			.placeNearTarget()
			.text("This text is defined in a language file.");
		scene.idle(50);
		ItemStack uncharged = AllItems.CINDER_FLOUR.asStack();
		scene.world().createItemOnBeltLike(depot, Direction.NORTH, uncharged);
		Vec3 depotCenter = util.vector().centerOf(depot.south());
		scene.overlay().showControls(depotCenter, Pointing.UP, 30).withItem(uncharged);
		scene.idle(10);

        scene.world().modifyBlockEntity(dynamo, DynamoBlockEntity.class, be -> 
            be.chargingBehaviour.start(ChargingBehaviour.Mode.BELT, util.vector().blockSurface(depot, Direction.UP), 240)
        );
        scene.idle(60);
        //TODO make dynamo actually render in ponder

        scene.world().modifyBlockEntity(dynamo, DynamoBlockEntity.class, be -> 
            be.chargingBehaviour.running = false
        );
        ItemStack charged = new ItemStack(Items.REDSTONE);
        scene.world().removeItemsFromBelt(depot);
		scene.world().createItemOnBeltLike(depot, Direction.UP, charged);
		scene.idle(10);
		scene.overlay().showControls(depotCenter, Pointing.UP, 50).withItem(charged);
		scene.idle(60);

        scene.markAsFinished();
    };

    public static void dynamoElectrolysis(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("dynamo.electrolysis", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();
        
        BlockPos basin = util.grid().at(1, 1, 1);
        BlockPos dynamo = util.grid().at(1, 3, 1);

        scene.idle(5);
        scene.world().showSection(util.select().position(basin), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(1, 0, 3, 1, 3, 3), Direction.NORTH);
        scene.idle(5);
        scene.world().showSection(util.select().position(1, 3, 2), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(dynamo), Direction.DOWN);
        scene.idle(5);

        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(dynamo, Direction.WEST));
        scene.idle(100);
        
        scene.markAsFinished();
    };

    
};
