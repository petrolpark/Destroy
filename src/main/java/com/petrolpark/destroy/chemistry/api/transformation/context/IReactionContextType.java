package com.petrolpark.destroy.chemistry.api.transformation.context;

/**
 * A type of {@link IReactionContext}.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public interface IReactionContextType<RC extends IReactionContext<? super RC>> {
    
    /**
     * Get the typical {@link IReactionContext} if the {@link IReactionContextProvider} does not specify one. For example, the default {@link IReactionContext} representing UV power to a {@link IMixture} might be one representing <em>no</em> UV power.
     * @param reactionContextProvider The {@link IReactionContextProvider} querying the default {@link IReactionContext} of this type. Typically the return value of this method would not depend on this, but it's there if you need it.
     * @return A non-{@code null}
     */
    public RC getDefault(IReactionContextProvider reactionContextProvider);
};
