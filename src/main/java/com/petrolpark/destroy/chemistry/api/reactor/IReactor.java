package com.petrolpark.destroy.chemistry.api.reactor;

import java.util.Optional;

import com.petrolpark.destroy.chemistry.api.transformation.context.IReactionContextProvider;
import com.petrolpark.destroy.chemistry.api.transformation.reaction.IReacting;
import com.petrolpark.destroy.chemistry.api.transformation.reaction.IReaction;
import com.petrolpark.destroy.chemistry.api.transformation.reaction.result.IReactionResult;

/**
 * A vessel in which {@link IReacting something} can {@link IReaction react}.
 * In contrast to an {@link IReacting}, which represents the substance undergoing chemical change, instances of Reactors represent the thing in which the change is occuring.
 * Examples would be Vats, Test Tubes and Basins.
 * <p>Reactors have associated {@link IReactorProperty properties} which are used by {@link IReactionResult}s.</p>
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public interface IReactor extends IReactionContextProvider {
    
    /**
     * Get a {@link IReactorProperty} of this {@link IReactor}.
     * @param <T> The type of 
     * @param reactorProperty
     * @return An {@link Optional} containing the property if it exists for this {@link IReactor}, or an empty {@link Optional} otherwise
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    public <T> Optional<? extends T> getReactorProperty(IReactorProperty<T> reactorProperty);
};
