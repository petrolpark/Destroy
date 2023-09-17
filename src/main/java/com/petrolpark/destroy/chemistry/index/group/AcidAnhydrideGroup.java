package com.petrolpark.destroy.chemistry.index.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;

public class AcidAnhydrideGroup extends Group<AcidAnhydrideGroup> {

    private Atom firstCarbon;
    private Atom firstOxygen;
    private Atom secondCarbon;
    private Atom secondOxygen;
    private Atom bridgingOxygen;

    public AcidAnhydrideGroup() {
        super();
    };

    public AcidAnhydrideGroup(Atom firstCarbon, Atom firstOxygen, Atom secondCarbon, Atom secondOxygen, Atom bridgingOxygen) {
        super();
        this.firstCarbon = firstCarbon;
        this.firstOxygen = firstOxygen;
        this.secondCarbon = secondCarbon;
        this.secondOxygen = secondOxygen;
        this.bridgingOxygen = bridgingOxygen;
    };

    public Atom getFirstCarbon() {
        return firstCarbon;
    };

    public Atom getFirstOxygen() {
        return firstOxygen;
    };

    public Atom getSecondCarbon() {
        return secondCarbon;
    };

    public Atom getSecondOxygen() {
        return secondOxygen;
    };

    public Atom getBridgingOxygen() {
        return bridgingOxygen;
    };

    @Override
    public Molecule getExampleMolecule() {
        return DestroyMolecules.GENERIC_ACID_ANHYDRIDE;
    }

    @Override
    public GroupType<AcidAnhydrideGroup> getType() {
        return DestroyGroupTypes.ACID_ANHYDRIDE;
    };
    
};
