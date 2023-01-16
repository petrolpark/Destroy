package com.petrolpark.destroy.chemistry.index.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;

public class AcylChlorideGroup extends Group {

    private Atom carbon;
    private Atom oxygen;
    private Atom chlorine;

    public AcylChlorideGroup(Atom carbon, Atom oxygen, Atom chlorine) {
        super();
        this.carbon = carbon;
        this.oxygen = oxygen;
        this.chlorine = chlorine;
    };

    public Atom getCarbon() {
        return carbon;
    };

    public Atom getOxygen() {
        return oxygen;
    };

    public Atom getChlorine() {
        return chlorine;
    };

    @Override
    public Molecule getExampleMolecule() {
        return DestroyMolecules.GENERIC_ACYL_CHLORIDE;
    };
    
    @Override
    public String getID() {
        return "ACYL_CHLORIDE";
    };
    
};
