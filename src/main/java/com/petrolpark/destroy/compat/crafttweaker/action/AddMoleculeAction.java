package com.petrolpark.destroy.compat.crafttweaker.action;

import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.compat.crafttweaker.CTDestroy;

public class AddMoleculeAction extends DestroyAction {
    private final Molecule molecule;

    public AddMoleculeAction(Molecule molecule) {
        this.molecule = molecule;
    }
    @Override
    public void undo() {
        Molecule.removeMolecule(molecule);
    }

    @Override
    public String describeUndo() {
        return "Unregisters the Molecule from the registry and makes it invisible for the reactions";
    }

    @Override
    public void apply() {
        CTDestroy.getLogger().info("Registered " + molecule.getFullID());
    }

    @Override
    public String describe() {
        return "Adds a Molecule to the registry and makes it visible for the reactions";
    }
}
