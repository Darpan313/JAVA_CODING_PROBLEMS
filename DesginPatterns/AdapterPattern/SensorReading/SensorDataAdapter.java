package DesginPatterns.AdapterPattern.SensorReading;

/**
 * Target interface — all format-specific adapters implement this.
 * Clients depend only on this interface, never on the raw format.
 */
public interface SensorDataAdapter {
    SensorReading adapt(String rawData);
}
