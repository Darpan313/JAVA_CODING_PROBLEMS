package ImplementProblems.ParallelSensor;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SensorService {
    public SensorReading fetchReading(String sensorName) {
        System.out.println("[" + Thread.currentThread().getName() + "] Fetching data for: " + sensorName + "...");

        try {
            int delay = ThreadLocalRandom.current().nextInt(1000, 3000);
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sensor read interrupted", ex);
        }

        double randomValue = Math.round(ThreadLocalRandom.current().nextDouble(10.0, 100.0)*100.0/100.0);
        System.out.println("[" + Thread.currentThread().getName() + "] ✅ " + sensorName + " finished.");
        return new SensorReading(sensorName, randomValue);
    }
    record SensorReading(String sensorName, double value){}
    record FactoryHealthReport(List<SensorReading> readings){}
}
