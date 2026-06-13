package DesginPatterns.FactoryPattern;

import java.util.Map;
import java.util.function.Supplier;

public class MelonFactory {

    private MelonFactory() {
        throw new AssertionError("Cannot be instantiated");
    }

    private static final TriFunction<String, Integer, String, Melon> MELON = Melon::new;

    private static final Map<String, Supplier<Fruit>> MELONS =
            Map.of("Gac", Gac::new, "Hemi", Hemi::new, "Cantaloupe", Cantaloupe::new);

    // calling constructor without args to create new instance
    public static Fruit newInstance(Class<?> clazz) {
        Supplier<Fruit> supplier = MELONS.get(clazz.getSimpleName());

        if(supplier == null) {
            throw new IllegalArgumentException("Invalid clazz argument: " + clazz);
        }

        return supplier.get();
    }

    // calling constructor with args to create new instance
    public static final Fruit newInstance(String name, int weight, String color) {
        return MELON.apply(name, weight, color);
    }
}
