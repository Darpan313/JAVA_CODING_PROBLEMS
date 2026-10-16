package OOP.ATM.core;

import OOP.ATM.enums.Denomination;
import OOP.ATM.exceptions.ATMException;
import OOP.ATM.handlers.CashHandler;
import OOP.ATM.handlers.DenominationHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public class CashDispenser {
    private final Map<Denomination, DenominationHandler> denominationHandlers;
    private CashHandler chainHead;

    public CashDispenser(Map<Denomination, DenominationHandler> denominationHandlers) {
        this.denominationHandlers = new LinkedHashMap<>();
        for(Denomination d : Denomination.values()) {
            denominationHandlers.put(d, new DenominationHandler(d, 0));
        }
        buildChain();
    }

    private void buildChain() {
        DenominationHandler[] handlers = denominationHandlers.values().toArray(new DenominationHandler[0]);

        for(int i=0; i<handlers.length-1; i++) {
            handlers[i].setNextHandler(handlers[i+1]);
        }
        chainHead = handlers[0];
    }

    public synchronized boolean canDispense(int amount) {
        if(amount<0 || amount%10 != 0) {
            return false;
        }

        // Simulate dispensing without modifying actual inventory
        int remaining = amount;
        for(DenominationHandler handler : denominationHandlers.values()) {
            int billValue = handler.getDenomination().getValue();
            int billsAvailable = handler.getCount();
            int billsNeeded = remaining/billValue;
            int billsToUse = Math.min(billsNeeded, billsAvailable);
            remaining -= (billsToUse*billValue);
        }
        return remaining == 0;
    }

    public synchronized Map<Denomination, Integer> dispense(int amount) {
        if(!canDispense(amount)) {
            throw new ATMException("Cannot dispense $" + amount +
                    " with available denominations");
        }

        Map<Denomination, Integer> result = new LinkedHashMap<>();
        chainHead.dispense(amount, result);
        return result;
    }

    public synchronized void addCash(Denomination denomination, int count) {
        denominationHandlers.get(denomination).addBills(count);
    }

    public synchronized int getTotalCash() {
        int total = 0;
        for(Map.Entry<Denomination, DenominationHandler> entry : denominationHandlers.entrySet()) {
            total += (entry.getKey().getValue() * entry.getValue().getCount());
        }
        return total;
    }
}
