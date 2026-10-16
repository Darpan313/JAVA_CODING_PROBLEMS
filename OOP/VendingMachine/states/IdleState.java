package OOP.VendingMachine.states;

import OOP.VendingMachine.VendingMachine;
import OOP.VendingMachine.enums.Coin;

public class IdleState implements VendingState {
    @Override
    public void insertCoin(VendingMachine m, Coin coin) {
        m.addBalance(coin);
        m.setState(new HasMoneyState());
    }

    @Override
    public void selectProduct(VendingMachine m, String code) {
        m.display("Insert coins first");
    }

    @Override
    public void cancel(VendingMachine m) {
        m.display("Nothing to cancel");
    }

    @Override
    public void dispense(VendingMachine m) {
        m.display("Nothing to dispense");
    }

    @Override
    public String name() {
        return "IDLE";
    }
}
