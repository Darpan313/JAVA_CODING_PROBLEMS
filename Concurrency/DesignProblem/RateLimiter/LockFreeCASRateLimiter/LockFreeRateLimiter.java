package Concurrency.DesignProblem.RateLimiter.LockFreeCASRateLimiter;

import java.util.concurrent.ConcurrentHashMap;

public class LockFreeRateLimiter {
    private final ConcurrentHashMap<String, LockFreeBucket> buckets = new ConcurrentHashMap<>();
    private final double tokensPerSecond;
    private final long capacity;

    public LockFreeRateLimiter(double tokensPerSecond, long capacity) {
        this.tokensPerSecond = tokensPerSecond;
        this.capacity = capacity;
    }

    public boolean tryAcquire(String clientId) {
        LockFreeBucket bucket = buckets.computeIfAbsent(clientId, k -> new LockFreeBucket(capacity, tokensPerSecond));
        return bucket.tryAcquire();
    }
}
