package com.petrolpark.destroy.compat.crafttweaker.natives;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeConstructor;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Bond;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import org.openzen.zencode.java.ZenCodeType;

import java.util.List;
import java.util.Set;

@ZenRegister
@Document("mods/destroy/Formula")
@NativeTypeRegistration(
    value = Formula.class,
    zenCodeName = "mods.destroy.Formula",
    constructors = {
        @NativeConstructor({
            @NativeConstructor.ConstructorParameter(type = Atom.class, name = "startingAtom")
        })
    }
)
public class CTFormula {
    @ZenCodeType.Method
    public static Formula moveTo(Formula internal, Atom atom) {
        return internal.moveTo(atom);
    }

    @ZenCodeType.Method
    public static Formula setStartingAtom(Formula internal, Atom atom) {
        return internal.setStartingAtom(atom);
    }

    @ZenCodeType.StaticExpansionMethod
    public static Formula atom(Element element) {
        return Formula.atom(element);
    }

    @ZenCodeType.StaticExpansionMethod
    public static Formula carbonChain(int length) {
        return Formula.carbonChain(length);
    }

    @ZenCodeType.StaticExpansionMethod
    public static Formula alcohol() {
        return Formula.alcohol();
    }

    @ZenCodeType.Method
    public static Formula addAtom(Formula internal, Element element) {
        return internal.addAtom(element);
    }

    @ZenCodeType.Method
    public static Formula addAtom(Formula internal, Element element, Bond.BondType bondType) {
        return internal.addAtom(element, bondType);
    }

    @ZenCodeType.Method
    public static Formula addAtom(Formula internal, Atom atom) {
        return internal.addAtom(atom);
    }

    @ZenCodeType.Method
    public static Formula addAtom(Formula internal, Atom atom, Bond.BondType bondType) {
        return internal.addAtom(atom, bondType);
    }

    @ZenCodeType.Method
    public static Formula addGroup(Formula internal, Formula group) {
        return internal.addGroup(group);
    }

    @ZenCodeType.Method
    public static Formula addGroup(Formula internal, Formula group, boolean isSideGroup) {
        return internal.addGroup(group, isSideGroup);
    }

    @ZenCodeType.Method
    public static Formula joinFormulae(Formula formula1, Formula formula2, Bond.BondType bondType) {
        return Formula.joinFormulae(formula1, formula2, bondType);
    }

    @ZenCodeType.Method
    public static Formula addGroup(Formula internal, Formula group, boolean isSideGroup, Bond.BondType bondType) {
        return internal.addGroup(group, isSideGroup, bondType);
    }

    @ZenCodeType.Method
    public static boolean isCyclic(Formula internal) {
        return internal.isCyclic();
    }

    @ZenCodeType.Method
    public static Formula remove(Formula internal, Atom atom) {
        return internal.remove(atom);
    }

    @ZenCodeType.Method
    public static Formula replace(Formula internal, Atom oldAtom, Atom newAtom) {
        return internal.replace(oldAtom, newAtom);
    }

    @ZenCodeType.Method
    public static Formula replaceBondTo(Formula internal, Atom otherAtom, Bond.BondType bondType) {
        return internal.replaceBondTo(otherAtom, bondType);
    }

    @ZenCodeType.Method
    public static Formula addCarbonyl(Formula internal) {
        return internal.addCarbonyl();
    }

    @ZenCodeType.Method
    public static Formula addAllHydrogens(Formula internal) {
        return internal.addAllHydrogens();
    }

    @ZenCodeType.Method
    public static Set<Atom> getAllAtoms(Formula internal) {
        return internal.getAllAtoms();
    }

    @ZenCodeType.Method
    public static List<Atom> getBondedAtomsOfElement(Formula internal, Element element) {
        return internal.getBondedAtomsOfElement(element);
    }

    @ZenCodeType.Method
    public static double getTotalBonds(Formula internal, List<Bond> bonds) {
        return internal.getTotalBonds(bonds);
    }

    @ZenCodeType.Method
    public static String toString(Formula internal) {
        return internal.serialize();
    }

    @ZenCodeType.StaticExpansionMethod
    public static Formula deserialize(String FROWNSstring) {
        return Formula.deserialize(FROWNSstring);
    }

    @ZenCodeType.Method
    public static float getCarbocationStability(Formula internal, Atom carbon, boolean isCarbanion) {
        return internal.getCarbocationStability(carbon, isCarbanion);
    }
}
