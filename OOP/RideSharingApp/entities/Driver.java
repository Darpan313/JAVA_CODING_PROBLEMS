package OOP.RideSharingApp.entities;

public class Driver extends User {
    private final Vehicle vehicle;

    public Driver(String name, Vehicle vehicle) {
        super(name);
        this.vehicle = vehicle;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
}
