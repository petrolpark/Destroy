package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.destroy.block.entity.LongShaftBlockEntity;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(RotationPropagator.class)
public class RotationPropagatorMixin {
    
    /**
     * Allow Kinetic Blocks to rotate the Gear end of a Long Shaft.
     * @param from
     * @param to
     * @param cir
     */
    @Inject(method = "getRotationSpeedModifier", at = @At(value = "TAIL"), cancellable = true)
    private static void inGetRotationSpeedModifier(KineticBlockEntity from, KineticBlockEntity to, CallbackInfoReturnable<Float> cir) {
        BlockState fromBlockState = from.getBlockState();
        BlockPos fromBlockPos = from.getBlockPos();
        if (to instanceof LongShaftBlockEntity) {
            Direction direction = LongShaftBlockEntity.getDirection(to.getBlockState());
            IRotate defFrom = (IRotate)fromBlockState.getBlock();
            if (
                to.getBlockPos().subtract(fromBlockPos).equals(BlockPos.ZERO.relative(direction.getOpposite(), 2)) // If the Long Shaft is in the right orientation and position
                && defFrom.hasShaftTowards(from.getLevel(), fromBlockPos, fromBlockState, direction.getOpposite()) // If this has a Shaft in the right direction
            ) {
                cir.setReturnValue(1f);
            };
        };
    };
};
