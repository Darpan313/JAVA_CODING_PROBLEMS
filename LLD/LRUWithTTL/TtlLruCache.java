package LLD.LRUWithTTL;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/*
    A thread safe LRU Cache with TTL expiration
 */
public class TtlLruCache<K,V> {
    private final int capacity;
    private final long ttlNanos;

    private final Map<K,Node<K,V>> cache;
    private final Node<K,V> head;
    private final Node<K,V> tail;

    // Native primitive to synchronize the Map and the Doubly Linked List.
    private final ReentrantLock lock = new ReentrantLock();

    public TtlLruCache(int capacity, long ttlDuration, TimeUnit timeUnit) {
        if(capacity<=0) throw new IllegalArgumentException("Cache capacity must be > 0.");
        this.capacity = capacity;
        this.ttlNanos = timeUnit.toNanos(ttlDuration);
        this.cache = new HashMap<>();
        this.head = new Node<>(null, null, 0);
        this.tail = new Node<>(null, null, 0);
        head.next = tail;
        tail.prev = head;
    }

    public V get(K key) {
        lock.lock();
        try {
            Node<K,V> node = cache.get(key);
            if(node == null) {
                return null;
            }

            if(isExpired(node)) {
                removeNode(node);
                cache.remove(key);
                return null;
            }

            moveToHead(node);
            return node.value;
        } finally {
            lock.unlock();
        }
    }

    public void put(K key, V value) {
        lock.lock();
        try {
            Node<K,V> existingNode = cache.get(key);
            long expiryTime = System.nanoTime() + ttlNanos;

            if(existingNode != null) {
                existingNode.value = value;
                existingNode.expiryTime = expiryTime;
                moveToHead(existingNode);
            } else {
                if(cache.size()>=capacity) {
                    Node<K,V> lruNode = tail.prev;
                    removeNode(lruNode);
                    cache.remove(lruNode.key);
                }
                Node<K,V> newNode = new Node<>(key, value, expiryTime);
                cache.put(key, newNode);
                addToHead(newNode);
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean isExpired(Node<K,V> node) {
        return System.nanoTime() > node.expiryTime;
    }

    private void removeNode(Node<K,V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    // --- Doubly Linked List Helper methods ---
    // Note: These methods assume the caller already holds the lock

    private void moveToHead(Node<K,V> node) {
        removeNode(node);
        addToHead(node);
    }

    private void addToHead(Node<K,V> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private static class Node<K,V> {
        K key;
        V value;
        long expiryTime;
        Node<K,V> prev;
        Node<K,V> next;

        Node(K key, V value, long expiryTime) {
            this.key = key;
            this.value = value;
            this.expiryTime = expiryTime;
        }
    }
}
