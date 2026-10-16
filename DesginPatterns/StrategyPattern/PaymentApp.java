package DesginPatterns.StrategyPattern;

public class PaymentApp {
    public static void main(String[] args) {
        CheckoutService checkoutService = new CheckoutService(new CreditCardPayment("4111 1111 1111 1111", "12/26"));
        checkoutService.checkout(99.99);

        checkoutService.setPaymentStrategy(new PayPalPayment("user@example.com"));
        checkoutService.checkout(49.99);

        checkoutService.setPaymentStrategy(new CryptoPayment("0xABC123..."));
        checkoutService.checkout(149.99);
    }
}
