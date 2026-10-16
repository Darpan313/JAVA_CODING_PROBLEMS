package OOP.PizzaShop.entities;

import OOP.PizzaShop.enums.Size;
import OOP.PizzaShop.strategies.PricingContext;

import java.util.ArrayList;
import java.util.List;

public class Pizza {
    private final PizzaBase pizzaBase;
    private final Crust crust;
    private final Size size;
    private final List<Topping> toppings;

    public Pizza(PizzaBase pizzaBase, Crust crust, Size size, List<Topping> toppings) {
        this.pizzaBase = pizzaBase;
        this.crust = crust == null ? Crust.classicHandTossed() : crust;
        this.size = size;
        this.toppings = List.copyOf(toppings);
    }

    public double getCost() {
        // Phase 1: price the base. baseCost is not known yet, and no base
        // strategy can depend on it, so 0.0 is a safe placeholder.
        double baseCost = pizzaBase.getCost(new PricingContext(size, 0.0));

        // Phase 2: baseCost is now known once, so add-ons may depend on it.
        PricingContext context = new PricingContext(size, baseCost);

        double total = baseCost;
        total += crust.getCost(context);
        for (Topping topping : toppings) {
            total += topping.getCost(context);
        }
        return total;
    }

    public Size getSize() {
        return size;
    }

    public PizzaBase getPizzaBase() {
        return pizzaBase;
    }

    public Crust getCrust() {
        return crust;
    }

    public List<Topping> getToppings() {
        return toppings;
    }

    /** Base, crust and toppings in bill order — everything the receipt prints. */
    public List<PricedComponent> getComponents() {
        List<PricedComponent> components = new ArrayList<>();
        components.add(pizzaBase);
        components.add(crust);
        components.addAll(toppings);
        return components;
    }
}
