package com.petrolpark.destroy.chemistry.index.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;

public class NitrileGroup extends Group<NitrileGroup> {

    public final Atom carbon;
    public final Atom nitrogen;

    public NitrileGroup(Atom carbon, Atom nitrogen) {
        this.carbon = carbon;
        this.nitrogen = nitrogen;
    };

    @Override
    public GroupType<NitrileGroup> getType() {
        return DestroyGroupTypes.NITRILE;
    };
    
};
