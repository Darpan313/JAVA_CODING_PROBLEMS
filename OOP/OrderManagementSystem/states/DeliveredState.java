package OOP.OrderManagementSystem.states;

import OOP.OrderManagementSystem.enums.OrderEvent;
import OOP.OrderManagementSystem.enums.OrderStatus;
import OOP.OrderManagementSystem.model.Order;

public class DeliveredState implements OrderState {
    @Override
    public OrderStatus status() {
        return OrderStatus.DELIVERED;
    }

    @Override
    public OrderState handle(OrderEvent event, Order order) {
        return OrderState.super.handle(event, order);
    }
}
