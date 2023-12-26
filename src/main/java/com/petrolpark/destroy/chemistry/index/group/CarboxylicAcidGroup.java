package com.petrolpark.destroy.chemistry.index.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;

public class CarboxylicAcidGroup extends Group<CarboxylicAcidGroup> {
    public final Atom carbon;
    public final Atom carbonylOxygen;
    public final Atom alcoholOxygen;
    public final Atom proton;

    public CarboxylicAcidGroup(Atom carbon, Atom carbonylOxygen, Atom alcoholOxygen, Atom proton) {
        super();
        this.carbon = carbon;
        this.carbonylOxygen = carbonylOxygen;
        this.alcoholOxygen = alcoholOxygen;
        this.proton = proton;
    };

    @Override
    public GroupType<CarboxylicAcidGroup> getType() {
        return DestroyGroupTypes.CARBOXYLIC_ACID;
    };
};
