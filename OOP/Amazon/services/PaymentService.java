package OOP.Amazon.services;

import OOP.Amazon.strategies.PaymentStrategy;

public class PaymentService {
    boolean processPayment(PaymentStrategy paymentStrategy, double amount) {
        return paymentStrategy.pay(amount);
    }
}
