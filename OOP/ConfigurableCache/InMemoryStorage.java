package OOP.ConfigurableCache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorage<K,V> implements Storage<K,V> {
    private final Map<K,V> internalMap;
    private final int capacity;

    public InMemoryStorage(Map<K, V> internalMap, int capacity) {
        this.internalMap = new ConcurrentHashMap<>();
        this.capacity = capacity;
    }

    @Override
    public void add(K key, V value) {
        internalMap.put(key, value);
    }

    @Override
    public V get(K key) {
        return internalMap.get(key);
    }

    @Override
    public void remove(K key) {
        internalMap.remove(key);
    }

    @Override
    public boolean isFull() {
        return internalMap.size() == capacity;
    }
}
