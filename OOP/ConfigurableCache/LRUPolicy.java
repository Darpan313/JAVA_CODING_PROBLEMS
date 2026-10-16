package OOP.ConfigurableCache;

import java.util.LinkedHashSet;

public class LRUPolicy<K> implements EvictionPolicy<K> {
    LinkedHashSet<K> set;
    public LRUPolicy() {
        set = new LinkedHashSet<>();
    }
    @Override
    public K evictKey() {
        return set.removeFirst();
    }

    @Override
    public void accessKey(K key) {
        set.remove(key);
        set.addLast(key);
    }

    @Override
    public void evictKey(K key) {
        set.remove(key);
    }
}
