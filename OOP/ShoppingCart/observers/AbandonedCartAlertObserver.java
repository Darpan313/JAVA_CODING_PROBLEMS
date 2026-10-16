package OOP.ShoppingCart.observers;

import OOP.ShoppingCart.entities.Cart;
import OOP.ShoppingCart.entities.CartItem;

public class AbandonedCartAlertObserver implements CartObserver {
    private volatile long lastActivityTime;

    public AbandonedCartAlertObserver() {
        this.lastActivityTime = System.currentTimeMillis();
    }
    @Override
    public void onItemAdded(Cart cart, CartItem item) {
        lastActivityTime = System.currentTimeMillis();
    }

    @Override
    public void onItemRemoved(Cart cart, CartItem item) {
        lastActivityTime = System.currentTimeMillis();
    }

    @Override
    public void onCartCheckout(Cart cart) {
        lastActivityTime = System.currentTimeMillis();
    }

    long getLastActivityTime() {
        return lastActivityTime;
    }
}
