package OOP.ATM.entities;

import OOP.ATM.exceptions.ATMException;

/*
    Account is one mutable class in our system. The balance changes with every withdrawal and deposit, so debit and
    credit methods are synchronized to prevent race conditions when multiple ATMs share same bank service.
 */
public class Account {
    private final String accountNumber;
    private double balance;


    public Account(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    public synchronized void debit(double amount) {
        if(amount > balance) {
            throw new ATMException("Insufficient funds. Account balance $" + balance + " requested: $" + amount);
        }
        balance -= amount;
    }

    public synchronized void credit(double amount) {
        balance += amount;
    }

    public double getBalance() {
        return this.balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
