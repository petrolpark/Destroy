package com.petrolpark.destroy.core.gas.valve;

import java.util.stream.Stream;

public class OrValve implements IGasValve {

    protected final IGasValve[] valves;

    protected OrValve(IGasValve[] valves) {
        this.valves = valves;
    };

    public static IGasValve or(IGasValve... valves) {
        return new OrValve(Stream.of(valves).flatMap(valve -> {
            if (valve instanceof OrValve orValve) return Stream.of(orValve.valves);
            return Stream.of(valve);
        }).toArray(s -> new IGasValve[s]));
    };

    @Override
    public double getPressureGradient(double inletPressure, double outletPressure) {
        return Stream.of(valves).mapToDouble(valve -> valve.getPressureGradient(inletPressure, outletPressure)).sum();
    };
    
};
