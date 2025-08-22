package com.petrolpark.destroy.chemistry.api.util;

import java.util.Optional;

/**
 * @since Destroy 0.1.2
 * @author petrolpark
 */
public final class Holder<V> {
    
    private V held;

    public static <V> Holder<V> hold(V object) {
        return new Holder<>(object);
    };

    public Holder() {
        this(null);
    };

    public Holder(V held) {
        this.held = held;
    };

    public boolean isEmpty() {
        return held == null;
    };

    public V get() {
        return held;
    };

    public Optional<V> optional() {
        return Optional.ofNullable(held);  
    };

    public void set(V object) {
        held = object;
    };

    /**
     * {@inheritDocs}
     * @param obj
     * @return {@code true} if the supplied object is a Holder and the held object matches this, or both are {@link Holder#isEmpty() empty}
     */
    @Override
    public boolean equals(Object obj) {
        return (isEmpty() && obj instanceof Holder<?> holder && holder.isEmpty()) || obj.equals(held);
    };
};
