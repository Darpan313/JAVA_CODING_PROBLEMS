package OOP.RideSharingApp.core;

import OOP.RideSharingApp.entities.Booking;
import OOP.RideSharingApp.entities.Driver;
import OOP.RideSharingApp.entities.Location;
import OOP.RideSharingApp.entities.Rider;
import OOP.RideSharingApp.enums.RideStatus;
import OOP.RideSharingApp.exceptions.RideException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Ride {
    private static final AtomicInteger SEQ = new AtomicInteger(1);

    private final int id;
    private final Driver driver;
    private final Location origin, destination;
    private final int totalSeats;
    private final double ratePerKm;
    private int availableSeats;
    private RideStatus status = RideStatus.CREATED;
    private final List<Booking> bookings = new ArrayList<>();

    public Ride(Driver driver, Location origin, Location destination, int seats, double ratePerKm) {
        if(seats > driver.getVehicle().getCapacity()) {
            throw new RideException("Seats exceed vehicle capacity");
        }
        this.id = SEQ.getAndIncrement();
        this.driver = driver;
        this.origin = origin;
        this.destination = destination;
        this.totalSeats = seats;
        this.availableSeats = seats;
        this.ratePerKm = ratePerKm;
    }

    public synchronized boolean canAccomodate(int seats) {
        return status == RideStatus.CREATED && availableSeats >= seats;
    }

    public synchronized Booking addBooking(Rider rider, int seats) {
        if(!canAccomodate(seats)) throw new RideException("Ride " + id + " cannot take " + seats + " seats");

        Booking booking = new Booking(rider, seats);
        bookings.add(booking);
        availableSeats -= seats;
        return booking;
    }

    public synchronized void updateStatus(RideStatus next) {
        if(!isValidTransition(status, next)) {
            throw new RideException("Invalid transition " + status + " -> " + next);
        }
        status = next;
    }

    private static boolean isValidTransition(RideStatus from, RideStatus to) {
        return switch (from) {
            case CREATED -> to == RideStatus.ONGOING || to == RideStatus.CANCELLED;
            case ONGOING -> to == RideStatus.COMPLETED || to == RideStatus.CANCELLED;
            default -> false;
        };
    }

    public double distanceKm() {
        return origin.distanceTo(destination);
    }

    public int getId() {
        return id;
    }

    public Driver getDriver() {
        return driver;
    }

    public Location getOrigin() {
        return origin;
    }

    public Location getDestination() {
        return destination;
    }

    public double getRatePerKm() {
        return ratePerKm;
    }

    public RideStatus getStatus() {
        return status;
    }

    public synchronized int getAvailableSeats() {
        return availableSeats;
    }

    public synchronized List<Booking> getBookings() {
        return List.copyOf(bookings);
    }
}
