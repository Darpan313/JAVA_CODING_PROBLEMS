package OOP.OrderManagementSystem.listeners;

import OOP.OrderManagementSystem.model.Order;
import OOP.OrderManagementSystem.model.StatusChange;

public interface OrderEventListener {
    void onStatusChanged(Order order, StatusChange statusChange);
}
