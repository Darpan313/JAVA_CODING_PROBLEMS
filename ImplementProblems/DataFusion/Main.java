package ImplementProblems.DataFusion;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        // Define our time window
        long startTime = 100L;
        long endTime = 200L;

        // Mock Stream 1
        SensorDataFusionStream.SensorStream stream1 = () -> List.of(
                new SensorDataFusionStream.SensorReading("TempSensor", 90L, 22.0),    // Dropped: Too early
                new SensorDataFusionStream.SensorReading("TempSensor", 150L, 23.5),   // Deduplicated: Overwritten by newer TempSensor reading
                new SensorDataFusionStream.SensorReading("PressureSensor", 120L, 1013.2) // Kept
        );

        // Mock Stream 2
        SensorDataFusionStream.SensorStream stream2 = () -> List.of(
                new SensorDataFusionStream.SensorReading("TempSensor", 180L, 24.1),   // Kept: Latest TempSensor in the window
                new SensorDataFusionStream.SensorReading("TempSensor", 210L, 25.0),   // Dropped: Too late
                new SensorDataFusionStream.SensorReading("LightSensor", 199L, 800.0)  // Kept
        );

        // Initialize fusion engine
        SensorDataFusionStream fusionEngine = new SensorDataFusionStream();

        // Execute
        System.out.println("Processing streams with Time Window: [" + startTime + " - " + endTime + "]\n");
        List<SensorDataFusionStream.SensorReading> fusedData = fusionEngine.fuseData(List.of(stream1, stream2), startTime, endTime);

        // Print results
        System.out.println("--- Fused & Sorted Results ---");
        for (SensorDataFusionStream.SensorReading reading : fusedData) {
            System.out.println(String.format("Time: %-4d | Sensor: %-15s | Value: %.2f",
                    reading.timestamp(), reading.sensorId(), reading.value()));
        }
    }
}
