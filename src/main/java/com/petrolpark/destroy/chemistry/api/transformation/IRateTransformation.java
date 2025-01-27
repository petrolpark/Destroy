package com.petrolpark.destroy.chemistry.api.transformation;

import com.petrolpark.destroy.chemistry.api.transformation.reaction.IReacting;

/**
 * An {@link ITransformation} which occurs at a {@link IRateTransformation#getRate(IReacting) rate} that depends on properties of {@link IReacting where it occurs}.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public interface IRateTransformation<R extends IReacting<? super R>> extends ITransformation<R> {
    
    /**
     * Get the number of moles of {@link ITransformation} which should {@link ITransformation#enact(double, IReacting) proceed} every second.
     * @param system The {@link IReacting} in which this {@link ITransformation} is occuring.
     * @return A rate which is typically greater than or equal to {@code 0d}, but may be negative.
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    public double getRate(R system);
};
