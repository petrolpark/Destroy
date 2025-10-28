package com.petrolpark.destroy.core.gas.valve;

import java.util.stream.Stream;

public class AndValve implements IGasValve {

    protected final IGasValve[] valves;

    protected AndValve(IGasValve... valves) {
        this.valves = valves;
    };

    public static final IGasValve sequence(IGasValve ...valves) {
        return new AndValve(Stream.of(valves).flatMap(valve -> {
            if (valve instanceof AndValve andValve) return Stream.of(andValve.valves);
            return Stream.of(valve);
        }).toArray(s -> new IGasValve[s]));
    };

    @Override
    public double getPressureGradient(double inletPressure, double outletPressure) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPressureGradient'");
    };
    
};
