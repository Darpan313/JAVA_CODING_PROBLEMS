package DesginPatterns.AdapterPattern.TemperatureConverter;

public class CelsiusSensor implements Thermometer {
    @Override
    public double getTemperature() {
        return 25.0;
    }
}
