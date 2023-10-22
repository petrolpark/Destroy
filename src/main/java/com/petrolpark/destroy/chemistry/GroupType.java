package com.petrolpark.destroy.chemistry;

import java.util.function.Supplier;

import com.petrolpark.destroy.chemistry.error.ChemistryException.ExampleMoleculeMissingGroupException;

public class GroupType<G extends Group<G>> {

    private final Supplier<Molecule> exampleMolecule;
    private boolean exampleMoleculeVerified = false;

    public GroupType(Supplier<Molecule> exampleMoleculeSupplier) {
        
        this.exampleMolecule = exampleMoleculeSupplier;
    };

    public Molecule getExampleMolecule() {
        if (!exampleMoleculeVerified) verifyExampleMolecule();
        return exampleMolecule.get();
    };

    private void verifyExampleMolecule() {
        if (!exampleMolecule.get().getFunctionalGroups().stream().anyMatch(group -> {
            return group.getType() == this;
        })) {
            throw new ExampleMoleculeMissingGroupException(exampleMolecule.get());
        };
        exampleMoleculeVerified = true;
    };
};
