package OOP.VendingMachine.states;

import OOP.VendingMachine.VendingMachine;
import OOP.VendingMachine.enums.Coin;

public interface VendingState {
    void insertCoin(VendingMachine m, Coin coin);
    void selectProduct(VendingMachine m, String code);
    void cancel(VendingMachine m);
    void dispense(VendingMachine m);
    String name();
}
