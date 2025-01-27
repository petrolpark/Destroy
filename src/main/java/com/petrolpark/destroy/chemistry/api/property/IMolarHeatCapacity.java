package com.petrolpark.destroy.chemistry.api.property;

/**
 * An object of which instances have a molar heat capacity.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
@FunctionalInterface
public interface IMolarHeatCapacity {
    
    /**
     * The number of joules of heat flow required to raise the temperature of this substance by one kelvin.
     * @return A molar heat capacity in joules per mole-kelvin, greater than or equalt to {@code 0d}.
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    public double getMolarHeatCapacity();
};
