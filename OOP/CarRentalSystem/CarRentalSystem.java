package OOP.CarRentalSystem;

import OOP.CarRentalSystem.enums.VehicleType;
import OOP.CarRentalSystem.models.*;
import OOP.CarRentalSystem.observers.RentalObserver;
import OOP.CarRentalSystem.strategies.PricingStrategy;
import OOP.CarRentalSystem.strategies.StandardPricingStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CarRentalSystem {
    private static volatile CarRentalSystem instance;
    private static final Object lock = new Object();

    private final ConcurrentHashMap<String, Location> locations;
    private final ConcurrentHashMap<String, Vehicle> vehicles;
    private final ConcurrentHashMap<String, Reservation> reservations;
    // Maps location Id to list of vehicles at that Location
    private final ConcurrentHashMap<String, List<Vehicle>> locationVehicles;
    private final CopyOnWriteArrayList<RentalObserver> observers;
    private PricingStrategy pricingStrategy;
    private final AtomicInteger reservationCounter;
    private static final double LATE_FEE_PER_DAY = 50.0;


    public CarRentalSystem() {
        this.locations = new ConcurrentHashMap<>();
        this.vehicles = new ConcurrentHashMap<>();
        this.reservations = new ConcurrentHashMap<>();
        this.locationVehicles = new ConcurrentHashMap<>();
        this.observers = new CopyOnWriteArrayList<>();
        this.pricingStrategy = new StandardPricingStrategy();
        this.reservationCounter = new AtomicInteger(0);
    }

    public static CarRentalSystem getInstance() {
        if(instance == null) {
            synchronized (lock) {
                if(instance == null) {
                    instance = new CarRentalSystem();
                }
            }
        }
        return instance;
    }

    public synchronized void addLocation(Location location) {
        locations.put(location.getId(), location);
        locationVehicles.putIfAbsent(location.getId(), new ArrayList<>());
    }

    public synchronized void addVehicle(Vehicle vehicle) {
        vehicles.put(vehicle.getId(), vehicle);
        locationVehicles.computeIfAbsent(vehicle.getLocationId(), k -> new ArrayList<>()).add(vehicle);
    }

    public synchronized Reservation makeReservation(Customer customer,
                                                    VehicleType vehicleType, String pickupLocationId,
                                                    String returnLocationId, LocalDate pickupDate,
                                                    LocalDate returnDate, List<Equipment> equipment) {
        return null;
    }

    public synchronized Vehicle pickupVehicle(String reservationId) {
        return null;
    }

    public synchronized Bill returnVehicle(String reservationId, String returnLocationId, LocalDate actualReturnDate) {
        return null;
    }

    public synchronized void cancelReservation(String reservationId) {
        return;
    }

    public synchronized void setPricingStrategy(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public void addObserver(RentalObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(RentalObserver observer) {
        observers.remove(observer);
    }

    public void notifyReservationCreated(Reservation reservation) {
        for (RentalObserver observer : observers) {
            try {
                observer.onReservationCreated(reservation);
            } catch (Exception e) {
                System.err.println("Observer notification failed: " + e.getMessage());
            }
        }
    }

    private void notifyVehiclePickedUp(Reservation reservation) {
        for (RentalObserver observer : observers) {
            try {
                observer.onVehiclePickedUp(reservation);
            } catch (Exception e) {
                System.err.println("Observer notification failed: " + e.getMessage());
            }
        }
    }

    private void notifyVehicleReturned(Reservation reservation, Bill bill) {
        for (RentalObserver observer : observers) {
            try {
                observer.onVehicleReturned(reservation, bill);
            } catch (Exception e) {
                System.err.println("Observer notification failed: " + e.getMessage());
            }
        }
    }
}
