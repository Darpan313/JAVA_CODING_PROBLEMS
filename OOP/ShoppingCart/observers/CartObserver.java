package OOP.ShoppingCart.observers;

import OOP.ShoppingCart.entities.Cart;
import OOP.ShoppingCart.entities.CartItem;

/*
    The observer interface recieves the Cart and affected CartItem (Where applicable). Observers get enough context to
    log, alert or track without needing to query additional state.
 */
public interface CartObserver {
    void onItemAdded(Cart cart, CartItem item);
    void onItemRemoved(Cart cart, CartItem item);
    void onCartCheckout(Cart cart);
}
