package OOP.ShoppingCart.observers;

import OOP.ShoppingCart.entities.Cart;
import OOP.ShoppingCart.entities.CartItem;

public class CartEventLogger implements CartObserver {
    @Override
    public void onItemAdded(Cart cart, CartItem item) {
        System.out.println("[LOG] Item added to cart " + cart.getId() + ": "
                + item.getQuantity() + "x " + item.getProduct().getName()
                + " @ $" + String.format("%.2f", item.getPriceAtAddition()));
    }

    @Override
    public void onItemRemoved(Cart cart, CartItem item) {
        System.out.println("[LOG] Item removed from cart " + cart.getId() + ": "
                + item.getProduct().getName());
    }

    @Override
    public void onCartCheckout(Cart cart) {
        System.out.println("[LOG] Cart " + cart.getId() + " checked out. Total: $"
                + String.format("%.2f", cart.getTotal()));
    }
}
