package OOP.PizzaShop;

import OOP.PizzaShop.entities.Crust;
import OOP.PizzaShop.entities.Pizza;
import OOP.PizzaShop.entities.PizzaBase;
import OOP.PizzaShop.entities.Topping;
import OOP.PizzaShop.enums.Size;
import OOP.PizzaShop.strategies.FixedPricing;
import OOP.PizzaShop.strategies.SizeBasedPricing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<Size, Double> pricesForBasePizzaBySize = new HashMap<>();
        pricesForBasePizzaBySize.put(Size.SMALL, 9.00);
        pricesForBasePizzaBySize.put(Size.MEDIUM, 12.00);
        pricesForBasePizzaBySize.put(Size.LARGE, 16.00);
        PizzaBase pizzaBase = new PizzaBase("Farmhouse Pizza", new SizeBasedPricing(pricesForBasePizzaBySize));
        Crust crust = new Crust("Cheese Burst", new FixedPricing(3.00));

        Map<Size, Double> pricesForExtraCheeseBySize = new HashMap<>();
        pricesForExtraCheeseBySize.put(Size.SMALL, 1.00);
        pricesForExtraCheeseBySize.put(Size.MEDIUM, 1.50);
        pricesForExtraCheeseBySize.put(Size.LARGE, 2.00);
        Topping extraCheese = new Topping("Extra Cheese", new SizeBasedPricing(pricesForExtraCheeseBySize));

        Map<Size, Double> pricesForMushRoomBySize = new HashMap<>();
        pricesForMushRoomBySize.put(Size.SMALL, 0.75);
        pricesForMushRoomBySize.put(Size.MEDIUM, 1.00);
        pricesForMushRoomBySize.put(Size.LARGE, 1.25);
        Topping mushroom = new Topping("Mushroom", new SizeBasedPricing(pricesForMushRoomBySize));
        Pizza pizza = new Pizza(pizzaBase, crust, Size.MEDIUM, List.of(extraCheese, mushroom));
        System.out.println(pizza.getCost());
    }
}
