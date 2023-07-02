package com.petrolpark.destroy.chemistry.genericReaction;

import java.util.HashSet;
import java.util.Set;

import com.petrolpark.destroy.chemistry.Molecule.MoleculeBuilder;

public abstract class GenericReaction {

    public static Set<GenericReaction> GENERIC_REACTIONS = new HashSet<>();

    protected static MoleculeBuilder moleculeBuilder() {
        return new MoleculeBuilder("novel");
    };

    public abstract Boolean involvesSingleGroup();

};
