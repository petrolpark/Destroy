package com.petrolpark.destroy.mixin;

import java.util.Iterator;

import net.createmod.catnip.math.BlockFace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.destroy.DestroyFluids;
import com.petrolpark.destroy.mixin.accessor.FluidNetworkAccessor;
import com.simibubi.create.content.fluids.FluidNetwork;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.content.fluids.PipeConnection.Flow;
import net.createmod.catnip.data.Pair;

import net.minecraftforge.fluids.FluidStack;

@Mixin(FluidNetwork.class)
public class FluidNetworkMixin {
    
    /**
     * Similar to {@link com.petrolpark.destroy.mixin.PipeConnectionMixin here}, we don't want a Fluid Network to reset transferring Fluid if all that has changed
     * is one Mixture becoming another.
     */
    @Inject(
        method = "Lcom/simibubi/create/content/fluids/FluidNetwork;tick()V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Iterator;remove()V",
            ordinal = 1
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
        
    )
    public void inTick(CallbackInfo ci, int cycle, boolean shouldContinue, Iterator<Pair<BlockFace, PipeConnection>> iterator, Pair<BlockFace, PipeConnection> pair, BlockFace blockFace, PipeConnection pipeConnection, Flow flow) {
        FluidStack fluid = ((FluidNetworkAccessor)this).getFluid();
        if (DestroyFluids.isMixture(fluid) && DestroyFluids.isMixture(flow.fluid)) {
            ((FluidNetworkAccessor)this).setFluid(flow.fluid);
            ci.cancel();
        };
    };
};
