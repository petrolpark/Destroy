package com.petrolpark.destroy.core.explosion;

import java.util.function.Supplier;

import com.petrolpark.client.ponder.particle.PetrolparkEmitters;
import com.petrolpark.compat.CompatMods;
import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.DestroyItems;
import com.petrolpark.destroy.core.explosion.mixedexplosive.MixedExplosiveBlockEntity;
import com.petrolpark.destroy.core.explosion.mixedexplosive.IDyeableMixedExplosiveBlockEntity;
import com.simibubi.create.AllBlocks;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.element.InputWindowElement;
import net.createmod.ponder.foundation.instruction.FadeOutOfSceneInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class ExplosivesPonderScenes {
    
    private static final BlockPos craftingTable = new BlockPos(2, 1, 4);
    private static final BlockPos anvil = new BlockPos(2, 1, 0);
    private static final BlockPos first = new BlockPos(1, 1, 2);
    private static final BlockPos second = new BlockPos(3, 1, 2);

    public static void filling(SceneBuilder scene, SceneBuildingUtil util, Supplier<ItemStack> stack) {
        scene.title("explosives.filling", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        scene.idle(5);
        scene.world().showSection(util.select().position(craftingTable), Direction.DOWN);
        scene.idle(5);
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(craftingTable, Direction.WEST));
        scene.idle(10);
        scene.overlay().showControls(util.vector().topOf(craftingTable), Pointing.DOWN, 40).withItem(stack.get());
        scene.idle(50);
        scene.overlay().showControls(util.vector().topOf(craftingTable), Pointing.DOWN, 40).withItem(DestroyItems.CORDITE.asStack());
        scene.idle(60);

        scene.world().showSection(util.select().position(first), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(100)
            .text("This text is defined in a language file")
            .pointAt(util.vector().blockSurface(first, Direction.WEST))
            .attachKeyFrame();
        scene.idle(20);
        ElementLink<WorldSectionElement> funnel = scene.world().showIndependentSection(util.select().position(first.above()), Direction.DOWN);
        scene.idle(20);
        ElementLink<EntityElement> itemEntity = scene.world().createItemEntity(util.vector().centerOf(first.above(3)), Vec3.ZERO, DestroyItems.PICRIC_ACID_TABLET.asStack());
        scene.idle(30);
        scene.world().modifyEntity(itemEntity, Entity::kill);
        scene.idle(30);
        scene.world().hideIndependentSection(funnel, Direction.UP);
        scene.idle(20);

        scene.overlay().showText(100)
            .text("This text is defined in a language file")
            .pointAt(util.vector().blockSurface(first, Direction.WEST))
            .attachKeyFrame();
        scene.idle(20);
        scene.overlay().showControls(util.vector().topOf(first), Pointing.DOWN, 80).rightClick();
        scene.idle(100);

        scene.world().showSection(util.select().position(second), Direction.DOWN);
        scene.overlay().showText(170)
            .text("This text is defined in a language file.")
            .independent()
            .attachKeyFrame();
        scene.idle(20);
        scene.overlay().showControls(util.vector().topOf(first), Pointing.DOWN, 40).rightClick().withItem(AllBlocks.CLIPBOARD.asStack());
        scene.idle(50);
        scene.overlay().showControls(util.vector().topOf(second), Pointing.DOWN, 40).leftClick().withItem(AllBlocks.CLIPBOARD.asStack());
        scene.idle(50);
        scene.overlay().showControls(util.vector().topOf(second), Pointing.DOWN, 20).withItem(DestroyItems.CORDITE.asStack());
        scene.idle(30);
        scene.overlay().showControls(util.vector().topOf(second), Pointing.DOWN, 20).withItem(DestroyItems.PICRIC_ACID_TABLET.asStack());
        scene.idle(40);
        
        scene.markAsFinished();
    };
    
    private static ItemStack fireworkStar = ItemStack.EMPTY;
    private static ItemStack getFireworkStar() {
        if (fireworkStar.isEmpty()) {
            fireworkStar = new ItemStack(Items.FIREWORK_STAR);
            CompoundTag explosionTag = new CompoundTag();
            explosionTag.putIntArray("Colors", new int[]{4312372});
            fireworkStar.addTagElement("Explosion", explosionTag);
        };
        return fireworkStar;
    };
    
    public static void exploding(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("explosives.exploding", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        scene.idle(10);
        ElementLink<WorldSectionElement> bomb = scene.world().showIndependentSection(util.select().position(second), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(second, Direction.WEST));
        scene.idle(40);
        scene.overlay().showControls(util.vector().topOf(second), Pointing.DOWN, 40).withItem(getFireworkStar());
        scene.idle(80);

        scene.world().showSection(util.select().fromTo(0, 1, 1, 2, 1, 3), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .independent();
        scene.idle(60);
        scene.effects().emitParticles(util.vector().centerOf(second), PetrolparkEmitters.fireworkBall(0.25f, 4, new int[]{0xFF41CD34}, new int[0], false, false), 1f, 1);
        scene.addInstruction(new FadeOutOfSceneInstruction<>(0, Direction.DOWN, bomb));
        scene.idle(5);
        scene.world().destroyBlock(util.grid().at(2, 1, 2));
        scene.world().destroyBlock(first);
        scene.world().createItemEntity(util.vector().centerOf(util.grid().at(2, 1, 2)), util.vector().of(0d, 0.3d, -0.1d), new ItemStack(Blocks.GLASS));
        scene.idle(55);

        scene.world().hideSection(util.select().position(1, 1, 1).add(util.select().position(0, 1, 2)).add(util.select().position(1, 1, 3)), Direction.UP);
        scene.idle(20);
        ElementLink<WorldSectionElement> coal = scene.world().showIndependentSection(util.select().position(first.above()), Direction.DOWN);
        scene.world().moveSection(coal, util.vector().of(0d, -1d, 0d), 0);
        bomb = scene.world().showIndependentSection(util.select().position(second), Direction.DOWN);
        scene.idle(20);

        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(first, Direction.WEST));
        scene.idle(40);
        scene.effects().emitParticles(util.vector().centerOf(second), PetrolparkEmitters.fireworkBall(0.25f, 4, new int[]{0xFF41CD34}, new int[0], false, false), 1f, 1);
        scene.addInstruction(new FadeOutOfSceneInstruction<>(0, Direction.DOWN, bomb));
        scene.idle(5);
        scene.world().modifyEntities(ItemEntity.class, Entity::kill);
        scene.addInstruction(new FadeOutOfSceneInstruction<>(0, Direction.DOWN, coal));
        scene.world().createItemEntity(util.vector().centerOf(first), util.vector().of(0.05d, 0.3d, 0.1d), DestroyItems.NANODIAMONDS.asStack());
        scene.idle(75);

        scene.markAsFinished();
    };
    
    private static final int red = 14128786;
    private static final int purple = 10912960;

    public static void dyeing(SceneBuilder scene, SceneBuildingUtil util, Supplier<ItemStack> stack) {
        scene.title("explosives.dyeing", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        scene.idle(5);
        scene.world().showSection(util.select().position(craftingTable), Direction.DOWN);
        scene.idle(5);
        scene.overlay().showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(craftingTable, Direction.WEST));
        scene.idle(10);
        scene.overlay().showControls(util.vector().topOf(craftingTable), Pointing.DOWN, 40).withItem(stack.get());
        scene.idle(50);
        scene.overlay().showControls(util.vector().topOf(craftingTable), Pointing.DOWN, 40).withItem(new ItemStack(Items.RED_DYE));
        scene.idle(60);

        scene.world().modifyBlockEntity(first, BlockEntity.class, be -> ((IDyeableMixedExplosiveBlockEntity)be).setColor(red));
        scene.world().showSection(util.select().position(first), Direction.DOWN);
        scene.idle(20);

        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(first, Direction.WEST));
        scene.idle(40);
        scene.overlay().showControls(util.vector().topOf(first), Pointing.DOWN, 40).rightClick().withItem(new ItemStack(Items.BLUE_DYE));
        scene.idle(5);
        scene.world().modifyBlockEntity(first, BlockEntity.class, be -> ((IDyeableMixedExplosiveBlockEntity)be).setColor(purple));
        scene.addInstruction(s -> s.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw));
        scene.idle(55);

        scene.world().showSection(util.select().position(second), Direction.DOWN);
        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .independent()
            .attachKeyFrame();
        scene.idle(20);
        scene.overlay().showControls(util.vector().topOf(first), Pointing.DOWN, 40).rightClick().withItem(AllBlocks.CLIPBOARD.asStack());
        scene.idle(50);
        scene.overlay().showControls(util.vector().topOf(second), Pointing.DOWN, 40).leftClick().withItem(AllBlocks.CLIPBOARD.asStack());
        scene.idle(5);
        scene.world().modifyBlockEntity(second, BlockEntity.class, be -> ((IDyeableMixedExplosiveBlockEntity)be).setColor(purple));
        scene.addInstruction(s -> s.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw));
        scene.idle(25);

        scene.markAsFinished();
    };

    public static void naming(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("explosives.naming", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        scene.idle(5);
        scene.world().showSection(util.select().position(anvil), Direction.DOWN);
        scene.idle(5);
        scene.overlay().showText(120)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(anvil, Direction.WEST));
        scene.idle(10);
        scene.overlay().showControls(util.vector().topOf(anvil), Pointing.DOWN, 40).withItem(DestroyBlocks.CUSTOM_EXPLOSIVE_MIX.asStack());
        scene.idle(70);
        ItemStack namedStack = DestroyBlocks.CUSTOM_EXPLOSIVE_MIX.asStack();
        Component name = Component.literal("TNX");
        namedStack.setHoverName(name);
        scene.overlay().showControls(util.vector().topOf(anvil), Pointing.DOWN, 40).withItem(namedStack);
        scene.idle(60);

        scene.world().modifyBlockEntity(first, MixedExplosiveBlockEntity.class, be -> be.setCustomName(name));
        scene.world().showSection(util.select().position(first), Direction.DOWN);
        scene.idle(20);

        scene.overlay().showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector().blockSurface(first, Direction.WEST));
        scene.idle(100);

        scene.world().showSection(util.select().position(second), Direction.DOWN);
        scene.overlay().showText(110)
            .text("This text is defined in a language file.")
            .independent()
            .attachKeyFrame();
        scene.idle(20);
        scene.overlay().showControls(util.vector().topOf(first), Pointing.DOWN, 40).rightClick().withItem(AllBlocks.CLIPBOARD.asStack());
        scene.idle(50);
        scene.overlay().showControls(util.vector().topOf(second), Pointing.DOWN, 40).leftClick().withItem(AllBlocks.CLIPBOARD.asStack());
        scene.idle(5);
        scene.world().modifyBlockEntity(second, MixedExplosiveBlockEntity.class, be -> be.setCustomName(name));
        scene.idle(55);

        if (CompatMods.BIG_CANNONS.isLoaded()) {
            scene.overlay().showText(100)
                .text("This text is defined in a language file.")
                .colored(PonderPalette.RED)
                .independent();
            scene.idle(120);
        };

        scene.markAsFinished();
    };
};
