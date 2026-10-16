package OOP.ConfigurableCache;

public interface EvictionPolicy<K> {
    K evictKey();
    void accessKey(K key);
    void evictKey(K key);
}
