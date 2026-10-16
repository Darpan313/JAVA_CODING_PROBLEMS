package OOP.VendingMachine.states;

import OOP.VendingMachine.VendingMachine;
import OOP.VendingMachine.enums.Coin;
import OOP.VendingMachine.exceptions.HardwareException;

public class DispensingState implements VendingState {
    @Override
    public void insertCoin(VendingMachine m, Coin coin) {
        m.display("Please wait");
    }

    @Override
    public void selectProduct(VendingMachine m, String code) {
        m.display("Please wait");
    }

    @Override
    public void cancel(VendingMachine m) {
        m.display("Too late to cancel");
    }

    @Override
    public void dispense(VendingMachine m) {
        try {
            m.commitSale();
            m.setState(new IdleState());
        } catch (HardwareException e) {
            m.refund();
            m.setState(new OutOfServiceState());
        }
    }

    @Override
    public String name() {
        return "DISPENSING";
    }
}
