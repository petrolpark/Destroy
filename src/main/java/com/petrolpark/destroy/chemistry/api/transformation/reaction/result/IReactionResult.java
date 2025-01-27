package com.petrolpark.destroy.chemistry.api.transformation.reaction.result;

import com.petrolpark.destroy.chemistry.api.reactor.IReactor;
import com.petrolpark.destroy.chemistry.api.transformation.reaction.IReaction;

/**
 * Something which comes about as a result of an {@link IReaction}, other than a {@link IMoleculeComponent} being {@link IReaction#getProduct(com.petrolpark.destroy.chemistry.api.mixture.IMixtureComponent) produced} or {@link IHeatable heating} occuring.
 * {@link IReactionResult}s occur singly in time.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public interface IReactionResult {
    
    /**
     * 
     * @param reactor
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    public void enact(IReactor reactor);
};
