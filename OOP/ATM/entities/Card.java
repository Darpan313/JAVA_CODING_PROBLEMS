package OOP.ATM.entities;

/*
    Card is an immutable container for bank card data. All three fields are final because a card's number, PIN, and linked
    account don't change during an ATM session.
 */
public class Card {
    private final String cardNumber;
    private final String pin;
    private final String accountNumber;


    public Card(String cardNumber, String pin, String accountNumber) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.accountNumber = accountNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getPin() {
        return pin;
    }
}
