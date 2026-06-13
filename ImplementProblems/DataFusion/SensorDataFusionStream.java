package ImplementProblems.DataFusion;

import java.util.List;
import java.util.stream.Collectors;

public class SensorDataFusionStream {
    public record SensorReading(String sensorId, long timestamp, double value) {}

    public interface SensorStream {
        List<SensorReading> getReadings();
    }
    /**
     * Merges streams, filters by time window, deduplicates by sensor ID, and sorts by timestamp.
     *
     * @param streams   List of incoming sensor streams
     * @param startTime Beginning of the time window (inclusive)
     * @param endTime   End of the time window (inclusive)
     * @return          A sorted list of deduplicated SensorReadings
     */
    public List<SensorReading> fuseData(List<SensorStream> streams, long startTime, long endTime) {
        return streams.stream()
                // Step 1: Flatten all streams into a single unified stream of readings
                .flatMap(stream -> stream.getReadings().stream())
                // Step 2: Filter readings to ensure they fall within the specified time window
                .filter(reading -> reading.timestamp >= startTime && reading.timestamp <= endTime)
                // Step 3: Deduplicate by sensorId.
                // If multiple readings exist for the same sensor, we keep the one with the latest timestamp.
                .collect(Collectors.toMap(
                                SensorReading::sensorId,
                                reading -> reading,
                                (existing, replacement) ->
                                        existing.timestamp >= replacement.timestamp ? existing : replacement
                        ))
                .values()
                .stream()
                .toList();
    }

//    public List<SensorReading> traditionallyFuseData(List<SensorStream> streams, long startTime, long endTime) {
//
//    }
}
