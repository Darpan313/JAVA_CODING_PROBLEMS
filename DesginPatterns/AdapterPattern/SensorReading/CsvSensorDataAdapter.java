package DesginPatterns.AdapterPattern.SensorReading;

/**
 * Adaptee: raw CSV string.
 * Expected column order: sensorId, sensorType, value, unit, timestamp
 *
 * Example input:
 *   S-02,humidity,65.0,Percent,2026-06-14T10:05:00
 *
 * An optional header line starting with "sensorId" is skipped automatically.
 */
public class CsvSensorDataAdapter implements SensorDataAdapter {

    private static final int COL_SENSOR_ID   = 0;
    private static final int COL_SENSOR_TYPE = 1;
    private static final int COL_VALUE       = 2;
    private static final int COL_UNIT        = 3;
    private static final int COL_TIMESTAMP   = 4;
    private static final int EXPECTED_COLS   = 5;

    @Override
    public SensorReading adapt(String rawData) {
        String line = rawData.trim();

        // Skip header row if present
        if (line.toLowerCase().startsWith("sensorid")) {
            throw new IllegalArgumentException("Header row passed to adapter — provide a data row, not the header.");
        }

        String[] cols = line.split(",", -1);
        if (cols.length != EXPECTED_COLS) {
            throw new IllegalArgumentException(
                "CSV must have exactly " + EXPECTED_COLS + " columns, got " + cols.length + ": " + rawData);
        }

        String sensorId   = cols[COL_SENSOR_ID].trim();
        String sensorType = cols[COL_SENSOR_TYPE].trim();
        double value      = Double.parseDouble(cols[COL_VALUE].trim());
        String unit       = cols[COL_UNIT].trim();
        String timestamp  = cols[COL_TIMESTAMP].trim();

        return new SensorReading(sensorId, sensorType, value, unit, timestamp);
    }
}
