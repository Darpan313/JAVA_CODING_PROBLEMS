package OOP.VendingMachine;

import OOP.VendingMachine.enums.Coin;
import OOP.VendingMachine.model.Product;
import OOP.VendingMachine.model.Slot;

/*
    Design a vending machine.
    Users insert coins, select a product, and receive the product and any change.
    Handle out-of-stock items, insufficient money, and cancellation.
    Design it in Java.
 */
public class VendingMachineDemo {
    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();
        vm.addSlot(new Slot("A1", new Product("Chips", 65), 2));
        vm.loadCoins(Coin.NICKEL, 10); vm.loadCoins(Coin.DIME, 10);

        vm.selectProduct("A1");         // IDLE: "Insert coins first"
        vm.insertCoin(Coin.QUARTER);    // -> HAS_MONEY, balance 25
        vm.insertCoin(Coin.QUARTER);    // balance 50
        vm.selectProduct("A1");         // "Insert 15 more"
        vm.insertCoin(Coin.QUARTER);    // balance 75
        vm.selectProduct("A1");         // change 10 planned -> DISPENSING -> dispensed, -> IDLE
        vm.insertCoin(Coin.DOLLAR);
        vm.cancel();
    }
}
