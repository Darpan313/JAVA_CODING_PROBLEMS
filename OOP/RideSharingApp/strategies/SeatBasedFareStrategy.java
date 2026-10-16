package OOP.RideSharingApp.strategies;

public class SeatBasedFareStrategy implements FareStrategy {
    private static final double SHARED_DISCOUNT = 0.75;
    @Override
    public double calculate(double km, int seats, double ratePerKm) {
        if(seats>=2) return km * seats * SHARED_DISCOUNT * ratePerKm;
        return km * ratePerKm;
    }
}
