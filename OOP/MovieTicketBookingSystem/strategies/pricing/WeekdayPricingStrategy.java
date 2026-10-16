package OOP.MovieTicketBookingSystem.strategies.pricing;

import OOP.MovieTicketBookingSystem.entities.Seat;
import OOP.MovieTicketBookingSystem.enums.SeatType;

import java.util.List;
import java.util.Map;

public class WeekdayPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(List<Seat> seats, Map<SeatType, Double> seatPrices) {
        return seats.stream().mapToDouble(seat -> seatPrices.get(seat.getType())).sum();
    }
}
