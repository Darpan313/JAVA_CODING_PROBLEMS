package OOP.OrderManagementSystem.listeners;

import OOP.OrderManagementSystem.enums.OrderStatus;
import OOP.OrderManagementSystem.model.Order;
import OOP.OrderManagementSystem.model.StatusChange;

public class RefundListener implements OrderEventListener {
    @Override
    public void onStatusChanged(Order order, StatusChange statusChange) {
        boolean wasPaid = statusChange.from() == OrderStatus.PAID || statusChange.from() == OrderStatus.PACKED;
        if(statusChange.to() == OrderStatus.CANCELLED && wasPaid || statusChange.to() == OrderStatus.RETURNED) {
            System.out.printf("  [Refund] %d cents to customer %s%n", order.totalCents(), order.getCustomerId());
        }
    }
}
