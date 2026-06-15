package DesginPatterns.AdapterPattern.SensorReading;

import java.util.HashMap;
import java.util.Map;

/**
 * Adaptee: raw JSON string.
 * Converts flat JSON {"key":"value",...} into a SensorReading.
 *
 * Example input:
 *   {"sensorId":"S-01","sensorType":"temperature","value":"24.5","unit":"Celsius","timestamp":"2026-06-14T10:00:00"}
 */
public class JsonSensorDataAdapter implements SensorDataAdapter {

    @Override
    public SensorReading adapt(String rawData) {
        Map<String, String> fields = parseJson(rawData);

        String sensorId   = require(fields, "sensorId");
        String sensorType = require(fields, "sensorType");
        double value      = Double.parseDouble(require(fields, "value"));
        String unit       = require(fields, "unit");
        String timestamp  = require(fields, "timestamp");

        return new SensorReading(sensorId, sensorType, value, unit, timestamp);
    }

    // Minimal flat-JSON parser: handles string and numeric values, no nesting.
    private Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        // Strip surrounding braces and whitespace
        String body = json.trim();
        if (body.startsWith("{")) body = body.substring(1);
        if (body.endsWith("}"))   body = body.substring(0, body.length() - 1);

        // Split on commas that are NOT inside quotes (simple: split then re-join quoted commas)
        String[] pairs = body.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length != 2) continue;
            String key = stripQuotes(kv[0].trim());
            String val = stripQuotes(kv[1].trim());
            map.put(key, val);
        }
        return map;
    }

    private String stripQuotes(String s) {
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2)
            return s.substring(1, s.length() - 1);
        return s;
    }

    private String require(Map<String, String> fields, String key) {
        String val = fields.get(key);
        if (val == null) throw new IllegalArgumentException("Missing JSON field: " + key);
        return val;
    }
}
