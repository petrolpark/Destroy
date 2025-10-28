package com.petrolpark.destroy.chemistry.api.transformation.context;

import java.util.Optional;

import com.petrolpark.destroy.chemistry.api.transformation.reaction.IReaction;

/**
 * Something which can provide additional information for {@link IReaction}s, such as UV power.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public interface IReactionContextProvider {

    /**
     * Get the particular {@link IReactionContext} of the given {@link IReactionContextType type}.
     * <p><strong>Do not override this method.</strong> Override the {@link IReactionContextProvider#getOptionalReactionContext(IReactionContextType) internal}.</p>
     * @param <RC> {@link IReactionContext} sub-class
     * @param reactionContextType
     * @return A non-{@code null} {@link IReactionContext}, possibly the {@link IReactionContextType#getDefault() default}
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    public default <RC extends IReactionContext<? super RC>> RC getReactionContext(IReactionContextType<RC> reactionContextType) {
        return getOptionalReactionContext(reactionContextType).orElse(reactionContextType.getDefault(this));
    };
    
    /**
     * Get an {@link Optional} which should contain the relevant {@link IReactionContext} if it exists.
     * This method should just be internal - {@link IReactionContextProvider#getReactionContext(IReactionContextType)} is the public facing method, which is guaranteed to return a result, even if it just the {@link IReactionContextType#getDefault() default}.
     * @param <RC> The class of the {@link IReactionContext}
     * @param reactionContextType The {@link IReactionContextType type} of the {@link IReactionContext} to check for
     * @return A non-{@code null} {@link Optional}
     * @see IReactionContextProvider#getReactionContext(IReactionContextType) The public-facing version of this method
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    <RC extends IReactionContext<? super RC>> Optional<RC> getOptionalReactionContext(IReactionContextType<RC> reactionContextType);

    /**
     * Whether this {@link IReactionContextProvider} specifies a {@link IReactionContext} of the given {@link IReactionContextType type} that isn't just the {@link IReactionContextType#getDefault() default}.
     * @param reactionContextType The {@link IReactionContextType type} of the {@link IReactionContext} to check for
     * @return {@code true} if a non-{@link IReactionContextType#getDefault() default} {@link IReactionContext} is specified
     * @since Destroy 0.1.0
     * @author petrolpark
     */
     public default <RC extends IReactionContext<? super RC>> boolean specifies(IReactionContextType<RC> reactionContextType) {
        return getOptionalReactionContext(reactionContextType).isPresent();
    };
};
