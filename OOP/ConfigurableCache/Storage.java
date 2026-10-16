package OOP.ConfigurableCache;

public interface Storage<K,V> {
    void add(K key, V value);
    V get(K key);
    void remove(K key);
    boolean isFull();
}
