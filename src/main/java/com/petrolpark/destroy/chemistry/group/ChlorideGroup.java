package com.petrolpark.destroy.chemistry.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;

public class ChlorideGroup extends Group {

    private Atom carbon;
    private Atom chlorine;

    private int degree;

    public ChlorideGroup(Atom carbon, Atom chlorine, int degree) {
        this.carbon = carbon;
        this.chlorine = chlorine;
        this.degree = degree;
    };

    public Atom getCarbon() {
        return carbon;
    };

    public Atom getChlorine() {
        return chlorine;
    };

    public int getDegree() {
        return degree;
    };

    @Override
    public Molecule getExampleMolecule() {
        return DestroyMolecules.GENERIC_CHLORIDE;
    };
    
};
