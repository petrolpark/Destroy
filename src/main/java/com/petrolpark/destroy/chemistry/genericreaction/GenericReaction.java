package com.petrolpark.destroy.chemistry.genericreaction;

import java.util.HashSet;
import java.util.Set;

import com.petrolpark.destroy.chemistry.Molecule.MoleculeBuilder;
import com.petrolpark.destroy.chemistry.Reaction.ReactionBuilder;

public abstract class GenericReaction {

    public static Set<GenericReaction> GENERIC_REACTIONS = new HashSet<>();

    protected static MoleculeBuilder moleculeBuilder() {
        return new MoleculeBuilder("novel");
    };

    protected static ReactionBuilder reactionBuilder() {
        return new ReactionBuilder(true);
    };

    public abstract Boolean involvesSingleGroup();

};
