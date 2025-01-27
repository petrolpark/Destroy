package com.petrolpark.destroy.chemistry.api.property;

import com.petrolpark.destroy.chemistry.api.util.Namespace;

/**
 * An object which has an associated {@link Namespace}.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
@FunctionalInterface
public interface INamespace {
    
    /**
     * Get the {@link Namespace} associated with this object - typically the platform or add-on which {@link com.petrolpark.destroy.chemistry.api.registry.IChemistryRegistry registered} {@link com.petrolpark.destroy.chemistry.api.registry.IRegisteredChemistryObject this}.
     * The {@link Namespace} of an object should never change under normal circumstances.
     */
    public Namespace getNamespace();
};
