package DesginPatterns.AdapterPattern.SensorReading;

/**
 * Client — works only with SensorDataAdapter, unaware of JSON/CSV specifics.
 */
public class SensorDataProcessor {

    private final SensorDataAdapter adapter;

    public SensorDataProcessor(SensorDataAdapter adapter) {
        this.adapter = adapter;
    }

    public void process(String rawData) {
        SensorReading reading = adapter.adapt(rawData);
        System.out.println("[Processed] " + reading);

        if (reading.getValue() < 0) {
            System.out.println("  WARNING: negative sensor value detected.");
        }
    }
}
