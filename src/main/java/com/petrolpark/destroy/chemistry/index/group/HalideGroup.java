package com.petrolpark.destroy.chemistry.index.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;

public class HalideGroup extends Group<HalideGroup> {

    public final Atom carbon;
    public final  Atom halogen;
    public int degree;

    public HalideGroup(Atom carbon, Atom halogen, int degree) {
        this.carbon = carbon;
        this.halogen = halogen;
        this.degree = degree;
    };

    @Override
    public GroupType<HalideGroup> getType() {
        return DestroyGroupTypes.HALIDE;
    };
    
};
