package com.petrolpark.destroy.chemistry.index.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;

public class AlcoholGroup extends Group<AlcoholGroup> {

    public final Atom carbon;
    public final Atom oxygen;
    public final Atom hydrogen;

    public final int degree;

    public AlcoholGroup(Atom carbon, Atom oxygen, Atom hydrogen, int degree) {
        super();
        this.carbon = carbon;
        this.oxygen = oxygen;
        this.hydrogen = hydrogen;
        this.degree = degree;
    };

    @Override
    public GroupType<AlcoholGroup> getType() {
        return DestroyGroupTypes.ALCOHOL;
    };
    
};
