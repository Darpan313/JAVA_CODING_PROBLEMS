package OOP.InventoryManagement.entities;

import java.util.concurrent.locks.ReentrantLock;

public final class InventoryItem {
    private final String sku;
    private int onHand;     // physical units in the warehouse
    private int reserved;   // units held by PENDING reservations
    private final ReentrantLock lock = new ReentrantLock();

    public InventoryItem(String sku, int onHand) {
        this.sku = sku;
        this.onHand = onHand;
    }

    public ReentrantLock lock() {
        return lock;
    }

    public String sku() {
        return sku;
    }

    // All the following must be called while holding lock;
    public int available() {
        return onHand - reserved;
    }

    public void addOnHand(int qty) {
        onHand += qty;
    }

    public void hold(int qty) {
        reserved += qty;
    }

    public void unhold(int qty) {
        reserved -= qty;
    }

    public void consume(int qty) {
        onHand -= qty;
        reserved -= qty;
    }
}
