package com.petrolpark.destroy.mixin;

import java.util.Optional;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.mixin.accessor.FluidNetworkAccessor;
import com.petrolpark.destroy.mixin.accessor.PipeConnectionAccessor;
import com.simibubi.create.content.fluids.FlowSource;
import com.simibubi.create.content.fluids.FluidNetwork;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.content.fluids.PipeConnection.Flow;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

@Mixin(PipeConnection.class)
public class PipeConnectionMixin {
  
    /**
     * Usually if a Fluid Network is trying to pull from a Tank and is animating the Fluid moving through the Pipes, it will 
     * reset if the Fluid Stack changes. This stops this behaviour if the Fluid being transferred happens to be a Mixture, as
     * these change a lot. We don't want to try restarting the flow every tick.
     * @param world
     * @param pos
     * @param internalFluid
     * @param extractionPredicate
     * @param cir
     */
    @Inject(
        method = "manageFlows(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraftforge/fluids/FluidStack;Ljava/util/function/Predicate;)Z",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Optional;empty()V",
            ordinal = 1
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    public void inManageFlows(Level world, BlockPos pos, FluidStack internalFluid, Predicate<FluidStack> extractionPredicate, CallbackInfoReturnable<Boolean> cir, Optional<FluidNetwork> retainedNetwork) {
        Flow flow = ((PipeConnectionAccessor)this).getFlow().get();
        FlowSource source = ((PipeConnectionAccessor)this).getSource().get();

        FluidStack provided = flow.inbound ? source.provideFluid(extractionPredicate) : internalFluid;
        if (((PipeConnection)(Object)this).hasPressure() && DestroyFluids.isMixture(provided) && DestroyFluids.isMixture(flow.fluid)) { // Only update the Fluid if we have Fluid and should be moving it
            flow.fluid = provided;
            Optional<FluidNetwork> network = retainedNetwork;
            if (network.isPresent()) {
                ((FluidNetworkAccessor)network.get()).setFluid(provided.copy());
                ((PipeConnectionAccessor)this).setNetwork(network);
            };

            ((PipeConnection)(Object)this).manageFlows(world, pos, internalFluid, extractionPredicate);
            
            cir.setReturnValue(true); // Let the client know an update has occured
            cir.cancel();
        };
    };
};
