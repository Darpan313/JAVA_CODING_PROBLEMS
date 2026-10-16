package OOP.MovieTicketBookingSystem.entities;

import OOP.MovieTicketBookingSystem.enums.SeatType;
import OOP.MovieTicketBookingSystem.strategies.pricing.PricingStrategy;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class Show {
    private final String id;
    private final Movie movie;
    private final Screen screen;
    private final LocalDateTime startTime;
    private final Map<SeatType, Double> seatPrices;
    private final PricingStrategy pricingStrategy;
    private final Object lock = new Object();

    public Show(String id, Movie movie, Screen screen, LocalDateTime startTime, Map<SeatType, Double> seatPrices, PricingStrategy pricingStrategy) {
        this.id = id;
        this.movie = movie;
        this.screen = screen;
        this.startTime = startTime;
        this.seatPrices = seatPrices;
        this.pricingStrategy = pricingStrategy;
    }

    public String getId() {
        return this.id;
    }

    public Movie getMovie() {
        return movie;
    }

    public Screen getScreen() {
        return this.screen;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Object getLock() {
        return lock;
    }

    public PricingStrategy getPricingStrategy() {
        return pricingStrategy;
    }

    public Map<SeatType, Double> getSeatPrices() {
        return seatPrices;
    }
}
