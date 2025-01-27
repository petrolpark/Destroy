package com.petrolpark.destroy.chemistry.api.property;

/**
 * {@inheritDoc}
 * Something which can be heated, often resulting in a change in its {@link ITemperature homgenous temperature}.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
@FunctionalInterface
public interface IHeatable {
    
    /**
     * 
     * @param energyDensity The heat flow into this {@link IHeatable}, in joules per litre. Can be positive or negative
     */
    public void heat(double energyDensity);
};
