package OOP.ConfigurableCache;

public class ConfigurableCache<K,V> {
    private final Storage<K,V> storage;
    private final EvictionPolicy<K> evictionPolicy;


    public ConfigurableCache(Storage<K, V> storage, EvictionPolicy<K> evictionPolicy) {
        this.storage = storage;
        this.evictionPolicy = evictionPolicy;
    }

    public void put( K key, V value) {
        if(storage.isFull()) {
            K evictedKey = evictionPolicy.evictKey();
            storage.remove(evictedKey);
        }
        storage.add(key, value);
        evictionPolicy.accessKey(key);
    }

    public V get(K key) {
        V value = storage.get(key);
        if(value != null) {
            evictionPolicy.accessKey(key);
        }
        return value;
    }

    public void remove(K key) {
        evictionPolicy.evictKey(key);
        storage.remove(key);
    }
}
