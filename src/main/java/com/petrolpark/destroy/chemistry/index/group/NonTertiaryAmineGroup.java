package com.petrolpark.destroy.chemistry.index.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;

public class NonTertiaryAmineGroup extends Group<NonTertiaryAmineGroup> {

    public final Atom carbon;
    public final Atom nitrogen;
    public final Atom hydrogen;

    public NonTertiaryAmineGroup(Atom carbon, Atom nitrogen, Atom hydrogen) {
        this.carbon = carbon;
        this.nitrogen = nitrogen;
        this.hydrogen = hydrogen;
    };

    @Override
    public GroupType<NonTertiaryAmineGroup> getType() {
        return DestroyGroupTypes.NON_TERTIARY_AMINE;
    };
    
};
