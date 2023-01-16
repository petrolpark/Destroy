package com.petrolpark.destroy.chemistry.index.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;

public class EsterGroup extends Group {

    private Atom carbonylCarbon;
    private Atom alcoholCarbon;
    private Atom carbonylOxygen;
    private Atom bridgeOxygen;

    public EsterGroup() {
        super();
    };

    public EsterGroup(Atom carbonylCarbon, Atom alcoholCarbon, Atom carbonylOxygen, Atom bridgeOxygen) {
        super();
        this.carbonylCarbon = carbonylCarbon;
        this.alcoholCarbon = alcoholCarbon;
        this.carbonylOxygen = carbonylOxygen;
        this.bridgeOxygen = bridgeOxygen;
    };

    public Atom getCarbonylCarbon() {
        return carbonylCarbon;
    };

    public Atom getAlcoholCarbon() {
        return alcoholCarbon;
    };

    public Atom getCarbonylOxygen() {
        return carbonylOxygen;
    };

    public Atom getBridgeOxygen() {
        return bridgeOxygen;
    };

    @Override
    public Molecule getExampleMolecule() {
        return DestroyMolecules.GENERIC_ESTER;
    };

    @Override
    public String getID() {
        return "ESTER";
    };
    
};
