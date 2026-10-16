package OOP.OrderManagementSystem.repositories;

import OOP.OrderManagementSystem.model.Order;

import java.util.Optional;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(String orderId);
}
