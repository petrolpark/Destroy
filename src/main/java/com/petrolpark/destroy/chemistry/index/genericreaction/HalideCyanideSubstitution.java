package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.Reaction.ReactionBuilder;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.group.HalideGroup;

public class HalideCyanideSubstitution extends HalideSubstitution {

    public HalideCyanideSubstitution() {
        super(Destroy.asResource("halide_cyanide_substitution"));
    };

    @Override
    public Formula getSubstitutedGroup() {
        return Formula.atom(Element.CARBON)
            .addAtom(Element.NITROGEN, BondType.TRIPLE);
    };

    @Override
    public void transform(ReactionBuilder builder, HalideGroup group) {
        builder.addReactant(DestroyMolecules.CYANIDE, 1, group.degree == 3 ? 0 : 1);
    };
    
};
