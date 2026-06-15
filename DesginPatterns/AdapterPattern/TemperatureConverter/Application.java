package DesginPatterns.AdapterPattern.TemperatureConverter;

public class Application {
    public static void main(String[] args) {
        Thermometer celsius = new CelsiusSensor();
        System.out.printf("Celsius sensor: %.1f C%n", celsius.getTemperature());
        System.out.println();

        FahrenheitSensor sensor = new FahrenheitSensor();
        Thermometer adapted = new FahrenheitAdapter(sensor);
        System.out.printf("Fahrenheit sensor (adapted): %.1f C%n", adapted.getTemperature());
        System.out.println();
    }
}
