package OOP.MovieTicketBookingSystem;

import OOP.MovieTicketBookingSystem.entities.*;
import OOP.MovieTicketBookingSystem.enums.PaymentStatus;
import OOP.MovieTicketBookingSystem.strategies.payment.PaymentStrategy;

import java.util.List;
import java.util.Optional;

public class TicketManager {
    private final SeatLockManager seatLockManager;


    public TicketManager(SeatLockManager seatLockManager) {
        this.seatLockManager = seatLockManager;
    }

    public Optional<Ticket> buyTicket(User user, Show show, List<Seat> seats, PaymentStrategy paymentStrategy) {
        if(seatLockManager.lockSeats(show, seats, user.getId())) {
            System.out.println("Could not lock the selected seats. They may already be taken.");
            return Optional.empty();
        }

        double totalAmount = show.getPricingStrategy().calculatePrice(seats, show.getSeatPrices());

        // 3. Process payment. If it fails, release the locks and stop.
        Payment payment = paymentStrategy.pay(totalAmount);
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            System.out.println("Payment failed. Releasing seats.");
            seatLockManager.unlockSeats(show, seats, user.getId());
            return Optional.empty();
        }

        // 4. Re-verify the lock and mark the seats BOOKED atomically. The lock may have
        //    expired while the payment was in flight, so confirm before charging the seat.
        if (!seatLockManager.confirmSeats(show, seats, user.getId())) {
            System.out.println("Seat lock expired before payment completed. Refunding payment.");
            paymentStrategy.refund(payment);
            return Optional.empty();
        }
        // 5. Build the confirmed booking
        Ticket booking = new Ticket(user, show, seats, totalAmount, payment);
        return Optional.of(booking);
    }
}
