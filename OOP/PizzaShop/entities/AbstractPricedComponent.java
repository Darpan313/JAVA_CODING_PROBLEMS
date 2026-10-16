package OOP.PizzaShop.entities;

import OOP.PizzaShop.strategies.PricingContext;
import OOP.PizzaShop.strategies.PricingStrategy;

/**
 * Holds the "name + delegate pricing to a strategy" behaviour once,
 * so PizzaBase / Crust / Topping stay distinct types without duplicating it.
 */
public abstract class AbstractPricedComponent implements PricedComponent {
    private final String name;
    private final PricingStrategy pricingStrategy;

    protected AbstractPricedComponent(String name, PricingStrategy pricingStrategy) {
        this.name = name;
        this.pricingStrategy = pricingStrategy;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getCost(PricingContext pricingContext) {
        return pricingStrategy.getCost(pricingContext);
    }
}
