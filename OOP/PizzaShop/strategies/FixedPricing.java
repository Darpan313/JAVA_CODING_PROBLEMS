package OOP.PizzaShop.strategies;

public class FixedPricing implements PricingStrategy {
    private final double price;

    public FixedPricing(double price) {
        this.price = price;
    }

    @Override
    public double getCost(PricingContext context) {
        return price;
    }
}
