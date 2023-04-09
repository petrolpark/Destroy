package com.petrolpark.destroy.chemistry;

/**
 * A specific Atom in a specific {@link Molecule}.
 */
public class Atom {
    /**
     * The {@link Element specific isotope} of this Atom.
     */
    private Element element;

    /**
     * The pKa of the dissociation of this proton.
     * If this Atom is not an acidic proton, then this will be 0.
     */
    private float pKa;

    /**
     * A specific Atom in a specific {@link Molecule}.
     * @param element The {@link Element specific isotope} of this Atom.
     */
    public Atom(Element element) {
        this.element = element;
        this.pKa = 0f;
    };

    /**
     * The {@link Element specific isotope} of this Atom.
     */
    public Element getElement() {
        return this.element;
    };

    /**
     * Turns this Atom into an acidic proton (if the Atom already has an {@link Element}, this is disregarded).
     * @param pKa The pKa of the dissociation of this proton
     * @return This Atom
     */
    public Atom setpKa(float pKa) {
        if (pKa == 0f) return this;
        element = Element.HYDROGEN;
        this.pKa = pKa;
        return this;
    };

    /**
     * Whether this Atom is a proton and can dissociate (i.e. its {@link Atom#pKa pKa} is 0).
     */
    public Boolean isAcidicProton() {
        return this.pKa != 0f;
    };

    /**
     * Whether this is Atom is both {@link Element#HYDROGEN Hydrogen} and not {@link Atom#isAcidicProton acidic}.
     */
    public Boolean isNonAcidicHydrogen() {
        return !isAcidicProton() && element == Element.HYDROGEN;
    };

    /**
     * The pKa of the dissociation of this proton.
     * If this Atom is not an acidic proton, then this will be 0.
     */
    public float getpKa() {
        return pKa;
    };
}
