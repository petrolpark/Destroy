package com.petrolpark.destroy.chemistry.genericReaction;

import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.Molecule;

public class GenericReactant<T extends Group> {
    private Molecule molecule;
    private T group;

    public GenericReactant(Molecule molecule, T group) {
        this.molecule = molecule;
        this.group = group;
    };

    public Molecule getMolecule() {
        return this.molecule;
    };

    public T getGroup() {
        return this.group;
    };
}
