package com.petrolpark.destroy.chemistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Molecule Tags do not affect the behaviour of {@link Molecule Molecules} in {@link Mixture Mixtures}, but instruct other parts of the game on how to deal with them.
 */
public class MoleculeTag {
    public static final Map<String, MoleculeTag> MOLECULE_TAGS = new HashMap<>();

    public MoleculeTag(String id) { //TODO replace with proper registry
        MOLECULE_TAGS.put(id, this);
    };
};
