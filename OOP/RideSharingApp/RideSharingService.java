package OOP.RideSharingApp;

import OOP.RideSharingApp.core.Ride;
import OOP.RideSharingApp.entities.*;
import OOP.RideSharingApp.enums.RideStatus;
import OOP.RideSharingApp.exceptions.RideException;
import OOP.RideSharingApp.strategies.FareStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RideSharingService {
    private final Map<Integer, Driver> drivers = new ConcurrentHashMap<>();
    private final Map<Integer, Rider> riders = new ConcurrentHashMap<>();
    private final Map<Integer, Ride> rides = new ConcurrentHashMap<>();

    private final FareStrategy fareStrategy;

    public RideSharingService(FareStrategy fareStrategy) {
        this.fareStrategy = fareStrategy;
    }

    // 1. Register a driver/cab
    Driver registerDriver(String name, Vehicle vehicle) {
        Driver driver = new Driver(name, vehicle);
        drivers.put(driver.getId(), driver);
        return driver;
    }

    // 2. register a rider
    Rider registerRider(String name) {
        Rider rider = new Rider(name);
        riders.put(rider.getId(), rider);
        return rider;
    }

    // 4. Create a ride (driver offers origin, destination, seats)
    Ride createRide(Driver driver, Location origin, Location destination, int seats, double ratePerKm) {
        Ride ride = new Ride(driver, origin, destination, seats, ratePerKm);
        rides.put(ride.getId(), ride);
        return ride;
    }

    // 6. Check validity of an ongoing/open rife for a new rider
    boolean isRideValidFor(Ride ride, Location origin, Location destination, int seats) {
        return ride.getOrigin().equals(origin)
                && ride.getDestination().equals(destination)
                && ride.canAccomodate(seats);
    }

    // 3. Book a ride (rider requests origin, destination, seats)
    Booking bookRide(Rider rider, Location origin, Location destination, int seats) {
        // Match on the first valid ride (could also pick cheapest/fewest empty seats)
        for(Ride ride : rides.values()) {
            if(isRideValidFor(ride, origin, destination, seats)) {
                try{
                    return ride.addBooking(rider, seats);       // May race; try next ride if it fails
                } catch (RideException ignored) {

                }
            }
        }
        throw new RideException("No ride available " + origin + " -> " + destination + " for " + seats + " seat(s)");
    }

    // 7. update ride status (bills generated automatically when COMPLETED)
    void updateRideStatus(Ride ride, RideStatus status) {
        ride.updateStatus(status);
        if(status == RideStatus.COMPLETED) generateBills(ride);
    }

    // 5. Generate bill - one per rider on the ride
    private void generateBills(Ride ride) {
        double km = ride.distanceKm();
        for(Booking b : ride.getBookings()) {
            double fare = fareStrategy.calculate(km, b.getSeats(), ride.getRatePerKm());
            b.setFare(fare);
            System.out.printf("Ride %d closed: %s pays %.2f (%.1f km, %d seat(s))%n",
                    ride.getId(), b.getRider().getName(), fare, km, b.getSeats());
        }
    }

    //8. Revenue of all rides taken by driver
    double fetchRevenue(Driver driver) {
        return rides.values().stream()
                .filter(r -> r.getDriver().getId() == driver.getId())
                .filter(r -> r.getStatus() == RideStatus.COMPLETED)
                .flatMap(r -> r.getBookings().stream())
                .mapToDouble(Booking::getFare)
                .sum();
    }
}
