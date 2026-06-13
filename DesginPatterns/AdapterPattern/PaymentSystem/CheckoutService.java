package DesginPatterns.AdapterPattern.PaymentSystem;

public class CheckoutService {

    private final PaymentProcessor paymentProcessor;


    public CheckoutService(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    public void checkout(double amount, String currency) {
        System.out.println("Checkout: Processing order for $" + amount + " " + currency);
        paymentProcessor.processPayment(amount, currency);
        if(paymentProcessor.isPaymentSuccessful()) {
            System.out.println("Checkout: Order successful! Txn: " + paymentProcessor.getTransactionId());
        }
        else {
            System.out.println("Checkout: Order failed.");
        }
    }
}
