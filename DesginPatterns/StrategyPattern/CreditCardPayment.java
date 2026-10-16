package DesginPatterns.StrategyPattern;

public class CreditCardPayment implements PaymentStrategy {
    private final String cardNumber;
    private final String expiryDate;

    public CreditCardPayment(String cardNumber, String expiryDate) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("Charging $" + amount + " to credit card ending in "
                + cardNumber.substring(cardNumber.length() - 4));
        return true;
    }
}
