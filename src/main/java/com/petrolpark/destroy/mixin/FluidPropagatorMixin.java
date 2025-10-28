package com.petrolpark.destroy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FluidPropagator.class)
public class FluidPropagatorMixin {
    @WrapOperation(
        method="Lcom/simibubi/create/content/fluids/FluidPropagator;propagateChangedPipe(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
        at=@At(
            value="INVOKE",
            target="Lcom/tterrag/registrate/util/entry/BlockEntry;has(Lnet/minecraft/world/level/block/state/BlockState;)Z",
            remap=false
        ),
        remap=false
    )
    private static boolean matchOtherPumps(BlockEntry<?> instance, BlockState state, Operation<Boolean> original) {
        return (state.getBlock() instanceof PumpBlock) || original.call(instance, state);
    }
}
