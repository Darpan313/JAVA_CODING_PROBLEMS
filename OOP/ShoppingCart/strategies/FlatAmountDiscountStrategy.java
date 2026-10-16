package OOP.ShoppingCart.strategies;

import OOP.ShoppingCart.entities.CartItem;
import OOP.ShoppingCart.enums.DiscountType;

import java.util.List;

public class FlatAmountDiscountStrategy implements DiscountStrategy {
    private final double amount;

    public FlatAmountDiscountStrategy(double amount) {
        if(amount<0) {
            throw new IllegalArgumentException("Discount amount cannot be negative");
        }
        this.amount = amount;
    }

    @Override
    public double calculateDiscount(List<CartItem> items) {
        double subtotal = items.stream().mapToDouble(CartItem::getSubtotal).sum();
        return Math.min(amount, subtotal);
    }

    @Override
    public String getDescription() {
        return "$" + String.format("%.2f", amount) + "  off";
    }

    @Override
    public DiscountType getType() {
        return DiscountType.FLAT_AMOUNT;
    }
}
