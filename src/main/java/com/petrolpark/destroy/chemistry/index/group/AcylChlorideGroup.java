package com.petrolpark.destroy.chemistry.index.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;

public class AcylChlorideGroup extends Group<AcylChlorideGroup> {

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
    public GroupType<AcylChlorideGroup> getType() {
        return DestroyGroupTypes.ACYL_CHLORIDE;
    };
    
};
