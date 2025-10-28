package com.petrolpark.destroy.mixin;

import java.util.Optional;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.petrolpark.destroy.DestroyFluids;
import com.petrolpark.destroy.mixin.accessor.FluidNetworkAccessor;
import com.petrolpark.destroy.mixin.accessor.PipeConnectionAccessor;
import com.simibubi.create.content.fluids.FluidNetwork;
import com.simibubi.create.content.fluids.PipeConnection;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

@Mixin(PipeConnection.class)
public class PipeConnectionMixin {
    static private ThreadLocal<Optional<FluidNetwork>> retainedNetwork = new ThreadLocal<>();

    @Inject(
        method="Lcom/simibubi/create/content/fluids/PipeConnection;manageFlows(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraftforge/fluids/FluidStack;Ljava/util/function/Predicate;)Z",
        at=@At("HEAD"),
        remap=false
    )
    private void onStartManageFlows(Level world, BlockPos pos, FluidStack internalFluid, Predicate<FluidStack> extractionPredicate, CallbackInfoReturnable<Boolean> cir) {
        retainedNetwork.set(((PipeConnectionAccessor)this).getNetwork());
    }

    /**
     * Usually if a Fluid Network is trying to pull from a Tank and is animating the Fluid moving through the Pipes, it will
     * reset if the Fluid Stack changes. This stops this behaviour if the Fluid being transferred happens to be a Mixture, as
     * these change a lot. We don't want to try restarting the flow every tick.
     */
    @WrapOperation(
        method="Lcom/simibubi/create/content/fluids/PipeConnection;manageFlows(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraftforge/fluids/FluidStack;Ljava/util/function/Predicate;)Z",
        at=@At(
                value="INVOKE",
                target="Lnet/minecraftforge/fluids/FluidStack;isFluidEqual(Lnet/minecraftforge/fluids/FluidStack;)Z"
        ),
        remap=false
    )
    private boolean considerMixturesEqual(FluidStack fluid, FluidStack other, Operation<Boolean> original) {
        if (original.call(fluid, other))
            return true;
        else if (DestroyFluids.isMixture(fluid) && DestroyFluids.isMixture(other)) {
            ((PipeConnectionAccessor)this).getFlow().get().fluid = fluid;
            if(retainedNetwork.get().isPresent()) ((FluidNetworkAccessor)(retainedNetwork.get()).get()).setFluid(fluid);
            return true;
        };

        return false;
    };
};
