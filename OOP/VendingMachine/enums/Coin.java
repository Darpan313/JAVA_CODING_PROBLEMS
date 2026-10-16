package OOP.VendingMachine.enums;

public enum Coin {
    NICKEL(5),
    DIME(10),
    QUARTER(25),
    DOLLAR(100);

    public final int cents;

    Coin(int cents) {
        this.cents = cents;
    }
}
