package OOP.OrderManagementSystem.states;

import OOP.OrderManagementSystem.enums.OrderEvent;
import OOP.OrderManagementSystem.enums.OrderStatus;
import OOP.OrderManagementSystem.exceptions.InvalidTransitionException;
import OOP.OrderManagementSystem.model.Order;

public interface OrderState {
    OrderStatus status();

    default OrderState handle(OrderEvent event, Order order) {
        throw new InvalidTransitionException(status(), event);
    }
}
