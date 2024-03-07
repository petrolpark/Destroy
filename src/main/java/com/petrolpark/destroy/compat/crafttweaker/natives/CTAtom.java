package com.petrolpark.destroy.compat.crafttweaker.natives;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import com.jozufozu.flywheel.core.PartialModel;
import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Element;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@Document("mods/destroy/Atom")
@NativeTypeRegistration(value = Atom.class, zenCodeName = "mods.destroy.Atom")
public class CTAtom {

    @ZenCodeType.Method
    public static Element getElement(Atom internal) {
        return internal.getElement();
    }

    @ZenCodeType.Method
    public static PartialModel getPartial(Atom internal) {
        return internal.getPartial();
    }

    @ZenCodeType.Method
    public static boolean isHydrogen(Atom internal) {
        return internal.isHydrogen();
    }
}
