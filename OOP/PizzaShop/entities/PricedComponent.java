package OOP.PizzaShop.entities;

import OOP.PizzaShop.strategies.PricingContext;

/**
 * Anything that shows up as a named, priced line on the bill.
 * The receipt only ever needs this much.
 */
public interface PricedComponent {
    String getName();

    double getCost(PricingContext pricingContext);
}
