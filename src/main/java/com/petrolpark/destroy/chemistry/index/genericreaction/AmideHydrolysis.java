package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.group.AmideGroup;

public class AmideHydrolysis extends SingleGroupGenericReaction<AmideGroup> {
    
    public AmideHydrolysis() {
        super(Destroy.asResource("amide_hydrolysis"), DestroyGroupTypes.AMIDE);
    };

    @Override
    public Reaction generateReaction(GenericReactant<AmideGroup> reactant) {
        //TODO
        throw new UnsupportedOperationException("Not implemented yet");
    };
};
