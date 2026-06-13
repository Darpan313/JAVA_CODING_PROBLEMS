package Concurrency.DesignProblem.RateLimiter.FineGrainedRateLimiter;

import java.util.concurrent.locks.ReentrantLock;

public class TokenBucket {
    private final ReentrantLock lock = new ReentrantLock();
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
        lock.lock();
        try {
            refill();
            if (tokens >= 1) {
                tokens--;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private void refill() {
        long now = System.nanoTime();
        double elapseSeconds = (now - lastRefillNanos) / 1_000_000_000.0;
        tokens = Math.min(capacity, tokens + elapseSeconds * tokensPerSecond);
        lastRefillNanos = System.nanoTime();
    }
}
