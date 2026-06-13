package Concurrency.DesignProblem.RateLimiter.CoarseGrainedRateLimiter;

public class TokenBucket {
    private final long capacity;
    private final double tokensPerSecond;
    private double tokens;
    private long lastRefillNanos;

    public TokenBucket(long capacity, double tokensPerSecond) {
        this.capacity = capacity;
        this.tokensPerSecond = tokensPerSecond;
        this.tokens = capacity;
        this.lastRefillNanos = System.nanoTime();
    }

    public boolean tryAcquire() {
        refill();
        if (tokens >= 1) {
            tokens -= 1;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.nanoTime();
        double elapsedSeconds = (now - lastRefillNanos) / 1_000_000_000.0;
        tokens = Math.min(capacity, tokens + elapsedSeconds * tokensPerSecond);
        lastRefillNanos = now;
    }
}
