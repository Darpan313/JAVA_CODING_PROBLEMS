package OOP.ATM.handlers;

import OOP.ATM.enums.Denomination;

import java.util.Map;

public class DenominationHandler implements CashHandler {
    private final Denomination denomination;
    private int count;
    private CashHandler nextHandler;

    public DenominationHandler(Denomination denomination, int count) {
        this.denomination = denomination;
        this.count = count;
    }


    @Override
    public void setNextHandler(CashHandler handler) {
        this.nextHandler = handler;
    }

    @Override
    public void dispense(int amount, Map<Denomination, Integer> result) {
        if(amount>0) {
            int billValue = denomination.getValue();
            int billsNeeded = amount/billValue;
            // Can't use more than we have in stock
            int billsToDispense = Math.min(billsNeeded, count);

            if(billsToDispense > 0) {
                result.put(denomination, billsToDispense);
                count -= billsToDispense;
            }

            int remaining = amount - (billsToDispense * billValue);
            if(remaining>0 && nextHandler != null) {
                nextHandler.dispense(remaining, result);
            }
        }
    }

    public Denomination getDenomination() {
        return denomination;
    }

    public int getCount() {
        return count;
    }

    public void addBills(int count) {
        this.count += count;
    }

    public void removeBills(int count) {
        this.count -= count;
    }
}
