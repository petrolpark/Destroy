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
  
    @Accessor("flow")
    public Optional<Flow> getFlow();

    @Accessor("flow")
    public void setFlow(Optional<Flow> flow);

    @Accessor("source")
    public Optional<FlowSource> getSource();

    @Accessor("network")
    public Optional<FluidNetwork> getNetwork();

    @Accessor("network")
    public void setNetwork(Optional<FluidNetwork> network);
};
