package com.petrolpark.destroy.ponder;

import java.util.List;

import com.petrolpark.destroy.block.entity.CentrifugeBlockEntity;
import com.simibubi.create.AllFluids;
import com.simibubi.create.content.contraptions.fluids.potion.PotionFluid;
import com.simibubi.create.content.contraptions.fluids.tank.FluidTankTileEntity;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class DestroyScenes {

    public static void centrifuge(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("centrifuge", "");
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
        scene.world.modifyTileEntity(new BlockPos(2, 5, 3), FluidTankTileEntity.class, te -> te.getTankInventory().fill(purpleFluid, FluidAction.EXECUTE));

        scene.world.showSection(util.select.fromTo(0, 0, 0, 4, 0, 4), Direction.UP);
        scene.world.showSection(pipework, Direction.DOWN);
        scene.idle(10);
        scene.rotateCameraY(90);
        scene.idle(10);
        scene.world.showSection(kinetics, Direction.NORTH);
        scene.idle(10);
        scene.overlay.showText(100)
            .text("")
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
            .text("")
            .pointAt(util.vector.blockSurface(denseOutputPump, Direction.EAST));
        scene.idle(120);
        scene.world.modifyTileEntity(centrifuge, CentrifugeBlockEntity.class, te -> te.getLightOutputTank().fill(redFluid, FluidAction.EXECUTE));
        scene.world.propagatePipeChange(lightOutputPump);
        scene.overlay.showText(100)
            .text("")
            .pointAt(util.vector.blockSurface(lightOutputPump, Direction.EAST));
        scene.idle(120);
        scene.markAsFinished();
    };

};
