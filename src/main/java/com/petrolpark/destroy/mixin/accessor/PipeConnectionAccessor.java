package com.petrolpark.destroy.mixin.accessor;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.content.fluids.FlowSource;
import com.simibubi.create.content.fluids.FluidNetwork;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.content.fluids.PipeConnection.Flow;

@Mixin(PipeConnection.class)
public interface PipeConnectionAccessor {
  
    @Accessor(
        value = "flow",
        remap = false
    )
    public Optional<Flow> getFlow();

    @Accessor(
        value = "flow",
        remap = false
    )
    public void setFlow(Optional<Flow> flow);

    @Accessor(
        value = "source",
        remap = false
    )
    public Optional<FlowSource> getSource();

    @Accessor(
        value = "network",
        remap = false
    )
    public Optional<FluidNetwork> getNetwork();

    @Accessor(
        value = "network",
        remap = false
    )
    public void setNetwork(Optional<FluidNetwork> network);
};
