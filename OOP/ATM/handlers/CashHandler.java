package OOP.ATM.handlers;

import OOP.ATM.enums.Denomination;

import java.util.Map;

public interface CashHandler {
    void setNextHandler(CashHandler handler);
    void dispense(int amount, Map<Denomination, Integer> result);
}
