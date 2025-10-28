package com.petrolpark.destroy.chemistry.legacy;

import com.petrolpark.destroy.client.DestroyPartials;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

/**
 * A specific Atom in a specific {@link LegacySpecies}.
 * Atoms can be rearranged and added to {@link LegacyMolecularStructure Formulae}, but <strong>never modify Atoms themselves</strong>.
 * Also note that {@link LegacyElement#HYDROGEN Hydrogen} Atoms are not conserved, so the Hydrogens
 * a Molecule is created with will not necessarily be carried over when accessed from different points.
 */
public class LegacyAtom {
    /**
     * The {@link LegacyElement specific isotope} of this Atom.
     */
    private final LegacyElement element;

    /**
     * If this 'Atom' is an {@link LegacyElement#R_GROUP R-Group} used to display a generic Reaction,
     * this number indicates which one it is.
     */
    public int rGroupNumber;

    /**
     * The charge of this Atom, relative to a proton.
     */
    public final double formalCharge;

    public LegacyAtom(LegacyElement element) {
        this(element, 0);
    };

    public LegacyAtom(LegacyElement element, double formalCharge) {
        this.element = element;
        this.formalCharge = formalCharge;
    };

    /**
     * The {@link LegacyElement specific isotope} of this Atom.
     */
    public LegacyElement getElement() {
        return this.element;
    };

    public PartialModel getPartial() {
        if (element == LegacyElement.R_GROUP) {
            if (rGroupNumber < 10 && rGroupNumber >= 1) {
                return DestroyPartials.rGroups.get(rGroupNumber);
            };
            return DestroyPartials.R_GROUP;
        } else {
            return element.getPartial();
        }
    };

    /**
     * Whether this is Atom is a {@link LegacyElement#HYDROGEN Hydrogen} with no {@link LegacyAtom#formalCharge formal charge}.
     */
    public Boolean isNeutralHydrogen() {
        return element == LegacyElement.HYDROGEN && formalCharge == 0;
    };

};
