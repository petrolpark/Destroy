package com.petrolpark.destroy.compat.crafttweaker.natives;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import com.petrolpark.destroy.chemistry.IItemReactant;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.ReactionResult;
import org.openzen.zencode.java.ZenCodeType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ZenRegister
@Document("mods/destroy/Reaction")
@NativeTypeRegistration(value = Reaction.class, zenCodeName = "mods.destroy.Reaction")
public class CTReaction {

    /**
     * Whether this Molecule gets consumed in this Reaction (does not include catalysts).
     *
     * @param molecule
     */
    @ZenCodeType.Method
    public static boolean containsReactant(Reaction internal, Molecule molecule) {
        return internal.containsReactant(molecule);
    }

    /**
     * Whether this Molecule is created in this Reaction.
     *
     * @param molecule
     */
    @ZenCodeType.Method
    public static boolean containsProduct(Reaction internal, Molecule molecule) {
        return internal.containsProduct(molecule);
    }

    /**
     * All Molecules which are consumed in this Reaction (but not their molar ratios).
     */
    @ZenCodeType.Method
    public static Set<Molecule> getReactants(Reaction internal) {
        return internal.getReactants();
    }

    /**
     * Whether this Reaction needs any Item Stack as a {@link IItemReactant}. Even if this is
     * {@code true}, the Reaction may still have {@link IItemReactant#isCatalyst Item Stack catalysts}.
     */
    @ZenCodeType.Method
    public static boolean consumesItem(Reaction internal) {
        return internal.consumesItem();
    }

    /**
     * Get the {@link IItemReactant} for this Reaction.
     */
    @ZenCodeType.Method
    public static List<IItemReactant> getItemReactants(Reaction internal) {
        return internal.getItemReactants();
    }

    @ZenCodeType.Method
    public static float getMolesPerItem(Reaction internal) {
        return internal.getMolesPerItem();
    }

    /**
     * Whether this Reaction needs UV light to proceed.
     */
    @ZenCodeType.Method
    public static boolean needsUV(Reaction internal) {
        return internal.needsUV();
    }

    /**
     * All Molecules which are created in this Reaction (but not their molar ratios).
     */
    @ZenCodeType.Method
    public static Set<Molecule> getProducts(Reaction internal) {
        return internal.getProducts();
    }
    @ZenCodeType.Method
    public static float getActivationEnergy(Reaction internal) {
        return internal.getActivationEnergy();
    }
    @ZenCodeType.Method
    public static float getPreexponentialFactor(Reaction internal) {
        return internal.getPreexponentialFactor();
    }

    /**
     * The rate constant of this Reaction at the given temperature.
     *
     * @param temperature (in kelvins).
     */
    @ZenCodeType.Method
    public static float getRateConstant(Reaction internal, float temperature) {
        return internal.getRateConstant(temperature);
    }

    @ZenCodeType.Method
    public static float getEnthalpyChange(Reaction internal) {
        return internal.getEnthalpyChange();
    }

    /**
     * Whether this Reaction has a {@link com.petrolpark.destroy.compat.crafttweaker.natives.reactionresult.CTReactionResult}.
     */
    @ZenCodeType.Method
    public static boolean hasResult(Reaction internal) {
        return internal.hasResult();
    }

    /**
     * The {@link com.petrolpark.destroy.compat.crafttweaker.natives.reactionresult.CTReactionResult} of this Reaction, which occurs once a set
     * number of moles of Reaction have occured.
     *
     * @return {@code null} if this Reaction has no result.
     */
    @ZenCodeType.Method
    public static ReactionResult getResult(Reaction internal) {
        return internal.getResult();
    }

    /**
     * Get the fully unique ID for this Reaction, in the format {@code <namespace>:
     * <id>}, for example {@code destroy:chloroform_fluorination}.
     */
    @ZenCodeType.Method
    public static String getFullID(Reaction internal) {
        return internal.getFullID();
    }

    /**
     * Whether this Reaction should be displayed in the list of Reactions in JEI.
     */
    @ZenCodeType.Method
    public static boolean includeInJei(Reaction internal) {
        return internal.includeInJei();
    }

    /**
     * Whether this Reaction should be displayed in JEI with an equilibrium arrow rather than a normal one.
     */
    @ZenCodeType.Method
    public static boolean displayAsReversible(Reaction internal) {
        return internal.displayAsReversible();
    }

    /**
     * If this is the 'forward' half of a reversible Reaction, this contains the reverse Reaction. This is so JEI
     * knows the products of the forward Reaction are the reactants of the reverse, and vice versa. If this is not
     * part of a reversible Reaction, this is empty. This is just for display; if a Reaction has a reverse and is needed
     * for logic (e.g. Reacting in a Mixture) it should not be accessed in this way.
     */
    @ZenCodeType.Method
    public static Optional<Reaction> getReverseReactionForDisplay(Reaction internal) {
        return internal.getReverseReactionForDisplay();
    }

    /**
     * Get the stoichometric ratio of this {@link CTMolecule} or catalyst in this Reaction.
     *
     * @param reactant
     * @return {@code 0} if this Molecule is not a reactant
     */
    @ZenCodeType.Method
    public static int getReactantMolarRatio(Reaction internal, Molecule reactant) {
        return internal.getReactantMolarRatio(reactant);
    }

    /**
     * Get the stoichometric ratio of this {@link CTMolecule} in this Reaction.
     *
     * @param product
     * @return {@code 0} if this Molecule is not a product
     */
    @ZenCodeType.Method
    public static int getProductMolarRatio(Reaction internal, Molecule product) {
        return internal.getProductMolarRatio(product);
    }

    /**
     * Get every {@link CTMolecule} reactant and catalyst in this Reaction, mapped to their
     * orders in the rate equation.
     */
    @ZenCodeType.Method
    public static Map<Molecule, Integer> getOrders(Reaction internal) {
        return internal.getOrders();
    }

    @ZenCodeType.Method
    public static String getID(Reaction internal) {
        return internal.getFullID();
    }
}
