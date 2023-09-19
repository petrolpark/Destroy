package com.petrolpark.destroy.chemistry.index.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;

public class ChlorideGroup extends Group<ChlorideGroup> {

    private Atom carbon;
    private Atom chlorine;

    private int degree;

    public ChlorideGroup() {
        super();
    };

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
    public GroupType<ChlorideGroup> getType() {
        return DestroyGroupTypes.CHLORIDE;
    };
    
};
