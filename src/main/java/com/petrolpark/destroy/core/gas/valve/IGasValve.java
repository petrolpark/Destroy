package com.petrolpark.destroy.core.gas.valve;

/**
 * @since Destroy 0.1.2
 * @author petrolpark
 */
public interface IGasValve {
    
    public double getPressureGradient(double inletPressure, double outletPressure);
};
