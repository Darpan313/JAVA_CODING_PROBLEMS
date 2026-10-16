package OOP.ATM.entities;

import OOP.ATM.enums.TransactionType;

import java.time.LocalDateTime;

/*
    Transaction is an immutable record of completed operation. Once created nothing about it can be changed. This
    immutability is critical for audit integrity.
 */
public class Transaction {
    private final String id;
    private final TransactionType transactionType;
    private final double amount;
    private final String accountNumber;
    private final String timestamp;


    public Transaction(String id, TransactionType transactionType, double amount, String accountNumber) {
        this.id = id;
        this.transactionType = transactionType;
        this.amount = amount;
        this.accountNumber = accountNumber;
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getId() {
        return id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Transaction{id='" + id + "', type=" + transactionType +
                ", amount=$" + amount + ", account='" + accountNumber +
                "', time=" + timestamp + "}";
    }
}
