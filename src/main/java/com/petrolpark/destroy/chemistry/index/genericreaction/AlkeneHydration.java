package com.petrolpark.destroy.chemistry.index.genericreaction;

import java.util.function.Supplier;

import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.group.AlkeneGroup;

public class AlkeneHydration extends SingleGroupGenericReaction<AlkeneGroup> {

    public AlkeneHydration(Supplier<AlkeneGroup> supplier) {
        super(supplier);
    };

    @Override
    public Reaction generateReaction(GenericReactant<AlkeneGroup> reactant) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateReaction'");
    };
    
};
