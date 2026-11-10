package Auvik.TopTalkers;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class TopTalkers {
    public record FlowRecord(String sourceIp, String destIp, long bytes, long timestamp) {}

    // Map of minute-timestamp -> (Map of IP -> bytes)
    private final ConcurrentHashMap<Long, ConcurrentHashMap<String, LongAdder>> timeBuckets = new ConcurrentHashMap<>();

    // Discuss why not precision? (using queue to append requests with ts?)
    private static final long MINUTE_IN_MS = 60_000L;
    private static final long WINDOW_MINUTES = 5L;

    public void add(FlowRecord flowRecord) {
        long currentMinute = System.currentTimeMillis() / MINUTE_IN_MS;
        long recordMinute = flowRecord.timestamp / MINUTE_IN_MS;

        // Drop records that are older than the 5-minute sliding window.
        if(recordMinute + WINDOW_MINUTES < currentMinute) {
            return;
        }

        // Lock-free retrieval or creation of the minute bucket
        ConcurrentHashMap<String, LongAdder> bucket = timeBuckets.computeIfAbsent(
                recordMinute, k -> new ConcurrentHashMap<>()
        );

        // Lock-free counter increment (highly performant for 10k/second concurrent writes)
        bucket.computeIfAbsent(flowRecord.sourceIp, k -> new LongAdder()).add(flowRecord.bytes);

        // Piggyback cleanup on inserts
        cleanUpOldBuckets(currentMinute);
    }

    public List<Map.Entry<String, Long>> topK(int k) {
        long currentMinute = System.currentTimeMillis() / MINUTE_IN_MS;

        // Aggregate totals for active 5-minue window
        Map<String, Long> aggregateCounts = new HashMap<>();

        for(long i = currentMinute - WINDOW_MINUTES; i<=currentMinute; i++) {
            ConcurrentHashMap<String, LongAdder> bucket = timeBuckets.get(i);
            if(bucket != null) {
                for (Map.Entry<String, LongAdder> entry : bucket.entrySet()) {
                    aggregateCounts.merge(
                            entry.getKey(),
                            entry.getValue().sum(),
                            Long::sum
                    );
                }
            }
        }

        PriorityQueue<Map.Entry<String, Long>> minHeap = new PriorityQueue<>(
                Comparator.comparingLong(Map.Entry::getValue)
        );

        for(Map.Entry<String, Long> entry : aggregateCounts.entrySet()) {
            minHeap.offer(entry);
            if(minHeap.size()>k) {
                minHeap.poll();
            }
        }

        List<Map.Entry<String, Long>> topTalkers = new ArrayList<>();
        while(!minHeap.isEmpty()) {
            topTalkers.add(minHeap.poll());
        }

        Collections.reverse(topTalkers);

        return topTalkers;
    }

    private void cleanUpOldBuckets(long currentMinute) {
        for(Long minuteKey : timeBuckets.keySet()) {
            if(minuteKey + WINDOW_MINUTES < currentMinute) {
                timeBuckets.remove(minuteKey);
            }
        }
    }
}
