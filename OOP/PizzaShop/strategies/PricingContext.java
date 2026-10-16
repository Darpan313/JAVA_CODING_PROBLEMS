package OOP.PizzaShop.strategies;

import OOP.PizzaShop.enums.Size;

public class PricingContext {
    private final Size size;
    private final double baseCost;

    public PricingContext(Size size, double baseCost) {
        this.size = size;
        this.baseCost = baseCost;
    }

    public Size getSize() {
        return size;
    }

    public double getBaseCost() {
        return baseCost;
    }
}
