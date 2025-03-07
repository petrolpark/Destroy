package com.petrolpark.destroy;

import com.petrolpark.destroy.content.processing.trypolithography.CircuitPatternItemAttribute;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import net.minecraft.core.Registry;

public abstract class DestroyItemAttributeTypes {

    public static final ItemAttributeType isCircuitPatternPunched = Registry.register(CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE, Destroy.asResource("is_circuit_pattern_punched"), new CircuitPatternItemAttribute.Type());

    public static void init() {}
};
