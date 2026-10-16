package OOP.ShoppingCart.entities;

import OOP.ShoppingCart.enums.CartStatus;
import OOP.ShoppingCart.exceptions.CartException;
import OOP.ShoppingCart.observers.CartObserver;
import OOP.ShoppingCart.strategies.DiscountStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Cart {
    private final String id;
    private final Customer customer;
    private final ConcurrentHashMap<String, CartItem> items;
    private volatile CartStatus cartStatus;
    private volatile DiscountStrategy discountStrategy;
    private final CopyOnWriteArrayList<CartObserver> observers;

    public Cart(String id, Customer customer) {
        this.id = id;
        this.customer = customer;
        this.items = new ConcurrentHashMap<>();
        this.cartStatus = CartStatus.ACTIVE;
        this.discountStrategy = null;
        this.observers = new CopyOnWriteArrayList<>();
    }

    public synchronized void addItem(Product product, int quantity) {
        validateActive();
        if(quantity<=0) {
            throw new CartException("Quantity must be positive");
        }

        CartItem existing = items.get(product.getId());
        int currentQuantity = (existing != null) ? existing.getQuantity() : 0;
        int newTotal = currentQuantity + quantity;

        if(newTotal>product.getMaxQuantityPerCart()) {
            throw new CartException("Cannot add " + quantity + " units of " + product.getName()
                    + ". Maximum allowed: " + product.getMaxQuantityPerCart()
                    + ", currently in cart: " + currentQuantity);
        }

        if(existing != null) {
            existing.setQuantity(newTotal);
        }
        else {
            existing = new CartItem(product, product.getPrice(), quantity);
            items.put(product.getId(), existing);
        }
        notifyItemAdded(existing);
    }

    synchronized void removeItem(String productId) {
        validateActive();
        CartItem removed = items.remove(productId);
        if(removed == null) {
            throw new CartException("Product " + productId + " not found in cart");
        }
        notifyItemRemoved(removed);
    }

    synchronized void updateItemQuantity(String productId, int newQuantity) {
        validateActive();
        CartItem item = items.get(productId);
        if(item == null) {
            throw new CartException("Product " + productId + " not found in cart");
        }

        if(newQuantity<=0) {
            removeItem(productId);
            return;
        }

        if(newQuantity > item.getProduct().getMaxQuantityPerCart()) {
            throw new CartException("Cannot set quantity to " + newQuantity
                    + ". Maximum allowed: " + item.getProduct().getMaxQuantityPerCart());
        }
        item.setQuantity(newQuantity);
    }

    public synchronized void applyDiscount(DiscountStrategy strategy) {
        validateActive();
        this.discountStrategy = discountStrategy;
    }

    synchronized void removeDiscount() {
        validateActive();
        this.discountStrategy = null;
    }

    public synchronized void checkout() {
        validateActive();
        if(items.isEmpty()) {
            throw new CartException("Cannot checkout empty cart");
        }
        this.cartStatus = CartStatus.CHECKED_OUT;
        notifyCheckout();
    }

    synchronized void abandon() {
        validateActive();
        this.cartStatus = CartStatus.ABANDONED;
    }

    public double getSubTotal() {
        return items.values().stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    public double getTotal() {
        double subTotal = getSubTotal();
        if(discountStrategy != null) {
            double discount = discountStrategy.calculateDiscount(new ArrayList<>(items.values()));
            return Math.max(0, subTotal-discount);
        }
        return subTotal;
    }

    public void addObserver(CartObserver observer) {
        observers.add(observer);
    }

    private void validateActive() {
        if(cartStatus != CartStatus.ACTIVE) {
            throw new CartException("Cannot modify cart. Cart status is " + cartStatus);
        }
    }

    private void notifyItemAdded(CartItem item) {
        for(CartObserver observer : observers) {
            observer.onItemAdded(this, item);
        }
    }

    private void notifyItemRemoved(CartItem item) {
        for(CartObserver observer : observers) {
            observer.onItemRemoved(this, item);
        }
    }

    private void notifyCheckout() {
        for (CartObserver observer : observers) {
            observer.onCartCheckout(this);
        }
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public CartStatus getCartStatus() {
        return cartStatus;
    }

    Map<String, CartItem> getItems() {
        return Collections.unmodifiableMap(items);
    }
}
