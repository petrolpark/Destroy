package com.petrolpark.destroy.chemistry.api.mixture;

/**
 * @since Destroy 0.1.2
 * @author petrolpark
 */
public interface IReactingMultiPhaseMixture <
    P extends IReactingPhase<? super R, ? super C>,
    R extends IReactingMultiPhaseMixture<? super P, ? super R, ? super C>,
    C extends IMixtureComponent
> extends
    IReactingPhase<R, C>,
    IMultiPhaseMixture<P, C>
{
    
};
