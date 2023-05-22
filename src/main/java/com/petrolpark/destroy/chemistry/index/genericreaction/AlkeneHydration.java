package com.petrolpark.destroy.chemistry.index.genericReaction;

import java.util.function.Supplier;

import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.genericReaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericReaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.group.AlkeneGroup;

public class AlkeneHydration extends SingleGroupGenericReaction<AlkeneGroup> {

    public AlkeneHydration(Supplier<AlkeneGroup> supplier) {
        super(supplier);
        //TODO Auto-generated constructor stub
    };

    @Override
    public Reaction generateReaction(GenericReactant<AlkeneGroup> reactant) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateReaction'");
    };
    
};
