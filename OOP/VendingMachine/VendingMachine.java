package OOP.VendingMachine;

import OOP.VendingMachine.enums.Coin;
import OOP.VendingMachine.model.CoinBank;
import OOP.VendingMachine.model.Inventory;
import OOP.VendingMachine.model.Slot;
import OOP.VendingMachine.model.Transaction;
import OOP.VendingMachine.states.IdleState;
import OOP.VendingMachine.states.VendingState;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class VendingMachine {
    private VendingState state = new IdleState();
    private final Inventory inventory = new Inventory();
    private final CoinBank coinBank = new CoinBank();
    private final ReentrantLock lock = new ReentrantLock();
    private final List<Transaction> ledger = new ArrayList<>();

    // session
    private int balanceInCents = 0;
    private final EnumMap<Coin, Integer> sessionCoins = new EnumMap<>(Coin.class);
    private Slot selected;
    private int changeDue;

    // ---- public events: each takes the lock, delegates to state ---
    public void insertCoin(Coin c) {
        withLock(() -> state.insertCoin(this, c));
    }

    public void selectProduct(String code) {
        withLock(() -> state.selectProduct(this, code));
    }

    public void cancel() {
        withLock(() -> state.cancel(this));
    }

    public void reset() {
        withLock(() -> {
            refund();
            state = new IdleState();
        });
    }

    public void refill(String code, int qty) {
        withLock(() -> {
            inventory.refill(code, qty);
        });
    }

    public void loadCoins(Coin c, int n) {
        withLock(() -> {
            coinBank.add(c, n);
        });
    }

    public void addSlot(Slot s) {
        withLock(() -> {
            inventory.addSlot(s);
        });
    }

    private void withLock(Runnable r) {
        lock.lock();
        try {
            r.run();
        } finally {
            lock.unlock();
        }
    }

    // -- helpers used by states (already under lock) ----

    public void setState(VendingState state) {
        this.state = state;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getBalanceInCents() {
        return balanceInCents;
    }

    public CoinBank getCoinBank() {
        return coinBank;
    }

    public void dispense() {
        state.dispense(this);
    }

    public void addBalance(Coin c) {
        balanceInCents += c.cents;
        sessionCoins.merge(c, 1, Integer::sum);
        display("Session Balance " + balanceInCents);
    }

    public void select(Slot slot, int changeDue) {
        this.selected = slot;
        this.changeDue = changeDue;
    }

    public void display(String msg) { System.out.println("[" + state.name() + "] " + msg); }

    public void refund() {
        if(balanceInCents>0) {
            display("Refunding " + balanceInCents);
            clearSession();
        }
    }

    /*
        Move session coins into bank, pay change out of the bank, decrement stock
     */
    public void commitSale() {
        if(selected.getQuantity() <= 0) throw new IllegalStateException("stock changed");   // defensive re-check
        Map<Coin, Integer> change = coinBank.planChange(changeDue).orElseThrow();           // guarded earlier
        sessionCoins.forEach(coinBank::add);
        coinBank.remove(change);
        selected.setQuantity(selected.getQuantity()-1);
        ledger.add(new Transaction(selected.getCode(), selected.getProduct().priceInCents(), balanceInCents, change));
        display("Dispensed " + selected.getProduct().name() + ", change " + changeDue);
        clearSession();
    }

    private void clearSession() {
        balanceInCents = 0;
        sessionCoins.clear();
        selected = null;
        changeDue = 0;
    }
}

