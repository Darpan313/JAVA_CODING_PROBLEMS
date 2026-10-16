package OOP.Amazon.strategies;

public class CreditCardPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean pay(double amount) {
        return false;
    }
}
