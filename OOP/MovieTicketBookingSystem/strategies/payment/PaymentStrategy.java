package OOP.MovieTicketBookingSystem.strategies.payment;

import OOP.MovieTicketBookingSystem.entities.Payment;

public interface PaymentStrategy {
    Payment pay(double amount);
    void refund(Payment payment);
}
