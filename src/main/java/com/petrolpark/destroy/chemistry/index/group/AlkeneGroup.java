package com.petrolpark.destroy.chemistry.index.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;

public class AlkeneGroup extends Group<AlkeneGroup> {

    //TODO alkenes

    private Atom highDegreeCarbon;
    private Atom lowDegreeCarbon;

    public AlkeneGroup() {
        super();
    };

    public AlkeneGroup(Atom highDegreeCarbon, Atom lowDegreeCarbon) {
        super();
        this.highDegreeCarbon = highDegreeCarbon;
        this.lowDegreeCarbon = lowDegreeCarbon;
    };

    @Override
    public GroupType<AlkeneGroup> getType() {
        return DestroyGroupTypes.ALKENE_GROUP;
    };

};
