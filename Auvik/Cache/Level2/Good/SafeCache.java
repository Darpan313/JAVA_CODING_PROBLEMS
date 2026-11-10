package Auvik.Cache.Level2.Good;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;

public final class SafeCache {
    private volatile ConcurrentHashMap<String, String> data = new ConcurrentHashMap<>();
    private final LongAdder hits = new LongAdder();

    public String get(String key) {
        String v = data.get(key);
        if (v!=null) hits.increment();
        return v;
    }

    public String getOrCompute(String key, Supplier<String> supplier) {
        return data.computeIfAbsent(key, k -> supplier.get());      // atomic per key
    }

    public void put(String key, String value) {
        if (key == null || key.isBlank()) throw new IllegalArgumentException("blank key");
        data.put(key, value);       // no extra lock needed: CHM put is atomic
    }

    public void replaceAll(Map<String, String> fresh) {
        data = new ConcurrentHashMap<>(fresh);  // readers see old map or new map, never half.
        // Trade-off to say out loud: a put() racing with this swap may land in the old map and be lost.
    }

    public long getHits() {
        return hits.sum();
    }

    public Map<String, String> snapshot() {
        return Map.copyOf(data);
    }
}

/*
    FIVE RULES TO REMEMBER:
    1.
 */
