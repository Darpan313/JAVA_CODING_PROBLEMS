package OOP.OrderManagementSystem.states;

import OOP.OrderManagementSystem.enums.OrderEvent;
import OOP.OrderManagementSystem.enums.OrderStatus;
import OOP.OrderManagementSystem.model.Order;

public enum ReturnedState implements OrderState {
    INSTANCE;

    @Override
    public OrderStatus status() {
        return OrderStatus.RETURNED;
    }

    @Override
    public OrderState handle(OrderEvent event, Order order) {
        return OrderState.super.handle(event, order);
    }
}
