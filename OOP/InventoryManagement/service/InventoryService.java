package OOP.InventoryManagement.service;

import OOP.InventoryManagement.entities.InventoryItem;
import OOP.InventoryManagement.entities.Reservation;
import OOP.InventoryManagement.enums.ReservationStatus;
import OOP.InventoryManagement.exceptions.InsufficientStockException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class InventoryService {
    private final ConcurrentHashMap<String, InventoryItem> items = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Reservation> reservations = new ConcurrentHashMap<>();
    private final long ttlMillis;

    public InventoryService(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }

    public void addStock(String sku, int qty) {
        requirePositiveQty(qty);
        InventoryItem item = items.computeIfAbsent(sku, s -> new InventoryItem(s,0));
        item.lock().lock();
        try{
            item.addOnHand(qty);
        } finally {
            item.lock().unlock();
        }
    }

    public int getAvailable(String sku) {
        InventoryItem item = items.get(sku);
        if(item == null) return 0;
        item.lock().lock();
        try {
            return item.available();
        } finally {
            item.lock().unlock();
        }
    }

    /* All-or-nothing reservation across SKUs. Idempotent on orderId */
    public Reservation reserve(String orderId, Map<String, Integer> lines) {
        Reservation existing = reservations.get(orderId);
        if(existing != null) return existing;

        lines.values().forEach(this::requirePositiveQty);
        Reservation res = new Reservation(orderId, lines, System.currentTimeMillis() + ttlMillis);
        List<InventoryItem> locked = lockAll(res.getLines().keySet());
        try {
            // Phase-1: check everything before touching anything
            for(InventoryItem item : locked) {
                int wanted = res.getLines().get(item.sku());
                if(item.available() < wanted) {
                    throw new InsufficientStockException(item.sku(), wanted, item.available());
                }
            }

            // Idompotency race: another thread may have registered same orderId meanwhile
            Reservation prior = reservations.putIfAbsent(orderId, res);
            if(prior != null) return prior;

            // Phase 2: apply
            for(InventoryItem item : locked) {
                item.hold(res.getLines().get(item.sku()));
            }
            return res;
        } finally {
            unlockAll(locked);
        }
    }

    /* Payment succeded: convert the hold into real deduction */
    public void confirm(String orderId) {
        transition(orderId, ReservationStatus.CONFIRMED, InventoryItem::consume);
    }

    /* Cancelled or payment failed: give units back */
    public void release(String orderId) {
        transition(orderId, ReservationStatus.RELEASED, InventoryItem::unhold);
    }

    /* Called by a scheduler (e.g. every few seconds) */
    public void expireStale() {
        long now = System.currentTimeMillis();
        for(Reservation res : reservations.values()) {
            if(res.getStatus() == ReservationStatus.PENDING && res.getExpiresAtMillis() <= now) {
                transition(res.getOrderId(), ReservationStatus.EXPIRED, InventoryItem::unhold);
            }
        }
    }

    private interface LineOp {
        void apply(InventoryItem item, int qty);
    }

    private void transition(String orderId, ReservationStatus target, LineOp op) {
        Reservation res = reservations.get(orderId);
        if(res == null) throw new NoSuchElementException("unknown order " + orderId);
        List<InventoryItem> locked = lockAll(res.getLines().keySet());
        try {
            if(res.getStatus() != ReservationStatus.PENDING) return; // already terminal, idempotent
            for(InventoryItem item : locked) {
                op.apply(item, res.getLines().get(item.sku()));
            }
            res.setStatus(target);
        } finally {
            unlockAll(locked);
        }
    }

    /* Locks in sorted SKU order to prevent deadlock, missing SKUs are created empty */
    private List<InventoryItem> lockAll(Set<String> skus) {
        List<InventoryItem> list = new ArrayList<>();
        for(String sku : skus) {
            InventoryItem item = items.computeIfAbsent(sku, s->new InventoryItem(s,0));
            item.lock().lock();
            list.add(item);
        }
        return list;
    }

    private void unlockAll(List<InventoryItem> locked) {
        for(int i=locked.size()-1; i>=0; i--) {
            locked.get(i).lock().unlock();
        }
    }

    private void requirePositiveQty(int qty) {
        if(qty<=0) throw new IllegalArgumentException("qty must be > 0");
    }
}
