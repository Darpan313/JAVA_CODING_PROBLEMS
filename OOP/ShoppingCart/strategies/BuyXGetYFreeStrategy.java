package OOP.ShoppingCart.strategies;

import OOP.ShoppingCart.entities.Cart;
import OOP.ShoppingCart.entities.CartItem;
import OOP.ShoppingCart.enums.DiscountType;
import OOP.ShoppingCart.enums.ProductCategory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BuyXGetYFreeStrategy implements DiscountStrategy {
    private final int buyCount;
    private final int freeCount;
    private final ProductCategory productCategory;

    public BuyXGetYFreeStrategy(int buyCount, int freeCount, ProductCategory productCategory) {
        if(buyCount<=0 || freeCount <= 0) {
            throw new IllegalArgumentException("Buy and free counts must be positive");
        }
        this.buyCount = buyCount;
        this.freeCount = freeCount;
        this.productCategory = productCategory;
    }

    @Override
    public double calculateDiscount(List<CartItem> items) {
        List<Double> eligiblePrices = new ArrayList<>();
        for(CartItem cartItem : items) {
            if(cartItem.getProduct().getCategory() == productCategory) {
                for(int i=0; i<cartItem.getQuantity(); i++) {
                    eligiblePrices.add(cartItem.getPriceAtAddition());
                }
            }
        }
        Collections.sort(eligiblePrices);

        double discount = 0;
        int groupSize = buyCount + freeCount;
        for(int i=0; i<eligiblePrices.size(); i++) {
            int positionInGroup = i%groupSize;
            if(positionInGroup<freeCount) {
                discount += eligiblePrices.get(i);
            }
        }
        return discount;
    }

    @Override
    public String getDescription() {
        return "Buy " + buyCount + " Get " + freeCount + " Free (" + productCategory + ")";
    }

    @Override
    public DiscountType getType() {
        return DiscountType.BUY_X_GET_Y_FREE;
    }
}
