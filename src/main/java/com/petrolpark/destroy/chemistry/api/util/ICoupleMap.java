package com.petrolpark.destroy.chemistry.api.util;

import java.util.stream.Stream;

/**
 * A 2D Map.
 * @since Destroy 0.1.2
 * @author petrolpark
 */
public interface ICoupleMap<K, V> {
    
    public Stream<V> streamAllKeys(K key);
};
