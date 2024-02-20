package com.petrolpark.destroy.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotation.BracketResolver;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.MoleculeTag;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.compat.crafttweaker.action.AddMoleculeAction;
import com.petrolpark.destroy.compat.crafttweaker.action.RemoveMoleculeAction;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.destroy.Molecules")
@Document("mods/destroy/Molecules")
public class CTMoleculeManager {
    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder create(String id) {
        return new Molecule.MoleculeBuilder("crafttweaker")
            .id(id);
    }

    @ZenCodeType.Method
    public static Molecule getMoleculeById(String moleculeId) {
        return Molecule.MOLECULES.get(moleculeId);
    }

    @ZenCodeType.Method
    public static void removeMolecule(Molecule molecule) {
        CraftTweakerAPI.apply(new RemoveMoleculeAction(molecule));
    }

    @ZenCodeType.Method
    @BracketResolver("molecule")
    public static Object getMolecule(String tokens) {
        if(tokens.startsWith("tag:")) {
            return MoleculeTag.MOLECULE_TAGS.get(tokens.replaceFirst("tag:", ""));
        }
        return getMoleculeById(tokens);
    }

    @ZenCodeType.Method
    @BracketResolver("element")
    public static Element getElement(String tokens) {
        return Element.valueOf(tokens.toUpperCase());
    }
}
