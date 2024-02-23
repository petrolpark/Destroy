package com.petrolpark.destroy.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotation.BracketResolver;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.MoleculeTag;
import com.petrolpark.destroy.compat.crafttweaker.action.RemoveMoleculeAction;
import org.openzen.zencode.java.ZenCodeType;

/**
 * Use Molecules to manage molecules of the mod. Use <molecule> bracket handler to query
 * a molecule. Example: <molecule:destroy:water>
 * <p>
 * When creating a molecule, you may want to specify how does the molecule impact the environment, use
 * <moleculetag> bracket handler. Example: <moleculetag:destroy:acutely_toxic> (specifies that the molecule is toxic)
 * <p>
 * Use {@link CTMoleculeManager#create(String)} to create a {@link com.petrolpark.destroy.compat.crafttweaker.natives.CTMoleculeBuilder}
 * if you want to create your own molecule
 * <p>
 * Use {@link CTMoleculeManager#removeMolecule(Molecule)} to remove a molecule (all reactions involving this molecule will also be removed)
 */
@ZenRegister
@ZenCodeType.Name("mods.destroy.Molecules")
@Document("mods/destroy/Molecules")
public class CTMoleculeManager {

    /**
     * Creates a molecule builder. Call .build() to build the molecule
     * @param id ID of the new molecule
     * @return The {@link com.petrolpark.destroy.compat.crafttweaker.natives.CTMoleculeBuilder}
     *
     * @docParam "tellurium_copper"
     */
    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder create(String id) {
        return new Molecule.MoleculeBuilder("crafttweaker")
            .id(id);
    }

    /**
     * Gets a molecule by full ID
     * @param moleculeId Molecule ID to search
     * @return A {@link com.petrolpark.destroy.compat.crafttweaker.natives.CTMolecule} which was found by ID or null if molecule doesn't exist
     *
     * @docParam moleculeId "destroy:water"
     */
    @ZenCodeType.Method
    public static Molecule getMoleculeById(String moleculeId) {
        return Molecule.getMolecule(moleculeId);
    }

    /**
     * Removes this molecule from registry. This makes all reactions involving this molecule to unregister as well.
     * @param molecule {@link com.petrolpark.destroy.compat.crafttweaker.natives.CTMolecule} to remove
     *
     * @docParam molecule <molecule:destroy:methylamine>
     */
    @ZenCodeType.Method
    public static void removeMolecule(Molecule molecule) {
        CraftTweakerAPI.apply(new RemoveMoleculeAction(molecule));
    }

    @ZenCodeType.Method
    @BracketResolver("molecule")
    public static Molecule getMolecule(String tokens) {
        return getMoleculeById(tokens);
    }

    @ZenCodeType.Method
    @BracketResolver("moleculetag")
    public static MoleculeTag getMoleculeTag(String tokens) {
        return MoleculeTag.MOLECULE_TAGS.get(tokens);
    }

    @ZenCodeType.Method
    @BracketResolver("element")
    public static Element getElement(String tokens) {
        return Element.valueOf(tokens.toUpperCase());
    }
}
