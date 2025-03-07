package com.petrolpark.destroy.chemistry.api.mixture;

import com.petrolpark.destroy.chemistry.api.transformation.reaction.IReacting;

/**
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public interface IReactingMultiPhaseMixture <
    P extends IReactingPhase<R, ? super C>,
    R extends IReactingMultiPhaseMixture<? super P, ? super R, ? super C> & IReactingPhase<? super R, ? super C>,
    C extends IMixtureComponent
> extends
    IReacting<R>,
    IMultiPhaseMixture<P, C>
{
    
};
