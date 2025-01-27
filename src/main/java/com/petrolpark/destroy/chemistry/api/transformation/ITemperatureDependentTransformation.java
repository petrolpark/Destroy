package com.petrolpark.destroy.chemistry.api.transformation;

import com.petrolpark.destroy.chemistry.api.property.ITemperature;
import com.petrolpark.destroy.chemistry.api.transformation.reaction.IReacting;

/**
 * A {@link ITransformation} which occurs at a {@link IRateTransformation#getRate(IReacting) rate} depending on the {@link ITemperature} of its {@link IReacting system}.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public interface ITemperatureDependentTransformation<R extends IReacting<? super R> & ITemperature> extends IRateTransformation<R> {};
