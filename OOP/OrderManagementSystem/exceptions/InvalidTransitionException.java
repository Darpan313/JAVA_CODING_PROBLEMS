package OOP.OrderManagementSystem.exceptions;

import OOP.OrderManagementSystem.enums.OrderEvent;
import OOP.OrderManagementSystem.enums.OrderStatus;
import OOP.OrderManagementSystem.model.Order;

public class InvalidTransitionException extends RuntimeException {
    public InvalidTransitionException(OrderStatus from, OrderEvent event) {
        super("Cannot apply " + event + " when order is " + from);
    }
}
