package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.group.ChlorideGroup;

public class DestroyGenericReactions {

    private static final Atom fauxAtom = new Atom(Element.R_GROUP);

    public static final HydroxideSubstitutions HYDROXIDE_SUBSTITUTION = new HydroxideSubstitutions(ChlorideGroup::new);

    public static void register() {};
}
