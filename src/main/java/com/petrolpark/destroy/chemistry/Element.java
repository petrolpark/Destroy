package com.petrolpark.destroy.chemistry;

public enum Element {

    //in the order they should appear in Empirical Formulae
    R_GROUP("R", 0f, 1),
    CARBON("C", 12.01f, 4),
    HYDROGEN("H", 1.01f, 1),
    OXYGEN("O", 16.00f, 2),
    NITROGEN("N", 14.01f, 3, 5),
    FLUORINE("F", 19.00f, 1),
    SODIUM("Na", 23.00f, 1),
    SULFUR("S", 32.07f, 2, 0, 4, 6),
    CHLORINE("Cl", 35.45f, 1),
    POTASSIUM("K", 39.10f, 1),
    CALCIUM("Ca", 40.08f, 2),
    IRON("Fe", 55.85f, 0, 2, 3),
    NICKEL("Ni", 58.69f),
    COPPER("Cu", 63.55f, 1, 2),
    ZINC("Zn", 65.38f),
    ZIRCONIUM("Zr", 91.22f),
    RHODIUM("Rh", 102.91f),
    PALLADIUM("Pd", 106.42f),
    PLATINUM("Pt", 195.08f),
    GOLD("Au", 196.97f),
    MERCURY("Hg", 200.59f),
    LEAD("Pb", 207.20f)
    ;

    private String symbol;
    private float mass;
    private int[] valencies;

    private Element(String symbol, float mass) {
        this(symbol, mass, 0);
    };

    private Element(String symbol, float mass, int... valencies) {
        this.symbol = symbol;
        this.mass = mass;
        this.valencies = valencies;
    };

    public String getSymbol() {
        return this.symbol;
    };

    public float getMass() {
        return this.mass;
    };

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
        throw new Error("Unknown Element of symbol "+symbol);
    };
}
