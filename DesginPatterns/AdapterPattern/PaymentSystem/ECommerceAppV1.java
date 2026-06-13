package DesginPatterns.AdapterPattern.PaymentSystem;

public class ECommerceAppV1 {
    public static void main(String[] args) {
        PaymentProcessor processor = new InHousePaymentProcessor();
        CheckoutService checkoutService = new CheckoutService(processor);
        checkoutService.checkout(199.99, "USD");
    }
}
