package com.petrolpark.destroy.registrate;

import com.petrolpark.destroy.badge.Badge;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.VirtualFluidBuilder;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class DestroyRegistrate extends CreateRegistrate {

    public DestroyRegistrate(String modid) {
        super(modid);
    };

    public <T extends Badge> BadgeBuilder<Badge, DestroyRegistrate> badge(String name, NonNullSupplier<T> factory) {
		return entry(name,
			c -> new BadgeBuilder<>(self(), self(), name, c, new ResourceLocation(getModid(), name),
				new ResourceLocation(getModid(), "fluid/" + name + "_flow"), factory));
	};
    
};
