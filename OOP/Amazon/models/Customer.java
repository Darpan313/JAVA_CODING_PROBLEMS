package OOP.Amazon.models;

import OOP.Amazon.observers.OrderObserver;

import java.util.UUID;

/*
    Customer represents shopper and also plays a role of an observer.
 */
public class Customer implements OrderObserver {
    private final String id;
    private final String name;
    private final String email;
    private final Account account;
    private Address shippingAddress;

    public Customer(String id, String name, String email, String password, Address shippingAddress) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.account = new Account(email, password);
        this.shippingAddress = shippingAddress;
        // how to model customer with multiple shipping address, and select address during placing order?
    }


    @Override
    public void update(Order order) {
        System.out.printf("[Notification for %s]: Your order #%s status has been updated to: %s.%n",
                this.name, order.getId(), order.getCurrentOrderStatus());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Account getAccount() {
        return account;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
