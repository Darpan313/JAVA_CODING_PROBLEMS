package OOP.CarRentalSystem.models;

import OOP.CarRentalSystem.enums.ReservationStatus;
import OOP.CarRentalSystem.enums.VehicleType;
import OOP.CarRentalSystem.exceptions.CarRentalException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reservation {
    private final String id;
    private final Customer customer;
    private final VehicleType vehicleType;
    private final String pickupLocationId;
    private final String returnLocationId;
    private final LocalDate pickupDate;
    private final LocalDate returnDate;
    private final List<Equipment> equipment;
    private Vehicle assignedVehicle;
    private ReservationStatus status;
    private double totalCost;


    public Reservation(String id, Customer customer, VehicleType vehicleType, String pickupLocationId, String returnLocationId, LocalDate pickupDate, LocalDate returnDate, List<Equipment> equipment) {
        this.id = id;
        this.customer = customer;
        this.vehicleType = vehicleType;
        this.pickupLocationId = pickupLocationId;
        this.returnLocationId = returnLocationId;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.equipment = Collections.unmodifiableList(new ArrayList<>(equipment));
        this.assignedVehicle = null;
        this.status = ReservationStatus.CONFIRMED;
        this.totalCost = 0;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public String getPickupLocationId() {
        return pickupLocationId;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public String getReturnLocationId() {
        return returnLocationId;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public Vehicle getAssignedVehicle() {
        return assignedVehicle;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void assignVehicle(Vehicle vehicle) {
        this.assignedVehicle = vehicle;
    }

    public void activate() {
        if(status != ReservationStatus.CONFIRMED) {
            throw new CarRentalException(
                    "Can only activate CONFIRMED reservations. Current: " + status);
        }

        this.status = ReservationStatus.ACTIVE;
    }

    public void complete(double totalCost) {
        if(status != ReservationStatus.ACTIVE) {
            throw new CarRentalException(
                    "Can only complete ACTIVE reservations. Current: " + status);
        }

        this.status = ReservationStatus.COMPLETED;
        this.totalCost = totalCost;
    }

    public void cancel() {
        if(status != ReservationStatus.CONFIRMED) {
            throw new CarRentalException(
                    "Can only cancel CONFIRMED reservations. Current: " + status);
        }
        this.status = ReservationStatus.CANCELLED;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + id + '\'' +
                ", customer=" + customer +
                ", vehicleType=" + vehicleType +
                ", status=" + status +
                '}';
    }
}
