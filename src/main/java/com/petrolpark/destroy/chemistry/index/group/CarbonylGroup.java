package com.petrolpark.destroy.chemistry.index.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;

public class CarbonylGroup extends Group<CarbonylGroup> {

    public final Atom carbon;
    public final Atom oxygen;

    /**
     * Whether this Carbonyl is a ketone - if it is not a ketone, it is either an aldehyde (with one R group), formaldehye, or unknown.
     */
    public final boolean isKetone;

    public CarbonylGroup(Atom carbon, Atom oxygen, boolean isKetone) {
        super();
        this.carbon = carbon;
        this.oxygen = oxygen;
        this.isKetone = isKetone;
    };

    @Override
    public GroupType<CarbonylGroup> getType() {
        return DestroyGroupTypes.CARBONYL;
    };
    
};
