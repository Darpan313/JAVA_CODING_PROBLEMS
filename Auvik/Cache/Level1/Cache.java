package Auvik.Cache.Level1;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/*
    Why Optional and no nulls?
    HashMap.get returns null for both "missing" and "stored null". Rejecting null values makes null/empty mean exactly
    one thing : miss. (ConcurrentHashMap rejects nulls anyway)
 */
public final class Cache {
    private final Map<CacheKey,String> data = new HashMap<>();
    private final int capacity;

    public Cache(int capacity) {
        if(capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;
    }


    public Optional<String> get(CacheKey key) {
        return Optional.ofNullable(data.get(key));
    }

    /** @return false if the cache is full and the key is new. **/
    public boolean put(CacheKey key, String value) {
        Objects.requireNonNull(value);
        if(!data.containsKey(key) && data.size() >= capacity) {
            return false;   // Level 4 replaces this with eviction
        }
        data.put(key, value);
        return true;
    }

    public void remove(CacheKey key) {
        data.remove(key);
    }
}
