package com.petrolpark.destroy.chemistry.api.transformation;

import com.petrolpark.destroy.chemistry.api.transformation.reaction.IReacting;

import com.petrolpark.destroy.chemistry.api.mixture.IMixtureComponent;
import com.petrolpark.destroy.chemistry.api.mixture.IMultiPhaseMixture;
import com.petrolpark.destroy.chemistry.api.mixture.IPhase;

/**
 * A {@link ITransformation} that leads to the creation of a new {@link IPhase} in a {@link IMultiPhaseMixture}.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public interface ICreatePhaseTransformation<P extends IPhase<? super C>, C extends IMixtureComponent, R extends IReacting<? super R> & IMultiPhaseMixture<? super P, ? super C>> extends ITransformation<R> {
  
    //TODO
};
