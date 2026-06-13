package DesginPatterns.AdapterPattern.PaymentSystem;

public class ECommerceAppV2 {
    public static void main(String[] args) {
        // Modern processor
        PaymentProcessor processor = new InHousePaymentProcessor();
        CheckoutService modernCheckout = new CheckoutService(processor);
        System.out.println("----Using Moder Processor----");
        modernCheckout.checkout(199.9, "USD");

        // Legacy gateway through adapter
        System.out.println("\n---- Using Legacy Gateway via Adapter----");
        LegacyGateway legacyGateway = new LegacyGateway();
        processor = new LegacyGatewayAdapter(legacyGateway);
        CheckoutService legacyCheckout = new CheckoutService(processor);
        legacyCheckout.checkout(75.50, "USD");
    }
}
