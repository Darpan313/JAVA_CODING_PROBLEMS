package OOP.CarRentalSystem.models;

public class Customer {
    private final String id;
    private final String name;
    private final String email;
    private final String drivingLicense;

    public Customer(String id, String name, String email, String drivingLicense) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.drivingLicense = drivingLicense;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDrivingLicense() {
        return drivingLicense;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                '}';
    }
}
