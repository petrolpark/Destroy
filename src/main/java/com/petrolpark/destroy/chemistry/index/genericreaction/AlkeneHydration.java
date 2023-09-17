package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.group.AlkeneGroup;

public class AlkeneHydration extends SingleGroupGenericReaction<AlkeneGroup> {

    public AlkeneHydration() {
        super(Destroy.asResource("alkene_hydration"), DestroyGroupTypes.ALKENE_GROUP);
    };

    @Override
    public Reaction generateReaction(GenericReactant<AlkeneGroup> reactant) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateReaction'");
    }

    
};
