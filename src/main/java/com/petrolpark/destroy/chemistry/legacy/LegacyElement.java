package com.petrolpark.destroy.chemistry.legacy;

import java.util.function.Function;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.petrolpark.destroy.core.chemistry.MoleculeRenderer.Geometry;

/**
 * Something inbetween an actual element and a nuclide.
 * Different Elements (as this enum defines) have different properties - be those chemical or physical (in the case of the two commom Uranium isotopes).
 */
public enum LegacyElement {

    // In the order they should appear in Empirical Formulae
    R_GROUP("R", 0.0001f, 2.5f, new double[]{1, 2, 3}),
    CARBON("C", 12.01f, 2.5f, new double[]{4}),
    HYDROGEN("H", 1.01f, 2.1f, new double[]{1}),
    SULFUR("S", 32.07f, 2.5f, new double[]{2, 0, 4, 6}),
    NITROGEN("N", 14.01f, 3.0f, new double[]{3, 4}, ((i) -> {return i == 3 ? Geometry.TRIGONAL_PYRAMIDAL : null;})),
    OXYGEN("O", 16.00f, 3.5f, new double[]{0, 1.5d, 2}, ((i) -> {return i == 2 ? Geometry.V_SHAPE : null;})),
    BORON("B", 10.81f, 2.04f, new double[]{3d}),
    FLUORINE("F", 19.00f, 4.0f, new double[]{1}),
    SODIUM("Na", 23.00f, 0.9f, new double[]{1}),
    CHLORINE("Cl", 35.45f, 3.0f, new double[]{1}),
    POTASSIUM("K", 39.10f, 0.8f, new double[]{1}),
    CALCIUM("Ca", 40.08f, 1.0f, new double[]{2}),
    CHROMIUM("Cr", 52.00f, 1.66f, new double[]{2d, 3d, 6d}),
    IRON("Fe", 55.85f, 1.8f, new double[]{0, 2, 3}),
    NICKEL("Ni", 58.69f, 1.8f, new double[]{1}),
    COPPER("Cu", 63.55f, 1.9f, new double[]{1, 2}),
    ZINC("Zn", 65.38f, 1.6f, new double[]{1}),
    ZIRCONIUM("Zr", 91.22f, 1.4f, new double[]{1}),
    //RHODIUM("Rh", 102.91f, 2.2f),
    //PALLADIUM("Pd", 106.42f, 2.2f),
    IODINE("I", 126.90f, 2.7f, new double[]{1}),
    PLATINUM("Pt", 195.08f, 2.2f, new double[]{1}),
    GOLD("Au", 196.97f, 2.4f, new double[]{0, 4}),
    MERCURY("Hg", 200.59f, 1.9f, new double[]{2}),
    LEAD("Pb", 207.20f, 1.8f, new double[]{2, 4}),
    ARGON("Ar", 39.95f, 0f, new double[]{0})
    ;

    public final String symbol;
    public final Float mass;
    public final Float electronegativity;
    public final double[] valencies;

    private Function<Integer, Geometry> geometryOverride;

    private PartialModel partial;

    private LegacyElement(String symbol, Float mass, Float electronegativity, double[] valencies) {
        this(symbol, mass, electronegativity, valencies, null);
    };

    private LegacyElement(String symbol, Float mass, Float electronegativity, double[] valencies, Function<Integer, Geometry> geometryOverride) {
        this.symbol = symbol;
        this.mass = mass;
        this.electronegativity = electronegativity;
        this.valencies = valencies;
        this.geometryOverride = geometryOverride;
    };

    public String getSymbol() {
        return this.symbol;
    };

    public Float getMass() {
        return this.mass;
    };

    public Float getElectronegativity() {
        return this.electronegativity;
    };
;
    public Boolean isValidValency(double valency) {
        for (double possibleValency : valencies) {
            if (Math.abs(possibleValency - valency) < 0.000001d) return true;
        };
        return false;
    };

    public double getNextLowestValency(double valency) {
        for (double validValency : valencies) {
            if (validValency >= valency) return validValency;
        };
        return 0;
    };

    public double getMaxValency() {
        double currentMax = valencies[0];
        for (double valency : valencies) {
            if (valency > currentMax) valency = currentMax;
        };
        return currentMax; 
    };

    public static LegacyElement fromSymbol(String symbol) {
        for (LegacyElement element : values()) {
            if (element.symbol.equals(symbol)) return element;
        };
        throw new EnumConstantNotPresentException(LegacyElement.class, "Unknown Element of symbol "+symbol);
    };

    public Geometry getGeometry(int connections) {
        Geometry geometry = null;
        if (geometryOverride != null) geometry = geometryOverride.apply(connections);
        if (geometry != null) return geometry;
        switch (connections) {
            case 0:
            case 1:
            case 2:
                return Geometry.LINEAR;
            case 3:
                return Geometry.TRIGONAL_PLANAR;
            case 4:
                return Geometry.TETRAHEDRAL;
            case 5:
                // TODO add trigonal bipyramidal geometry
            case 6:
                return Geometry.OCTAHEDRAL;
        };
        return Geometry.OCTAHEDRAL;
    };

    public PartialModel getPartial() {
        return partial;
    };

    public void setPartial(PartialModel partial) {
        this.partial = partial;
    };
}
