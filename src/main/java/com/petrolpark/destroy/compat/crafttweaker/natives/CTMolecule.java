package com.petrolpark.destroy.compat.crafttweaker.natives;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.MoleculeTag;
import com.petrolpark.destroy.chemistry.Reaction;
import net.minecraft.network.chat.Component;
import org.openzen.zencode.java.ZenCodeType;

import java.util.List;
import java.util.Set;

@ZenRegister
@Document("mods/destroy/Molecule")
@NativeTypeRegistration(value = Molecule.class, zenCodeName = "mods.destroy.Molecule")
public class CTMolecule {

    @ZenCodeType.Method
    public static Molecule getEquivalent(Molecule internal) {
        return internal.getEquivalent();
    }

    @ZenCodeType.Method
    public static String getFROWNSCode(Molecule internal) {
        return internal.getFROWNSCode();
    }

    @ZenCodeType.Method
    public static int getCharge(Molecule internal) {
        return internal.getCharge();
    }

    @ZenCodeType.Method
    public static float getMass(Molecule internal) {
        return internal.getMass();
    }

    @ZenCodeType.Method
    public static float getDensity(Molecule internal) {
        return internal.getDensity();
    }

    @ZenCodeType.Method
    public static float getPureConcentration(Molecule internal) {
        return internal.getPureConcentration();
    }

    @ZenCodeType.Method
    public static float getBoilingPoint(Molecule internal) {
        return internal.getBoilingPoint();
    }

    @ZenCodeType.Method
    public static float getDipoleMoment(Molecule internal) {
        return internal.getDipoleMoment();
    }

    @ZenCodeType.Method
    public static float getMolarHeatCapacity(Molecule internal) {
        return internal.getMolarHeatCapacity();
    }

    @ZenCodeType.Method
    public static float getLatentHeat(Molecule internal) {
        return internal.getLatentHeat();
    }

    @ZenCodeType.Method
    public static boolean isCyclic(Molecule internal) {
        return internal.isCyclic();
    }

    @ZenCodeType.Method
    public static Set<Atom> getAtoms(Molecule internal) {
        return internal.getAtoms();
    }

    @ZenCodeType.Method
    public static boolean isHypothetical(Molecule internal) {
        return internal.isHypothetical();
    }

    @ZenCodeType.Method
    public static Set<MoleculeTag> getTags(Molecule internal) {
        return internal.getTags();
    }

    @ZenCodeType.Method
    public static boolean hasTag(Molecule internal, MoleculeTag tag) {
        return internal.hasTag(tag);
    }

    @ZenCodeType.Method
    public static boolean isNovel(Molecule internal) {
        return internal.isNovel();
    }

    @ZenCodeType.Method
    public static String getStructuralFormula(Molecule internal) {
        return internal.getStructuralFormula();
    }

    @ZenCodeType.Method

    public static float getCarbocationStability(Molecule internal, Atom carbon, boolean isCarbanion) {
        return internal.getCarbocationStability(carbon, isCarbanion);
    }

    @ZenCodeType.Method
    public static String getTranslationKey(Molecule internal, boolean iupac) {
        return internal.getTranslationKey(iupac);
    }

    @ZenCodeType.Method
    public static int getColor(Molecule internal) {
        return internal.getColor();
    }

    @ZenCodeType.Method
    public static boolean isColorless(Molecule internal) {
        return internal.isColorless();
    }

    @ZenCodeType.Method
    public static Component getName(Molecule internal, boolean iupac) {
        return internal.getName(iupac);
    }

    @ZenCodeType.Method
    public static String getId(Molecule internal) {
        return internal.getFullID();
    }

    @ZenCodeType.Method
    public static void addReactantReaction(Molecule internal, Reaction reaction) {
        internal.addReactantReaction(reaction);
    }

    /**
     * Mark this Molecule as being a necessary reactant in the given {@link Reaction}.
     * There should never be any need to call this method (it is done automatically when {@link Reaction.ReactionBuilder#build building} a Reaction).
     * @param reaction
     */

    @ZenCodeType.Method
    public static void addProductReaction(Molecule internal, Reaction reaction) {
        internal.addProductReaction(reaction);
    }

    /**
     * Get the list of {@link Reaction Reactions} of which this Molecule is a necessary Reactant.
     * @return List of Reactions ordered by declaration
     */
    @ZenCodeType.Method
    public static List<Reaction> getReactantReactions(Molecule internal) {
        return internal.getReactantReactions();
    }

    /**
     * Get the list of {@link Reaction Reactions} by which this Molecule is made.
     * @return List of Reactions ordered by declaration
     */
    @ZenCodeType.Method
    public static List<Reaction> getProductReactions(Molecule internal) {
        return internal.getProductReactions();
    }
}
