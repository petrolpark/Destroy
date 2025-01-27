package com.petrolpark.destroy.chemistry.api.transformation.cacher;

import java.util.stream.Stream;

import com.petrolpark.destroy.chemistry.api.mixture.IMixture;
import com.petrolpark.destroy.chemistry.api.mixture.IMixtureComponent;
import com.petrolpark.destroy.chemistry.api.transformation.ITransformation;
import com.petrolpark.destroy.chemistry.api.transformation.context.IReactionContext;
import com.petrolpark.destroy.chemistry.api.transformation.reaction.IReacting;

/**
 * Multiple {@link ITransformationCacher}s in one.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public interface ICompoundTransformationCacher<C extends IMixtureComponent, R extends IReacting<? super R> & IMixture<? super C>, T extends ITransformation<? extends R>> extends ITransformationCacher<C, R, T> {

    /**
     * All {@link ITransformationCacher}s this {@link ICompoundTransformationCache} wraps.
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    public Stream<ITransformationCacher<? super C, ? extends R, ? extends T>> getCachers();
    
    /**
     * {@inheritDoc}
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    @Override
    public default Stream<? extends T> getPossibleTransformations() {
        return getCachers().flatMap(ITransformationCacher::getPossibleTransformations).distinct();
    };

    /**
     * {@inheritDoc}
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    @Override
    public default void componentRemoved(C component) {
        getCachers().forEach(cacher -> cacher.componentRemoved(component));
    };

    /**
     * {@inheritDoc}
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    @Override
    public default void componentAdded(C component) {
        getCachers().forEach(cacher -> cacher.componentAdded(component));
    };

    /**
     * {@inheritDoc}
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    @Override
    public default <RC extends IReactionContext<? super RC>> void reactionContextChanged(RC newContextValue) {
        getCachers().forEach(c -> c.reactionContextChanged(newContextValue));
    };
};
