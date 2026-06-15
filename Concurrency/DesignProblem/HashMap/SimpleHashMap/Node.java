package Concurrency.DesignProblem.HashMap.SimpleHashMap;

public class Node<K,V> {
    final int hash;
    Node<K,V> next;
    final K key;
    V value;

    public Node(int hash, K key, V value, Node<K,V> next) {
        this.hash = hash;
        this.key = key;
        this.value = value;
        this.next = next;
    }
}
