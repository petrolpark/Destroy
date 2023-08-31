package com.petrolpark.destroy.chemistry;

import com.jozufozu.flywheel.core.PartialModel;

/**
 * Something inbetween an actual element and a nuclide.
 * Different Elements (as this enum defines) have different properties - be those chemical or physical (in the case of the two commom Uranium isotopes).
 */
public enum Element {

    //TODO replace 'valencies' with a BiPredicate of bonds and charge

    // In the order they should appear in Empirical Formulae
    R_GROUP("R", 0f, 2.5f, 1),
    CARBON("C", 12.01f, 2.5f, 4),
    HYDROGEN("H", 1.01f, 2.1f, 1),
    SULFUR("S", 32.07f, 2.5f, 2, 0, 4, 6),
    OXYGEN("O", 16.00f, 3.5f, 2),
    NITROGEN("N", 14.01f, 3.0f, 3, 5),
    FLUORINE("F", 19.00f, 4.0f, 1),
    SODIUM("Na", 23.00f, 0.9f, 1),
    CHLORINE("Cl", 35.45f, 3.0f, 1),
    POTASSIUM("K", 39.10f, 0.8f, 1),
    CALCIUM("Ca", 40.08f, 1.0f, 2),
    IRON("Fe", 55.85f, 1.8f, 0, 2, 3),
    NICKEL("Ni", 58.69f, 1.8f),
    COPPER("Cu", 63.55f, 1.9f, 1, 2),
    ZINC("Zn", 65.38f, 1.6f),
    ZIRCONIUM("Zr", 91.22f, 1.4f),
    //RHODIUM("Rh", 102.91f, 2.2f),
    //PALLADIUM("Pd", 106.42f, 2.2f),
    IODINE("I", 126.90f, 2.7f),
    PLATINUM("Pt", 195.08f, 2.2f),
    GOLD("Au", 196.97f, 2.4f, 0, 4),
    MERCURY("Hg", 200.59f, 1.9f, 2),
    //LEAD("Pb", 207.20f, 1.8f)
    ;

    private String symbol;
    private Float mass;
    private Float electronegativity;
    private int[] valencies;

    private PartialModel partial;

    private Element(String symbol, Float mass, Float electronegativity, int... valencies) {
        this.symbol = symbol;
        this.mass = mass;
        this.electronegativity = electronegativity;
        this.valencies = valencies;
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
    public Boolean isValidValency(int valency) {
        for (int possibleValency : valencies) {
            if (possibleValency == valency) return true;
        };
        return false;
    };

    public int getNextLowestValency(int valency) {
        for (int validValency : valencies) {
            if (validValency >= valency) return validValency;
        };
        return 0;
    };

    public int getMaxValency() {
        int currentMax = valencies[0];
        for (int valency : valencies) {
            if (valency > currentMax) valency = currentMax;
        };
        return currentMax; 
    };

    public static Element fromSymbol(String symbol) {
        for (Element element : values()) {
            if (element.symbol.equals(symbol)) return element;
        };
        throw new EnumConstantNotPresentException(Element.class, "Unknown Element of symbol "+symbol);
    };

    public PartialModel getPartial() {
        return partial;
    };

    public void setPartial(PartialModel partial) {
        this.partial = partial;
    };
}
