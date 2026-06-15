package DesginPatterns.AdapterPattern.TemperatureConverter;

public class FahrenheitAdapter implements Thermometer {
    private final FahrenheitSensor fahrenheitSensor;

    public FahrenheitAdapter(FahrenheitSensor fahrenheitSensor) {
        this.fahrenheitSensor = fahrenheitSensor;
    }

    @Override
    public double getTemperature() {
        double F = fahrenheitSensor.readFarenheit();
        return ((F-32) * 5.0)/9.0;
    }
}
