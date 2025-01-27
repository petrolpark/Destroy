package com.petrolpark.destroy.chemistry.api.transformation.context;

/**
 * Default implementation of {@link IReactionContext} which just wraps another object.
 * It is recommended that this is used for objects defined by other libraries and that native objects should just implement {@link IReactionContext}.
 * @since Destroy 0.1.0
 * @author petrolpark
 * @see DoubleReactionContext Implementation which wraps a {@code double} primitive
 */
public abstract class SimpleValueReactionContext<T> implements IReactionContext<SimpleValueReactionContext<T>> {

    protected final T value;

    protected SimpleValueReactionContext(T value) {
        this.value = value;
    };

    public T get() {
        return value;
    };

    public static class Type<T> implements IReactionContextType<SimpleValueReactionContext<T>> {

        protected final Factory<T> factory;
        protected final SimpleValueReactionContext<T> defaultContext;

        public Type(Factory<T> reactionContextFactory, T defaultValue) {
            factory = reactionContextFactory;
            defaultContext = of(defaultValue);
        };

        public SimpleValueReactionContext<T> of(T value) {
            return factory.create(value);
        };

        @Override
        public SimpleValueReactionContext<T> getDefault(IReactionContextProvider reactionContextProvider) {
            return defaultContext;
        };

    };

    @FunctionalInterface
    public static interface Factory<T> {
        public SimpleValueReactionContext<T> create(T value);
    };
    
};
