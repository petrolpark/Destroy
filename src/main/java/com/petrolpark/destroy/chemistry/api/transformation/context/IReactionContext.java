package com.petrolpark.destroy.chemistry.api.transformation.context;

import com.petrolpark.destroy.chemistry.api.mixture.IMixture;
import com.petrolpark.destroy.chemistry.api.property.ITemperature;
import com.petrolpark.destroy.chemistry.api.transformation.reaction.IReaction;
import com.petrolpark.destroy.chemistry.api.mixture.IMixtureComponent;

/**
 * A condition which will affect the rate or possibility of an {@link IReaction}, such as the amount of UV light or any soluble solids.
 * This does not include:
 * <ul>
 * <li>{@link ITemperature Temperature}</li>
 * <li>{@link IMixture#getConcentration(IMixtureComponent) Concentration} of {@link IMixtureComponent solutes}
 * </ul> ...as these are handled separately.
 * <p>
 * Reaction Contexts do not have the requirement of immutability, and may be modified when {@link IReaction#getRate(IMixture, IReactionContextProvider, ITemperature) calculating the rate of}, and enacting {@link IReaction}s.
 * It would also be possible for them to be mutated when {@link IReaction#isPossible(IMixture, IReactionContextProvider, ITemperature) checking if a Reaction is possible}.
 * //TODO link to enacting reactions
 * </p>
 * @since Destroy 0.1.0
 * @author petrolpark
 * @see SimpleValueReactionContext Default implementation which wraps another object
 * @see DoubleReactionContext Implementation which wraps a {@code double} primitive
 */
public interface IReactionContext<RC extends IReactionContext<? super RC>> {
     
    /**
     * The {@link IReactionContextType type} of this {@link IReactionContext}.
     * @return Non-{@code null}, typically global static {@link IReactionContextType}
     */
    public IReactionContextType<RC> getReactionContextType();
};
