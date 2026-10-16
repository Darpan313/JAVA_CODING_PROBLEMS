package OOP.ShoppingCart.strategies;

import OOP.ShoppingCart.entities.CartItem;
import OOP.ShoppingCart.enums.DiscountType;

import java.util.List;
/*
    This strategy receives full list of cartItems. This is necessary because some strategies(buy-X-get-Y-free) need to
    inspect individual items and their categories, not just subtotal
 */
public interface DiscountStrategy {
    double calculateDiscount(List<CartItem> items);
    String getDescription();
    DiscountType getType();
}
