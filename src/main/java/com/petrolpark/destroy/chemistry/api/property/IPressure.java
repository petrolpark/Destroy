package com.petrolpark.destroy.chemistry.api.property;

/**
 * An object of which instances have a single, homogenous or average pressure.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
@FunctionalInterface
public interface IPressure {
    
    /**
     * Get the pressure of this object in pascals.
     * @return A double greater than or equal to {@code 0d}.
     */
    public double getPressure();
};
