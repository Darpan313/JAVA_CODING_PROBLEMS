package OOP.RideSharingApp.entities;

public class Vehicle {
    private final String regNo;
    private final int capacity;

    public Vehicle(String regNo, int capacity) {
        this.regNo = regNo;
        this.capacity = capacity;
    }

    public String getRegNo() {
        return regNo;
    }

    public int getCapacity() {
        return capacity;
    }
}
