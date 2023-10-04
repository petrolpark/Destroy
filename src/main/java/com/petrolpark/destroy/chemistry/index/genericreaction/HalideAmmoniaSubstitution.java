package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Reaction.ReactionBuilder;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.group.HalideGroup;

public class HalideAmmoniaSubstitution extends HalideSubstitution {

    public HalideAmmoniaSubstitution() {
        super(Destroy.asResource("halide_ammonia_substitution"));
    };

    @Override
    public Formula getSubstitutedGroup() {
        return Formula.atom(Element.NITROGEN)
            .addAtom(Element.HYDROGEN)
            .addAtom(Element.HYDROGEN);
    };

    @Override
    public void transform(ReactionBuilder builder, HalideGroup group) {
        builder.addReactant(DestroyMolecules.AMMONIA, 2, group.degree == 3 ? 1 : 2)
            .addProduct(DestroyMolecules.AMMONIUM);
    };
    
};
