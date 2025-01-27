package com.petrolpark.destroy.chemistry.api.transformation.reaction;

import com.petrolpark.destroy.chemistry.api.species.ISpecies;

/**
 * A reaction between {@link ISpecies}, which produces {@link ISpecies}.
 * @since 1.0
 * @author petrolpark
 */
public interface IChemicalReaction extends IReaction<IChemicalReacting, ISpecies> {
    
};
