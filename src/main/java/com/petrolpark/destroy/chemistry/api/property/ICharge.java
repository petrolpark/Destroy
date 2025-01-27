package com.petrolpark.destroy.chemistry.api.property;

/**
 * An object of which instances can have charge.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
@FunctionalInterface
public interface ICharge {
    
    /**
     * Get the charge of this, relative to a proton.
     * For example, electrons would return {@code -1d}, and neutrons {@code 0d}.
     * @return A positive or negative or zero double
     */
    public double getCharge();
};
