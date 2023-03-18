package com.petrolpark.destroy.client.ponder;

import java.util.List;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.entity.CentrifugeBlockEntity;
import com.petrolpark.destroy.item.DestroyItems;
import com.simibubi.create.content.contraptions.fluids.potion.PotionFluid;
import com.simibubi.create.content.contraptions.fluids.tank.FluidTankTileEntity;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.ponder.instruction.EmitParticlesInstruction.Emitter;
import com.simibubi.create.foundation.utility.Pointing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class DestroyScenes {

    public static void centrifuge(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("centrifuge", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        // Define Selections
        Selection pipework = util.select.fromTo(2, 1, 1, 2, 5, 3);
        Selection kinetics = util.select.fromTo(1, 1, 2, 1, 3, 5)
            .add(util.select.fromTo(2, 1, 4, 2, 5, 4))
            .add(util.select.position(2, 0, 5));
        BlockPos centrifuge = new BlockPos(2, 3, 3);
        BlockPos denseOutputPump = new BlockPos(2, 3, 2);
        BlockPos lightOutputPump = new BlockPos(2, 2, 3);

        // Define coloured Fluids
        FluidStack purpleFluid, blueFluid, redFluid;
        purpleFluid = new FluidStack(PotionFluid.withEffects(2000, new Potion(), List.of(new MobEffectInstance(MobEffects.REGENERATION))), 2000);
        blueFluid = new FluidStack(PotionFluid.withEffects(1000, new Potion(), List.of(new MobEffectInstance(MobEffects.NIGHT_VISION))), 1000);
        redFluid = new FluidStack(PotionFluid.withEffects(1000, new Potion(), List.of(new MobEffectInstance(MobEffects.DAMAGE_BOOST))), 1000);

        // Pre-fill the input Tank
        scene.world.modifyTileEntity(new BlockPos(2, 5, 3), FluidTankTileEntity.class, te -> {
            te.getTankInventory().fill(purpleFluid, FluidAction.EXECUTE);
        });
        // Ensure the Centrifuge faces the right way
        scene.world.modifyTileEntity(centrifuge, CentrifugeBlockEntity.class, be -> {
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
            .pointAt(util.vector.blockSurface(centrifuge, Direction.EAST));
        scene.world.propagatePipeChange(new BlockPos(2, 4, 3));
        scene.idle(120);
        scene.world.modifyTileEntity(centrifuge, CentrifugeBlockEntity.class, te -> {
            te.getInputTank().drain(4000, FluidAction.EXECUTE);
            te.sendData();
        });
        scene.world.modifyTileEntity(centrifuge, CentrifugeBlockEntity.class, te -> te.getDenseOutputTank().fill(blueFluid, FluidAction.EXECUTE));
        scene.world.propagatePipeChange(denseOutputPump);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(denseOutputPump, Direction.EAST));
        scene.idle(120);
        scene.world.modifyTileEntity(centrifuge, CentrifugeBlockEntity.class, te -> te.getLightOutputTank().fill(redFluid, FluidAction.EXECUTE));
        scene.world.propagatePipeChange(lightOutputPump);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(lightOutputPump, Direction.EAST));
        scene.idle(120);
        scene.markAsFinished();

        // TODO lubrication
    };

    public static void phytomining(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("phytomining", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();

        BlockPos ore = new BlockPos(1, 1, 1);
        BlockPos farmland = new BlockPos(1, 2, 1);

        scene.world.showSection(util.select.everywhere(), Direction.UP);
        scene.idle(10);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(farmland, Direction.UP));
        scene.idle(120);
        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(new BlockPos(1, 2, 1), Direction.UP), Pointing.DOWN)
            .rightClick()
            .withItem(new ItemStack(DestroyItems.HYPERACCUMULATING_FERTILIZER.get())),
            30
        );
        scene.idle(60);
        scene.effects.emitParticles(util.vector.topOf(farmland).add(0, 0.25f, 0), Emitter.withinBlockSpace(ParticleTypes.HAPPY_VILLAGER, Vec3.ZERO), 1.0f, 15);
        scene.world.modifyBlock(ore, s -> Blocks.STONE.defaultBlockState(), false);
        scene.world.modifyBlock(new BlockPos(1, 3, 1), s -> DestroyBlocks.GOLDEN_CARROTS.get().defaultBlockState(), false);
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(ore, Direction.WEST));
        scene.idle(120);
        scene.markAsFinished();
    };

};
