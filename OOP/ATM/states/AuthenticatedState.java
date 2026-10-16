package OOP.ATM.states;

import OOP.ATM.core.ATM;
import OOP.ATM.entities.Card;
import OOP.ATM.exceptions.ATMException;

public class AuthenticatedState implements ATMStateHandler {
    @Override
    public void insertCard(ATM atm, Card card) {
        throw new ATMException("A card is already inserted");
    }

    @Override
    public void authenticate(ATM atm, String pin) {
        throw new ATMException("Already authenticated");
    }

    @Override
    public void withdraw(ATM atm, double amount) {
        atm.performWithdrawal(amount);
    }

    @Override
    public void deposit(ATM atm, double amount) {
        atm.performDeposit(amount);
    }

    @Override
    public double checkBalance(ATM atm) {
        return atm.performBalanceInquiry();
    }

    @Override
    public void ejectCard(ATM atm) {

    }
}
