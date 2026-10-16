package OOP.MovieTicketBookingSystem.strategies.pricing;

import OOP.MovieTicketBookingSystem.entities.Seat;
import OOP.MovieTicketBookingSystem.enums.SeatType;

import java.util.List;
import java.util.Map;

public class WeekendPricingStrategy implements PricingStrategy {
    private static double WEEKEND_SURCHARGE = 1.2;
    @Override
    public double calculatePrice(List<Seat> seats, Map<SeatType, Double> seatPrices) {
        double basePrice = seats.stream().mapToDouble(seat -> seatPrices.get(seat.getType())).sum();
        return basePrice * WEEKEND_SURCHARGE;
    }
}
