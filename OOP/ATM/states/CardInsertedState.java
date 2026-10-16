package OOP.ATM.states;

import OOP.ATM.core.ATM;
import OOP.ATM.entities.Account;
import OOP.ATM.entities.Card;
import OOP.ATM.enums.ATMState;
import OOP.ATM.exceptions.ATMException;

public class CardInsertedState implements ATMStateHandler {
    @Override
    public void insertCard(ATM atm, Card card) {
        throw new ATMException("Card is already inserted.");
    }

    @Override
    public void authenticate(ATM atm, String pin) {
        Account account = atm.getBankService().authenticate(atm.getCurrentCard().getCardNumber(), pin);
        atm.setCurrentAccount(account);
        atm.setCurrentState(ATMState.AUTHENTICATED);
        System.out.println("Authentication successful for card " +
                atm.getCurrentCard().getCardNumber());
    }

    @Override
    public void withdraw(ATM atm, double amount) {
        throw new ATMException("Please authenticate first");
    }

    @Override
    public void deposit(ATM atm, double amount) {
        throw new ATMException("Please authenticate first");
    }

    @Override
    public double checkBalance(ATM atm) {
        throw new ATMException("Please authenticate first");
    }

    @Override
    public void ejectCard(ATM atm) {
        atm.setCurrentCard(null);
        atm.setCurrentState(ATMState.IDLE);
        System.out.println("Card ejected successfully");
    }
}
