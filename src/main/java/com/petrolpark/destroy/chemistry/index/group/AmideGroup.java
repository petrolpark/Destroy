package com.petrolpark.destroy.chemistry.index.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;

public class AmideGroup extends Group<AmideGroup> {

    private Atom carbon;
    private Atom oxygen;
    private Atom nitrogen;

    public AmideGroup() {
        super();
    };

    public AmideGroup(Atom carbon, Atom oxygen, Atom nitrogen) {
        super();
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
    public GroupType<AmideGroup> getType() {
        return DestroyGroupTypes.AMIDE;
    };
    
};
