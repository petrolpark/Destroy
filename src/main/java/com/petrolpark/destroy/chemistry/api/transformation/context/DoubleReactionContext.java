package com.petrolpark.destroy.chemistry.api.transformation.context;

import com.petrolpark.destroy.chemistry.minecraft.reaction.context.MinecraftReactionContextTypes;

/**
 * Implementation of {@link IReactionContext} which just wraps a {@code double} primitive.
 * @since Destroy 0.1.0
 * @author petrolpark
 * @see SimpleValueReactionContext Wrapping an object
 * @see MinecraftReactionContextTypes#SIMPLE_UV_POWER Example usage
 */
public abstract class DoubleReactionContext implements IReactionContext<DoubleReactionContext> {
    
    protected final double value;

    protected DoubleReactionContext(double value) {
        this.value = value;
    };

    public double get() {
        return value;
    };

    public static class Type implements IReactionContextType<DoubleReactionContext> {

        protected final Factory factory;
        protected final DoubleReactionContext defaultContext;

        public Type(Factory reactionContextFactory, double defaultValue) {
            factory = reactionContextFactory;
            defaultContext = of(defaultValue);
        };

        public DoubleReactionContext of(double value) {
            return factory.create(value);
        };

        @Override
        public DoubleReactionContext getDefault(IReactionContextProvider reactionContextProvider) {
            return defaultContext;
        };
        
    };

    @FunctionalInterface
    public static interface Factory {
        public DoubleReactionContext create(double value);
    };
};
