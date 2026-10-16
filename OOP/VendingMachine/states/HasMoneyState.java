package OOP.VendingMachine.states;

import OOP.VendingMachine.VendingMachine;
import OOP.VendingMachine.enums.Coin;
import OOP.VendingMachine.model.Slot;

import java.util.Optional;

public class HasMoneyState implements VendingState {
    @Override
    public void insertCoin(VendingMachine m, Coin coin) {
        m.addBalance(coin);
    }

    @Override
    public void selectProduct(VendingMachine m, String code) {
        Optional<Slot> opt = m.getInventory().getSlot(code);
        if(opt.isEmpty()) {
            m.display("Invalid selection");
            return;
        }

        Slot slot = opt.get();
        if(slot.getQuantity()<=0) {
            m.display("Sold out");
            return;
        }

        int price = slot.getProduct().priceInCents();
        if(m.getBalanceInCents() < price) {
            m.display("Insert " + (price - m.getBalanceInCents()) + " more");
            return;
        }
        int changeDue = m.getBalanceInCents() - price;
        if(m.getCoinBank().planChange(changeDue).isEmpty()) {
            m.display("Cannot make change, refunding");
            m.refund();
            m.setState(new IdleState());
            return;
        }
        m.select(slot, changeDue);
        m.setState(new DispensingState());
        m.dispense(); // drive the next step immediately
    }

    @Override
    public void cancel(VendingMachine m) {
        m.refund();
        m.setState(new IdleState());
    }

    @Override
    public void dispense(VendingMachine m) {
        m.display("Select a product first");
    }

    @Override
    public String name() {
        return "HAS_MONEY";
    }
}
