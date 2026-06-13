package Concurrency.DesignProblem.RateLimiter.CoarseGrainedRateLimiter;

import java.util.HashMap;
import java.util.Map;

// One global lock serializes all clients — simple but contended under high concurrency.
public class CoarseGrainedRateLimiter {
    private final Map<String, TokenBucket> buckets = new HashMap<>();
    private final Object lock = new Object();
    private final double tokensPerSecond;
    private final long capacity;

    public CoarseGrainedRateLimiter(long capacity, double tokensPerSecond) {
        this.capacity = capacity;
        this.tokensPerSecond = tokensPerSecond;
    }

    public boolean tryAcquire(String clientId) {
        synchronized (lock) {
            TokenBucket bucket = buckets.computeIfAbsent(clientId, k -> new TokenBucket(capacity, tokensPerSecond));
            return bucket.tryAcquire();
        }
    }
}
