package com.petrolpark.destroy.chemistry;

public class Atom {
    private Element element;

    private float pKa;

    public Atom(Element element) {
        this.element = element;
        this.pKa = 0f;
    };

    public Element getElement() {
        return this.element;
    };

    /**
     * Turns this Atom into an acidic proton (if the Atom already has an Element, this is disregarded)
     * @param pKa
     * @return
     */
    public Atom setpKa(float pKa) {
        if (pKa == 0f) return this;
        element = Element.HYDROGEN;
        this.pKa = pKa;
        return this;
    };

    public Boolean isAcidicProton() {
        return this.pKa != 0f;
    };

    public Boolean isNonAcidicHydrogen() {
        return !isAcidicProton() && element == Element.HYDROGEN;
    };

    public float getpKa() {
        return pKa;
    };
}
