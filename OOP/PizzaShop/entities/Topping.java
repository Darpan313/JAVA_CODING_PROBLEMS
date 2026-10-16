package OOP.PizzaShop.entities;

import OOP.PizzaShop.strategies.PricingStrategy;

public class Topping extends AbstractPricedComponent {

    public Topping(String name, PricingStrategy pricingStrategy) {
        super(name, pricingStrategy);
    }
}
