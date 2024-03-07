package com.petrolpark.destroy.compat.crafttweaker.natives;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import com.jozufozu.flywheel.core.PartialModel;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.client.gui.MoleculeRenderer;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@Document("mods/destroy/Element")
@NativeTypeRegistration(value = Element.class, zenCodeName = "mods.destroy.Element")
public class CTElement {
    @ZenCodeType.Method
    public static String getSymbol(Element internal) {
        return internal.getSymbol();
    }

    @ZenCodeType.Method
    public static Float getMass(Element internal) {
        return internal.getMass();
    }

    @ZenCodeType.Method
    public static Float getElectronegativity(Element internal) {
        return internal.getElectronegativity();
    }

    @ZenCodeType.Method
    public static boolean isValidValency(Element internal, double valency) {
        return internal.isValidValency(valency);
    }

    @ZenCodeType.Method
    public static double getNextLowestValency(Element internal, double valency) {
        return internal.getNextLowestValency(valency);
    }

    @ZenCodeType.Method
    public static double getMaxValency(Element internal) {
        return internal.getMaxValency();
    }

    @ZenCodeType.Method
    public static MoleculeRenderer.Geometry getGeometry(Element internal, int connections) {
        return internal.getGeometry(connections);
    }

    @ZenCodeType.Method
    public static PartialModel getPartial(Element internal) {
        return internal.getPartial();
    }

    @ZenCodeType.Method
    public static void setPartial(Element internal, PartialModel partial) {
        internal.setPartial(partial);
    }
}
