package OOP.OrderManagementSystem.model;

import OOP.OrderManagementSystem.enums.OrderEvent;
import OOP.OrderManagementSystem.enums.OrderStatus;
import OOP.OrderManagementSystem.states.CreatedState;
import OOP.OrderManagementSystem.states.OrderState;

import java.time.Instant;
import java.util.*;

public final class Order {
    private final String orderId;
    private final String customerId;
    private final List<OrderItem> items;
    private final List<StatusChange> history = new ArrayList<>();
    private final Set<String> appliedIdempotencyKeys = new HashSet<>();
    private OrderState state = CreatedState.INSTANCE;
    private long version = 0;


    public Order(String orderId, String customerId, List<OrderItem> items) {
        if(items == null || items.isEmpty()) throw new IllegalArgumentException("Items cannot be empty");
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public List<StatusChange> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public long totalCents() {
        return items.stream().mapToLong(OrderItem::getUnitPriceCents).sum();
    }

    public String getCustomerId() {
        return customerId;
    }

    boolean markProcessed(String idempotencyKey) {
        return idempotencyKey == null || appliedIdempotencyKeys.add(idempotencyKey);
    }

    void apply(OrderEvent event, String reason) {
        OrderState next = state.handle(event, this);
        history.add(new StatusChange(state.status(), next.status(), event, Instant.now(), reason));
        state = next;
        version++;
    }


}
