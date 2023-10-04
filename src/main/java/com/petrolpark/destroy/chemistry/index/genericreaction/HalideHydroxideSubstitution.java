package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Reaction.ReactionBuilder;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.group.HalideGroup;

public class HalideHydroxideSubstitution extends HalideSubstitution {

    public HalideHydroxideSubstitution() {
        super(Destroy.asResource("halide_hydroxide_substitution"));
    }

    @Override
    public Formula getSubstitutedGroup() {
        return Formula.alcohol();
    };

    @Override
    public void transform(ReactionBuilder builder, HalideGroup group) {
        builder.addReactant(DestroyMolecules.HYDROXIDE, 1, group.degree == 3 ? 0 : 1); // If this is a tertiary chloride, the mechanism is SN1 so hydroxide does not appear in the rate equation
    };
    
};
