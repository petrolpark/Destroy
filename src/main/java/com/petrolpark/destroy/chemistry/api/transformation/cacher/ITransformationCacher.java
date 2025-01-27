package com.petrolpark.destroy.chemistry.api.transformation.cacher;

import java.util.stream.Stream;

import com.petrolpark.destroy.chemistry.api.mixture.IMixture;
import com.petrolpark.destroy.chemistry.api.mixture.IMixtureComponent;
import com.petrolpark.destroy.chemistry.api.transformation.ITransformation;
import com.petrolpark.destroy.chemistry.api.transformation.context.IReactionContext;
import com.petrolpark.destroy.chemistry.api.transformation.reaction.IReacting;

/**
 * A store of {@link ITransformation}s possible in an {@link IReacting} {@link IMixture}, to avoid recalculating them repeatedly.
 * <p>The cache updates based on {@link ITransformationCacher#componentAdded(IMixtureComponent) added} and removed {@link ITransformationCacher#componentRemoved(IMixtureComponent) removed} {@link IMixtureComponent}s,
 * and {@link ITransformationCacher#reactionContextChanged(IReactionContext) changes} to {@link IReactionContext}s.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public interface ITransformationCacher<C extends IMixtureComponent, R extends IReacting<? super R> & IMixture<? super C>, T extends ITransformation<? extends R>> {
    
    /**
     * All {@link ITransformation}s {@link IReacting#isPossible(ITransformation) possible}.
     * <p>No guarantee is made about the order of the {@link ITransformation}s in this {@link Stream}.</p>
     * <p>This stream should not contain {@link Object#equals repeats}.</p>
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    public Stream<? extends T> getPossibleTransformations(); 

    /**
     * Notify this {@link ITransformationCacher} of the removal of a {@link IMixtureComponent} from its {@link IReacting} {@link IMixture},
     * prompting the re-evaluation of {@link IReacting#isPossible(ITransformation) possible} {@link ITransformation}s.
     * @param component The {@link IMixtureComponent} which now has {@link IMixture#getConcentration(IMixtureComponent) concentration} {@code 0d}, whereas it likely did not before.
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    public void componentRemoved(C component);

    /**
     * Notify this {@link ITransformationCacher} of the addition of a {@link IMixtureComponent} to its {@link IReacting} {@link IMixture},
     * prompting the re-evaluation of {@link IReacting#isPossible(ITransformation) possible} {@link ITransformation}s.
     * @param component The {@link IMixtureComponent} which now has {@link IMixture#getConcentration(IMixtureComponent) concentration} &gt {@code 0d}, whereas it likely had concentration {@code 0d} before
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    public void componentAdded(C component);

    /**
     * Notify this {@link ITransformationCacher} of the change to a {@link IReactionContext} of its {@link IReacting}.
     * prompting the re-evaluation of {@link IReacting#isPossible(ITransformation) possible} {@link ITransformation}s.
     * @param <RC> {@link IReactionContext} child class
     * @param newContextValue The updated value of the changed {@link IReactionContext}. This {@link ITransformationCacher} may cache the old value, but remember {@link IReactionContext}s are mutable.
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    public <RC extends IReactionContext<? super RC>> void reactionContextChanged(RC newContextValue);
};
