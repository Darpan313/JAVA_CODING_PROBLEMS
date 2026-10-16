package OOP.InventoryManagement.entities;

import OOP.InventoryManagement.enums.ReservationStatus;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public final class Reservation {
    final String orderId;
    final Map<String, Integer> lines;   // sku -> qty
    final long expiresAtMillis;
    volatile ReservationStatus status = ReservationStatus.PENDING;


    public Reservation(String orderId, Map<String, Integer> lines, long expiresAtMillis) {
        this.orderId = orderId;
        // sorted by sku - why? Thread-A locks SKU-1 and wants to reserver SKU-2, but meanwhile Thread-B hold SKU-2 and
        // wants SKU-1 it's a deadlock, to avoid that we apply order in which the lock is acquired by threads.
        this.lines = Collections.unmodifiableMap(new TreeMap<>(lines));
        this.expiresAtMillis = expiresAtMillis;
    }

    public String getOrderId() {
        return orderId;
    }

    public Map<String, Integer> getLines() {
        return lines;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public long getExpiresAtMillis() {
        return expiresAtMillis;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
