package OOP.VendingMachine.states;

import OOP.VendingMachine.VendingMachine;
import OOP.VendingMachine.enums.Coin;

public class OutOfServiceState implements VendingState {
    @Override
    public void insertCoin(VendingMachine m, Coin coin) {

    }

    @Override
    public void selectProduct(VendingMachine m, String code) {

    }

    @Override
    public void cancel(VendingMachine m) {

    }

    @Override
    public void dispense(VendingMachine m) {

    }

    @Override
    public String name() {
        return "";
    }
}
