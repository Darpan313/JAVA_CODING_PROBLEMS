package Auvik.Cache.Level2;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;

public final class SafeCache<K,V> {
    private final ConcurrentHashMap<K, V> data = new ConcurrentHashMap<>();
    private final LongAdder hits = new LongAdder();
    private final LongAdder misses = new LongAdder();

    public Optional<V> get(K key) {
        V v = data.get(key);
        (v!=null ? hits : misses).increment();
        return Optional.ofNullable(v);
    }

    public void put(K key, V value) {
        data.put(key, value);   //CHM throws NPE on null key/value
    }

    /** Atomic: the function runs at most once per absent key. Keep it fast **/
    public V getOrCompute(K key, Function<? super K, ? extends V> fn) {
        return data.computeIfAbsent(key, fn);
    }
}
