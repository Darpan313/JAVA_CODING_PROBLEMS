package OOP.RideSharingApp;

import OOP.RideSharingApp.core.Ride;
import OOP.RideSharingApp.entities.Driver;
import OOP.RideSharingApp.entities.Location;
import OOP.RideSharingApp.entities.Rider;
import OOP.RideSharingApp.entities.Vehicle;
import OOP.RideSharingApp.enums.RideStatus;
import OOP.RideSharingApp.exceptions.RideException;
import OOP.RideSharingApp.strategies.SeatBasedFareStrategy;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RideSharingDemo {
    public static void main(String[] args) throws InterruptedException {
        RideSharingService rideSharingService = new RideSharingService(new SeatBasedFareStrategy());

        Location a = new Location("A", 0, 0), b = new Location("B", 0, 10),
                c = new Location("C", 6, 8);
        Driver d1 = rideSharingService.registerDriver("Ravi", new Vehicle("KA01", 4));
        Driver d2 = rideSharingService.registerDriver("Meena", new Vehicle("KA02", 6));
        Rider r1 = rideSharingService.registerRider("Asha"), r2 = rideSharingService.registerRider("Bala"),
                r3 = rideSharingService.registerRider("Chitra");

        Ride ride1 = rideSharingService.createRide(d1, a, b, 3, 10.0);   // 10 km
        Ride ride2 = rideSharingService.createRide(d2, a, c, 5, 12.0);   // 10 km

        ExecutorService pool = Executors.newFixedThreadPool(3);
        pool.submit(() -> rideSharingService.bookRide(r1, a, b, 2));
        pool.submit(() -> rideSharingService.bookRide(r2, a, b, 1));
        pool.submit(() -> rideSharingService.bookRide(r3, a, c, 3));
        pool.shutdown();
        pool.awaitTermination(2, TimeUnit.SECONDS);

        // A 4th rider should fail on ride1 (0 seats left)
        try {
            rideSharingService.bookRide(rideSharingService.registerRider("Dev"), a, b, 1);
        }
        catch (RideException e) {
            System.out.println("Expected: " + e.getMessage());
        }

        for (Ride r : List.of(ride1, ride2)) {
            rideSharingService.updateRideStatus(r, RideStatus.ONGOING);
            rideSharingService.updateRideStatus(r, RideStatus.COMPLETED);   // triggers bills
        }

        System.out.printf("Revenue %s: %.2f%n", d1.getName(), rideSharingService.fetchRevenue(d1)); // 150 + 100 = 250
        System.out.printf("Revenue %s: %.2f%n", d2.getName(), rideSharingService.fetchRevenue(d2)); // 10*3*0.75*12 = 270
    }
}
