package Concurrency.DesignProblem.RateLimiter.TimeBasedRateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {
    private final Map<String, RequestsBucket> buckets = new ConcurrentHashMap<>();
    private final int capacity;
    private final long timeSpanInMs;

    public RateLimiter(int capacity, long timeSpanInMs) {
        this.capacity = capacity;
        this.timeSpanInMs = timeSpanInMs;
    }

    public boolean allow(String clientId) {
        RequestsBucket bucket = buckets.computeIfAbsent(clientId, v -> new RequestsBucket(capacity, timeSpanInMs));
        return bucket.tryAcquire();
    }
}
