package OOP.Amazon.states;

import OOP.Amazon.enums.OrderStatus;
import OOP.Amazon.models.Order;

public class PlacedState implements OrderState{
    @Override
    public void ship(Order order) {
        System.out.println("Shipping order: " + order.getId());
        order.setCurrentOrderStatus(OrderStatus.SHIPPED);
        order.setCurrentState(new ShippedState());
    }

    @Override
    public void deliver(Order order) {
        System.out.println("Cannot deliver an order that has not been shipped.");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Cancelling order " + order.getId());
        order.setCurrentOrderStatus(OrderStatus.CANCELLED);
        order.setCurrentState(new CancelledState());
    }
}
