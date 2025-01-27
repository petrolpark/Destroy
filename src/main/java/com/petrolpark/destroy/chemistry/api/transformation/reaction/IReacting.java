package com.petrolpark.destroy.chemistry.api.transformation.reaction;

import com.petrolpark.destroy.chemistry.api.reactor.IReactor;
import com.petrolpark.destroy.chemistry.api.transformation.ITransformation;
import com.petrolpark.destroy.chemistry.api.transformation.context.IReactionContextProvider;
import com.petrolpark.destroy.chemistry.api.mixture.IMixture;

/**
 * Something in which {@link IReaction}s or other {@link ITransformation}s occur, typically a {@link IMixture}.
 * In contrast to an {@link IReactor}, which represents the thing in which the chemical change is occuring, {@link IReacting}s represent the substance undergoing change.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public interface IReacting<R extends IReacting<? super R>> extends IReactionContextProvider {
    
    /**
     * Whether a particular {@link ITransformation} is possible in this {@link IReacting}.
     * <strong>This is the be-all-and-end-all as to whether an {@link IReaction} is possible - calling {@link IReaction#isPossible(IReacting)} is not sufficient.</strong>
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    public boolean isPossible(ITransformation<? extends R> transformation);

    /**
     * Simulate the {@link ITransformation}s applicable to this {@link IReacting} for a short while.
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    public void react();

    /**
     * Whether any {@link ITransformation}s are currently occuring
     * - equivalently, whether calling {@link IReacting#react()} will result in a change in state.
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    public boolean isAtEquilibrium();
};
