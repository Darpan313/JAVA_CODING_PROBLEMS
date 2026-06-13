package ImplementProblems.LRUCache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class LRUCache<K, V> {
    private final ReentrantLock lock = new ReentrantLock();
    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    private final DoublyLinkedList<K, V> dll;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.dll = new DoublyLinkedList<>();
    }

    public V get(K key) {
        lock.lock();
        try {
            Node<K, V> node = cache.get(key);
            if (node == null) return null;
            dll.moveToFront(node);
            return node.value;
        } finally {
            lock.unlock();
        }
    }

    public void put(K key, V value) {
        lock.lock();
        try {
            if (cache.containsKey(key)) {
                Node<K, V> node = cache.get(key);
                node.value = value;
                dll.moveToFront(node);
            } else {
                if (cache.size() == capacity) {
                    Node<K, V> lru = dll.removeLast();
                    if (lru != null) cache.remove(lru.key);
                }
                Node<K, V> newNode = new Node<>(key, value);
                dll.addFirst(newNode);
                cache.put(key, newNode);
            }
        } finally {
            lock.unlock();
        }

    }

    public void remove(K key) {
        lock.lock();
        try {
            Node<K, V> node = cache.get(key);
            if (node == null) return;
            dll.remove(node);
            cache.remove(key);
        } finally {
            lock.unlock();
        }
    }
}
