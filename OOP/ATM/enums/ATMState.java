package OOP.ATM.enums;

public enum ATMState {
    IDLE,           // No card inserted, waiting for user.
    CARD_INSERTED,  // Card inserted, awaiting pin.
    AUTHENTICATED   // PIN verified, ready for transaction.
}
