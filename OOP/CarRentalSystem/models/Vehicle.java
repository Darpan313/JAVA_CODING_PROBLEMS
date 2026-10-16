package OOP.CarRentalSystem.models;

import OOP.CarRentalSystem.enums.VehicleStatus;
import OOP.CarRentalSystem.enums.VehicleType;

public class Vehicle {
    private final String id;
    private final String licensePlate;
    private final VehicleType vehicleType;
    private final double dailyRate;
    private VehicleStatus vehicleStatus;
    private String locationId;


    public Vehicle(String id, String licensePlate, VehicleType vehicleType, double dailyRate, String locationId) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.dailyRate = dailyRate;
        this.locationId = locationId;
        this.vehicleStatus = VehicleStatus.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public VehicleStatus getVehicleStatus() {
        return vehicleStatus;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setVehicleStatus(VehicleStatus vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", vehicleType=" + vehicleType +
                ", dailyRate=" + dailyRate + "/day" +
                '}';
    }
}
