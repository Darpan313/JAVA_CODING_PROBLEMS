package OOP.OrderManagementSystem.states;

import OOP.OrderManagementSystem.enums.OrderStatus;

public enum PackedState implements OrderState {
    INSTANCE;

    @Override
    public OrderStatus status() {
        return null;
    }
}
