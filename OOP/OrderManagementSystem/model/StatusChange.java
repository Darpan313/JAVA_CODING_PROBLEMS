package OOP.OrderManagementSystem.model;

import OOP.OrderManagementSystem.enums.OrderEvent;
import OOP.OrderManagementSystem.enums.OrderStatus;

import java.time.Instant;

public record StatusChange(OrderStatus from, OrderStatus to, OrderEvent e, Instant at, String reason) {
}
