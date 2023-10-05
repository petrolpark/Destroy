package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.group.AcylChlorideGroup;

public class AcylChlorideHydration extends SingleGroupGenericReaction<AcylChlorideGroup> {

    public AcylChlorideHydration() {
        super(Destroy.asResource("acyl_chloride_hydration"), DestroyGroupTypes.ACYL_CHLORIDE);
    };

    @Override
    public Reaction generateReaction(GenericReactant<AcylChlorideGroup> reactant) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateReaction'");
    }

};
