package com.petrolpark.destroy.chemistry.api.transformation;

import com.petrolpark.destroy.chemistry.api.transformation.reaction.IReacting;
import com.petrolpark.destroy.chemistry.api.transformation.reaction.IReaction;

/**
 * A {@link ITransformation} is some (physical or chemical) change to a {@link IReacting system} which proceeds at a finite {@link IRateTransformation#getRate(IReacting) rate}.
 * They are quantifiable - that is, it makes sense to say that "X moles of {@link ITransformation} occur", or that "Y moles of {@link ITransformation} occur in a second".
 * <p>These primarily include {@link IReaction}s.</p>
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public interface ITransformation<R extends IReacting<? super R>> {
    
    /**
     * Enact the effects of this {@link ITransformation}.
     * For an {@link IReaction}, for example, this would entail using up {@link IReaction.Reactant Reactants} and making {@link IReaction.Product Products}.
     * @param moles The number of moles of change which are to occur, usually greater than or equal to {@code 0d}
     * @param system The {@link IReacting} which is changing
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    public void enact(double moles, R system);
};
