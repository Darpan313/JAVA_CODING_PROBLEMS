package OOP.MovieTicketBookingSystem.strategies.pricing;

import OOP.MovieTicketBookingSystem.entities.Seat;
import OOP.MovieTicketBookingSystem.enums.SeatType;

import java.util.List;
import java.util.Map;

public interface PricingStrategy {
    double calculatePrice(List<Seat> seats, Map<SeatType, Double> seatPrices);
}
