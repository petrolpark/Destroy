package com.petrolpark.destroy.compat.crafttweaker.natives;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.MoleculeTag;
import com.petrolpark.destroy.compat.crafttweaker.action.AddMoleculeAction;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@Document("mods/destroy/MoleculeBuilder")
@NativeTypeRegistration(value = Molecule.MoleculeBuilder.class, zenCodeName = "mods.destroy.MoleculeBuilder")
public class CTMoleculeBuilder {
    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder id(Molecule.MoleculeBuilder internal, String id) {
        return internal.id(id);
    }

    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder formula(Molecule.MoleculeBuilder internal, String formula) {
        return internal.structure(Formula.deserialize(formula));
    }

    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder structure(Molecule.MoleculeBuilder internal, Formula formula) {
        return internal.structure(formula);
    }

    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder density(Molecule.MoleculeBuilder internal, float density) {
        return internal.density(density);
    }

    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder charge(Molecule.MoleculeBuilder internal, int charge) {
        return internal.charge(charge);
    }

    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder boilingPoint(Molecule.MoleculeBuilder internal, float boilingPoint) {
        return internal.boilingPoint(boilingPoint);
    }

    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder boilingPointInKelvins(Molecule.MoleculeBuilder internal, float boilingPoint) {
        return internal.boilingPointInKelvins(boilingPoint);
    }

    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder dipoleMoment(Molecule.MoleculeBuilder internal, int dipoleMoment) {
        return internal.dipoleMoment(dipoleMoment);
    }

    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder specificHeatCapacity(Molecule.MoleculeBuilder internal, float specificHeatCapacity) {
        return internal.specificHeatCapacity(specificHeatCapacity);
    }

    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder molarHeatCapacity(Molecule.MoleculeBuilder internal, float molarHeatCapacity) {
        return internal.molarHeatCapacity(molarHeatCapacity);
    }

    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder latentHeat(Molecule.MoleculeBuilder internal, float latentHeat) {
        return internal.latentHeat(latentHeat);
    }

    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder translationKey(Molecule.MoleculeBuilder internal, String translationKey) {
        return internal.translationKey(translationKey);
    }

    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder color(Molecule.MoleculeBuilder internal, int color) {
        return internal.color(color);
    }

    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder hypothetical(Molecule.MoleculeBuilder internal) {
        return internal.hypothetical();
    }

    @ZenCodeType.Method
    public static Molecule.MoleculeBuilder tag(Molecule.MoleculeBuilder internal, MoleculeTag... tags) {
        return internal.tag(tags);
    }

    @ZenCodeType.Method
    public static Molecule build(Molecule.MoleculeBuilder internal) {
        Molecule result = internal.build();
        CraftTweakerAPI.apply(new AddMoleculeAction(result));
        return result;
    }
}
