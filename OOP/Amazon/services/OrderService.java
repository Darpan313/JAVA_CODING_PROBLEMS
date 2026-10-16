package OOP.Amazon.services;

import OOP.Amazon.models.Customer;
import OOP.Amazon.models.Order;
import OOP.Amazon.models.OrderLineItem;
import OOP.Amazon.models.ShoppingCart;

import java.util.List;

public class OrderService {
    private final InventoryService inventoryService;

    public OrderService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public Order createOrder(Customer customer, ShoppingCart cart) {
        List<OrderLineItem> orderLineItems = cart.getItems().values().stream()
                .map(cartItem -> new OrderLineItem(
                        cartItem.getProduct().getId(),
                        cartItem.getProduct().getName(),
                        cartItem.getQuantity(),
                        cartItem.getProduct().getPrice()
                ))
                .toList();
        inventoryService.updateStockForOrder(orderLineItems);

        return new Order(customer, orderLineItems, customer.getShippingAddress(), cart.calculateTotal());
    }
}
