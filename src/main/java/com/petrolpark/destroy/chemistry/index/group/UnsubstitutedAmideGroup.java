package com.petrolpark.destroy.chemistry.index.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;

public class UnsubstitutedAmideGroup extends Group<UnsubstitutedAmideGroup> {

    public final Atom carbon;
    public final Atom oxygen;
    public final Atom nitrogen;
    public final Atom hydrogen1;
    public final Atom hydrogen2;

    public UnsubstitutedAmideGroup(Atom carbon, Atom oxygen, Atom nitrogen, Atom hydrogen1, Atom hydrogen2) {
        super();
        this.carbon = carbon;
        this.oxygen = oxygen;
        this.nitrogen = nitrogen;
        this.hydrogen1 = hydrogen1;
        this.hydrogen2 = hydrogen2;
    };

    @Override
    public GroupType<UnsubstitutedAmideGroup> getType() {
        return DestroyGroupTypes.UNSUBSTITUTED_AMIDE;
    };
    
};
