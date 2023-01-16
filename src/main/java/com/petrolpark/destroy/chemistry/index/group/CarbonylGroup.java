package com.petrolpark.destroy.chemistry.index.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;

public class CarbonylGroup extends Group {

    private Atom carbon;
    private Atom oxygen;

    private Boolean isKetone;

    public CarbonylGroup() {
        super();
    };

    public CarbonylGroup(Atom carbon, Atom oxygen, Boolean isKetone) {
        super();
        this.carbon = carbon;
        this.oxygen = oxygen;
        this.isKetone = isKetone;
    };

    public Atom getCarbon() {
        return this.carbon;
    };

    public Atom getOxygen() {
        return this.oxygen;
    };

    /**
     * Whether this Carbonyl is a ketone - if it is not a ketone, it is either an aldehyde with one R group or formaldehye.
     */
    public Boolean isKetone() {
        return isKetone;
    };

    @Override
    public Molecule getExampleMolecule() {
        return DestroyMolecules.GENERIC_CARBONYL;
    };

    @Override
    public String getID() {
        return "CARBONYL";
    };
    
};
