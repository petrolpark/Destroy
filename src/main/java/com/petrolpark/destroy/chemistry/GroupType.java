package com.petrolpark.destroy.chemistry;

import java.util.function.Supplier;

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
            throw new IllegalStateException("Example Molecules for Group Types must include that Group.");
        };
        exampleMoleculeVerified = true;
    };
};
