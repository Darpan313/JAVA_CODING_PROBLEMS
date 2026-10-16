package OOP.RideSharingApp.strategies;

public interface FareStrategy {
    double calculate(double km, int seats, double ratePerKm);
}
