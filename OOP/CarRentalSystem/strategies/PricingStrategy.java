package OOP.CarRentalSystem.strategies;

public interface PricingStrategy {
    double calculateCost(double dailyRate, int days);
}
