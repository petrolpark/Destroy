package com.petrolpark.destroy.chemistry.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;

public class AmideGroup extends Group {

    private Atom carbon;
    private Atom oxygen;
    private Atom nitrogen;

    public AmideGroup(Atom carbon, Atom oxygen, Atom nitrogen) {
        this.carbon = carbon;
        this.oxygen = oxygen;
        this.nitrogen = nitrogen;
    };

    public Atom getCarbon() {
        return this.carbon;
    };

    public Atom getOxygen() {
        return this.oxygen;
    };

    public Atom getNitrogen() {
        return this.nitrogen;
    };

    @Override
    public Molecule getExampleMolecule() {
        return DestroyMolecules.GENERIC_AMIDE;
    };
    
};
