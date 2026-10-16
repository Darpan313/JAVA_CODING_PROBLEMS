package OOP.PizzaShop.entities;

import OOP.PizzaShop.strategies.FixedPricing;
import OOP.PizzaShop.strategies.PricingStrategy;

public class Crust extends AbstractPricedComponent {

    public Crust(String name, PricingStrategy pricingStrategy) {
        super(name, pricingStrategy);
    }

    /** Null Object: the no-upgrade default, so Pizza never needs a null check. */
    public static Crust classicHandTossed() {
        return new Crust("Classic Hand-Tossed", new FixedPricing(0.0));
    }
}
