package DesginPatterns.AdapterPattern.SensorReading;

public class SensorReading {
    private final String sensorId;
    private final String sensorType;
    private final double value;
    private final String unit;
    private final String timestamp;

    public SensorReading(String sensorId, String sensorType, double value, String unit, String timestamp) {
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.value = value;
        this.unit = unit;
        this.timestamp = timestamp;
    }

    public String getSensorId()   { return sensorId; }
    public String getSensorType() { return sensorType; }
    public double getValue()      { return value; }
    public String getUnit()       { return unit; }
    public String getTimestamp()  { return timestamp; }

    @Override
    public String toString() {
        return "SensorReading{id='" + sensorId + "', type='" + sensorType +
               "', value=" + value + ", unit='" + unit + "', timestamp='" + timestamp + "'}";
    }
}
