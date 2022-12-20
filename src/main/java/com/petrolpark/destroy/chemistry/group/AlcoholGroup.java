package com.petrolpark.destroy.chemistry.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;

public class AlcoholGroup extends Group {

    private Atom carbon;
    private Atom oxygen;
    private Atom hydrogen;

    private int degree;

    public AlcoholGroup() {
        super();
    };

    public AlcoholGroup(Atom carbon, Atom oxygen, Atom hydrogen, int degree) {
        super();
        this.carbon = carbon;
        this.oxygen = oxygen;
        this.hydrogen = hydrogen;
        this.degree = degree;
    };

    public Atom getCarbon() {
        return carbon;
    };

    public Atom getOxygen() {
        return oxygen;
    };

    public Atom getHydrogen() {
        return hydrogen;
    };

    public int getDegree() {
        return this.degree;
    };

    @Override
    public Molecule getExampleMolecule() {
        return DestroyMolecules.GENERIC_ALCOHOL;
    };

    @Override
    public String getID() {
        return "ALCOHOL";
    };
    
};
