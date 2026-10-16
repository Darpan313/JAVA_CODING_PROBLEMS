package DesginPatterns.StrategyPattern;

public class PayPalPayment implements PaymentStrategy {
    private final String email;

    public PayPalPayment(String email) {
        this.email = email;
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("Sending $" + amount + " via PayPal to " + email);
        return true;
    }
}
