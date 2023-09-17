package com.petrolpark.destroy.chemistry;

import com.jozufozu.flywheel.core.PartialModel;
import com.petrolpark.destroy.block.model.DestroyPartials;

/**
 * A specific Atom in a specific {@link Molecule}.
 * Atoms can be rearranged and added to {@link Formula Formulae}, but <strong>never modify Atoms themselves</strong>.
 * Also note that {@link Element#HYDROGEN Hydrogen} Atoms are not conserved, so the Hydrogens
 * a Molecule is created with will not necessarily be carried over when accessed from different points.
 */
public class Atom {
    /**
     * The {@link Element specific isotope} of this Atom.
     */
    private final Element element;

    /**
     * If this 'Atom' is an {@link Element#R_GROUP R-Group} used to display a generic Reaction,
     * this number indicates which one it is.
     */
    public int rGroupNumber;

    public Atom(Element element) {
        this.element = element;
    };

    /**
     * The {@link Element specific isotope} of this Atom.
     */
    public Element getElement() {
        return this.element;
    };

    public PartialModel getPartial() {
        if (element == Element.R_GROUP) {
            if (rGroupNumber < 10 && rGroupNumber >= 1) {
                return DestroyPartials.rGroups.get(rGroupNumber);
            };
            return DestroyPartials.R_GROUP;
        } else {
            return element.getPartial();
        }
    };

    /**
     * Whether this is Atom is a {@link Element#HYDROGEN Hydrogen}.
     */
    public Boolean isHydrogen() {
        return element == Element.HYDROGEN;
    };

};
