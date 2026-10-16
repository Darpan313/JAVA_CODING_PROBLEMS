package OOP.OrderManagementSystem.states;

import OOP.OrderManagementSystem.enums.OrderEvent;
import OOP.OrderManagementSystem.enums.OrderStatus;
import OOP.OrderManagementSystem.model.Order;

public enum PaidState implements OrderState {
    INSTANCE;

    @Override
    public OrderStatus status() {
        return OrderStatus.PAID;
    }

    @Override
    public OrderState handle(OrderEvent event, Order order) {
        return switch (event) {
            case PACK -> PackedState.INSTANCE;
            case CANCEL -> CancelledState.INSTANCE;
            default -> OrderState.super.handle(event, order);
        };
    }
}
