package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.simibubi.create.content.fluids.pipes.VanillaFluidTargets;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

@Mixin(VanillaFluidTargets.class)
public class VanillaFluidTargetsMixin {
    
    /**
     * Injection into the {@link com.simibubi.create.content.contraptions.fluids.pipes.VanillaFluidTargets#drainBlock Create method} which can drain non-Block-Entity-having Block States.
     */
    @Inject(method = "drainBlock", at = @At(value="HEAD"), cancellable = true)
    private static void inDrainBlock(Level level, BlockPos pos, BlockState state, boolean simulate, CallbackInfoReturnable<FluidStack> ci) {
        if (state.getBlock() == DestroyBlocks.URINE_CAULDRON.get()) {
            if (!simulate) level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
            ci.setReturnValue(new FluidStack(DestroyFluids.URINE.get().getSource(), 250));
        };
    };
};
