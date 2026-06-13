package Concurrency.DesignProblem.RateLimiter.FineGrainedRateLimiter;



import java.util.concurrent.ConcurrentHashMap;

public class FineGrainedRateLimiter {
    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final double tokensPerSecond;
    private final long capacity;


    public FineGrainedRateLimiter(double tokensPerSecond, long capacity) {
        this.tokensPerSecond = tokensPerSecond;
        this.capacity = capacity;
    }

    public boolean tryAcquire(String clientId) {
        // Thread-safe map handles concurrent get-or-create atomically
        // No global lock needed for lookup/insertion
        TokenBucket bucket = buckets.computeIfAbsent(clientId, k -> new TokenBucket(capacity, tokensPerSecond));

        // Each bucket has its own lock, so different clients don't block each other.
        return bucket.tryAcquire();
    }
}
