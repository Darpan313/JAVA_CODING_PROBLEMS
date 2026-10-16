package OOP.CarRentalSystem.strategies;

public class WeekendPricingStrategy implements PricingStrategy {
    private final double weekendMultiplier;

    public WeekendPricingStrategy(double weekendMultiplier) {
        this.weekendMultiplier = weekendMultiplier;
    }

    @Override
    public double calculateCost(double dailyRate, int days) {
        return dailyRate * days * weekendMultiplier;
    }
}
