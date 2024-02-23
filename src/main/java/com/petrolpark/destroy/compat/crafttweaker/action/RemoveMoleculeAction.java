package com.petrolpark.destroy.compat.crafttweaker.action;

import com.petrolpark.destroy.chemistry.Molecule;

public class RemoveMoleculeAction extends DestroyAction {

    private final Molecule molecule;

    public RemoveMoleculeAction(Molecule molecule) {
        this.molecule = molecule;
    }
    @Override
    public void undo() {
        Molecule.addMolecule(molecule);
    }

    @Override
    public String describe() {
        return "Removes the molecule from the registry and makes it unavailable for the vat reactions";
    }

    @Override
    public String describeUndo() {
        return "Adds the molecule back and makes it available for the reactions";
    }

    @Override
    public void apply() {
        Molecule.removeMolecule(molecule);
    }
}
