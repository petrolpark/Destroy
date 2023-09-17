package com.petrolpark.destroy.chemistry.genericreaction;

import java.util.HashSet;
import java.util.Set;

import com.petrolpark.destroy.chemistry.Molecule.MoleculeBuilder;

import net.minecraft.resources.ResourceLocation;

public abstract class GenericReaction {

    public static Set<GenericReaction> GENERIC_REACTIONS = new HashSet<>();
    protected final ResourceLocation id;

    public GenericReaction(ResourceLocation id) {
        this.id = id;
    };

    protected static MoleculeBuilder moleculeBuilder() {
        return new MoleculeBuilder("novel");
    };

    public abstract Boolean involvesSingleGroup();

};
