package OOP.Amazon.models;

import OOP.Amazon.enums.OrderStatus;
import OOP.Amazon.observers.Subject;
import OOP.Amazon.states.OrderState;
import OOP.Amazon.states.PlacedState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order extends Subject {
    private final String id;
    private final Customer customer;
    private final List<OrderLineItem> items;
    private final Address shippingAddress;
    private final double totalAmount;
    private final LocalDateTime orderDate;
    private OrderStatus currentOrderStatus;
    private OrderState currentState;


    public Order(Customer customer, List<OrderLineItem> items, Address shippingAddress, double totalAmount) {
        this.id = UUID.randomUUID().toString().substring(0,8);
        this.customer = customer;
        this.items = items;
        this.shippingAddress = shippingAddress;
        this.totalAmount = totalAmount;
        this.orderDate = LocalDateTime.now();
        this.currentOrderStatus = OrderStatus.PLACED;
        this.currentState = new PlacedState();
        addObserver(customer);
    }

    public void shipOrder() {
        currentState.ship(this);
    }

    public void deliverOrder() {
        currentState.deliver(this);
    }

    public void cancelOrder() {
        currentState.cancel(this);
    }

    public String getId() {
        return id;
    }

    public OrderStatus getCurrentOrderStatus() {
        return currentOrderStatus;
    }

    public void setCurrentState(OrderState currentState) {
        this.currentState = currentState;
    }

    public void setCurrentOrderStatus(OrderStatus currentOrderStatus) {
        this.currentOrderStatus = currentOrderStatus;
        notifyObservers(this);
    }

    public List<OrderLineItem> getItems() {
        return items;
    }
}
