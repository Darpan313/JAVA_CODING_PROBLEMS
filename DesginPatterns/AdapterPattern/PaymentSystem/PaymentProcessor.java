package DesginPatterns.AdapterPattern.PaymentSystem;

public interface PaymentProcessor {
    void processPayment(double amount, String currency);
    boolean isPaymentSuccessful();
    String getTransactionId();
}
