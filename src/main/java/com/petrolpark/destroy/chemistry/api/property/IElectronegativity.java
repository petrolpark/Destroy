package com.petrolpark.destroy.chemistry.api.property;

/**
 * An object of which instances can have an electronegativity.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
@FunctionalInterface
public interface IElectronegativity {
    
    /**
     * Get the Pauling electronegativity of this object.
     * @return A float which should but may not be greater than {@code 0f}.
     */
    public float getElectronegativity();
};
