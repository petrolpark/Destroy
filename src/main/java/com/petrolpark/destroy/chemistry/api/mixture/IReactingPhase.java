package com.petrolpark.destroy.chemistry.api.mixture;

import com.petrolpark.destroy.chemistry.api.transformation.reaction.IReacting;

/**
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public interface IReactingPhase <R extends IReactingPhase<? super R, ? super C>, C extends IMixtureComponent> extends IPhase<C>, IReacting<R> {
    
};
