package OOP.PizzaShop.entities;

import OOP.PizzaShop.strategies.PricingStrategy;

public class PizzaBase extends AbstractPricedComponent {

    public PizzaBase(String name, PricingStrategy pricingStrategy) {
        super(name, pricingStrategy);
    }
}
