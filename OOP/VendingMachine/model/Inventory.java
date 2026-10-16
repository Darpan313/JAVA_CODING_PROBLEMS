package OOP.VendingMachine.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Inventory {
    private final Map<String, Slot> slots = new HashMap<>();

    public void addSlot(Slot s) {
        slots.put(s.getCode(), s);
    }

    public Optional<Slot> getSlot(String code) {
        return Optional.ofNullable(slots.get(code));
    }

    public void refill(String code, int qty) {
        getSlot(code).orElseThrow().quantity += qty;
    }
}
