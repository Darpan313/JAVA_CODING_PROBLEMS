package DesginPatterns.AdapterPattern.SensorReading;

public class Main {

    public static void main(String[] args) {

        // --- JSON input ---
        String jsonReading =
            "{\"sensorId\":\"S-01\",\"sensorType\":\"temperature\",\"value\":\"24.5\"," +
            "\"unit\":\"Celsius\",\"timestamp\":\"2026-06-14T10:00:00\"}";

        System.out.println("=== JSON Adapter ===");
        System.out.println("Raw : " + jsonReading);
        SensorDataProcessor jsonProcessor = new SensorDataProcessor(new JsonSensorDataAdapter());
        jsonProcessor.process(jsonReading);

        System.out.println();

        // --- CSV input ---
        String csvReading = "S-02,humidity,65.0,Percent,2026-06-14T10:05:00";

        System.out.println("=== CSV Adapter ===");
        System.out.println("Raw : " + csvReading);
        SensorDataProcessor csvProcessor = new SensorDataProcessor(new CsvSensorDataAdapter());
        csvProcessor.process(csvReading);

        System.out.println();

        // --- Same client, different adapters — swappable at runtime ---
        System.out.println("=== Runtime adapter swap ===");
        String[] inputs = {
            "JSON|{\"sensorId\":\"S-03\",\"sensorType\":\"pressure\",\"value\":\"-1.2\",\"unit\":\"Bar\",\"timestamp\":\"2026-06-14T10:10:00\"}",
            "CSV|S-04,voltage,3.3,Volt,2026-06-14T10:15:00"
        };

        for (String entry : inputs) {
            String[] parts   = entry.split("\\|", 2);
            String format    = parts[0];
            String rawData   = parts[1];
            SensorDataAdapter adapter = format.equals("JSON")
                ? new JsonSensorDataAdapter()
                : new CsvSensorDataAdapter();

            new SensorDataProcessor(adapter).process(rawData);
        }
    }
}
