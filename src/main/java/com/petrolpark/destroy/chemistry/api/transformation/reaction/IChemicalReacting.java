package com.petrolpark.destroy.chemistry.api.transformation.reaction;

import com.petrolpark.destroy.chemistry.api.mixture.IMixture;
import com.petrolpark.destroy.chemistry.api.mixture.IMixtureComponent;
import com.petrolpark.destroy.chemistry.api.property.ITemperature;

/**
 * A {@link IReacting system} that facilitates {@link IChemicalReaction}s.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public interface IChemicalReacting extends IReacting<IChemicalReacting>, ITemperature, IMixture<IMixtureComponent> {
    
};
