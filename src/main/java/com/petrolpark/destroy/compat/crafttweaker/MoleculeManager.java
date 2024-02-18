package com.petrolpark.destroy.compat.crafttweaker;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.destroy.Reactions")
@Document("mods/destroy/Reactions")
public class MoleculeManager {
    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder create() {
        return new Molecule.MoleculeBuilder("crafttweaker");
    }

    @ZenCodeType.Method
    public static Molecule getMoleculeById(String moleculeId) {
        return Molecule.MOLECULES.get(moleculeId);
    }
}
