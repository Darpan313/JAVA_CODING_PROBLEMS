package Concurrency.DesignProblem.HashMap.SimpleHashMap;

public class SimpleHashMap<K,V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Node<K, V>[] table;
    private int size;

    public SimpleHashMap() {
        table = new Node[DEFAULT_CAPACITY];
    }

    public V put(K key, V value) {
        int hash = hash(key);
        int index = hash & (table.length - 1); // why &?

        for(Node<K,V> node = table[index]; node != null; node = node.next) {
            if(node.hash == hash && key.equals(node.key)) {
                V oldValue = node.value;
                node.value = value;
                return oldValue;
            }
        }

        table[index] = new Node<>(hash, key, value, table[index]);
        if(++size > table.length * LOAD_FACTOR) {
            resize();
        }
        return null;
    }

    public V get(K key) {
        int hash = hash(key);
        int index = hash & (table.length - 1);

        for(Node<K,V> node = table[index]; node != null; node = node.next) {
            if(node.hash == hash && key.equals(node.key)) {
                return node.value;
            }
        }
        return null;
    }

    private int hash(K key) {
        int h = key.hashCode();
        return h ^ (h>>16); // why this
    }

    private void resize() {
        // Double the capacity and rehash
    }
}
