package OOP.PizzaShop.strategies;

import OOP.PizzaShop.enums.Size;

import java.util.Map;
import java.util.NoSuchElementException;

public class SizeBasedPricing implements PricingStrategy {
    private final Map<Size, Double> priceBySize;

    public SizeBasedPricing(Map<Size, Double> priceBySize) {
        this.priceBySize = Map.copyOf(priceBySize);
    }

    @Override
    public double getCost(PricingContext context) {
        if(priceBySize.containsKey(context.getSize())) {
           return priceBySize.get(context.getSize());
        }
        throw new NoSuchElementException("Price is not set for size '" + context.getSize() +"'");
    }
}
