package com.petrolpark.destroy.chemistry.group;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;

public class AlkeneGroup extends Group {

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
    public Molecule getExampleMolecule() {
        return DestroyMolecules.GENERIC_ALKENE;
    };

    @Override
    public String getID() {
        return "ALKENE";
    };
    
};
