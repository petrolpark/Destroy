package com.petrolpark.destroy.chemistry.api.property;

/**
 * An object of which instances have a single, homogenous or average temperature.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
@FunctionalInterface
public interface ITemperature {
    
    /**
     * Get the temperature of this object in kelvins.
     * @return A double which should be <b>strictly greater than {@code 0d}</b>
     */
    public double getTemperature();
};
