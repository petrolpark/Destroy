package com.petrolpark.destroy.chemistry.api.property;

/**
 * An object of which instances have a single, homogenous or average density.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
@FunctionalInterface
public interface IDensity {
    
    /**
     * Get the density of this object, in kilograms per cubic meter.
     * @return A density greater than or equal to {@code 0d}.
     */
    public double getDensity();
};
