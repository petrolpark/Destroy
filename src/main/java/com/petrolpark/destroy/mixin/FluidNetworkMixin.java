package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.petrolpark.destroy.DestroyFluids;
import com.simibubi.create.content.fluids.FluidNetwork;

import net.minecraftforge.fluids.FluidStack;

@Mixin(FluidNetwork.class)
public class FluidNetworkMixin {
    @WrapOperation(
            method="Lcom/simibubi/create/content/fluids/FluidNetwork;tick()V",
            at=@At(
                    value="INVOKE",
                    ordinal=0,
                    target="Lnet/minecraftforge/fluids/FluidStack;isFluidEqual(Lnet/minecraftforge/fluids/FluidStack;)Z"
            ),
            remap=false
    )
    private boolean considerMixturesEqual(FluidStack fluid, FluidStack other, Operation<Boolean> original) {
        if(original.call(fluid, other))
            return true;
        else if(DestroyFluids.isMixture(fluid) && DestroyFluids.isMixture(other)) {
            return true;
        }

        return false;
    }
};
