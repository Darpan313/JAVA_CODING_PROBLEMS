package OOP.Amazon.observers;

import OOP.Amazon.models.Order;

public interface OrderObserver {
    void update(Order order);
}
