package OOP.RideSharingApp.entities;

public class Booking {
    private final Rider rider;
    private final int seats;
    private double fare;

    public Booking(Rider rider, int seats) {
        this.rider = rider;
        this.seats = seats;
    }

    public Rider getRider() {
        return rider;
    }

    public int getSeats() {
        return seats;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }
}
