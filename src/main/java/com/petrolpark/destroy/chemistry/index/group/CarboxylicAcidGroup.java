package com.petrolpark.destroy.chemistry.index.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;

public class CarboxylicAcidGroup extends Group<CarboxylicAcidGroup> {
    private Atom carbon;
    private Atom carbonylOxygen;
    private Atom alcoholOxygen;
    private Atom proton;

    public CarboxylicAcidGroup() {
        super();
    };

    public CarboxylicAcidGroup(Atom carbon, Atom carbonylOxygen, Atom alcoholOxygen, Atom proton) {
        super();
        this.carbon = carbon;
        this.carbonylOxygen = carbonylOxygen;
        this.alcoholOxygen = alcoholOxygen;
        this.proton = proton;
    };

    public Atom getCarbon() {
        return this.carbon;
    };

    public Atom getCarbonylOxygen() {
        return this.carbonylOxygen;
    };

    public Atom getAlcoholOxygen() {
        return this.alcoholOxygen;
    };

    public Atom getProton() {
        return this.proton;
    };

    @Override
    public Molecule getExampleMolecule() {
        return DestroyMolecules.GENERIC_CARBOXYLIC_ACID;
    };

    @Override
    public GroupType<CarboxylicAcidGroup> getType() {
        return DestroyGroupTypes.CARBOXYLIC_ACID;
    };
};
