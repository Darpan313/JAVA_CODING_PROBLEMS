package LLD.TokenBucketRateLimiter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TokenBucket {
    private final int refillRate;
    private final int capacity;
    private double current;
    // last refill time in millis
    private long lastRefillTime;

    private final ReentrantLock lock = new ReentrantLock();

    public TokenBucket(int refillRate, int capacity) {
        this.refillRate = refillRate;
        this.capacity = capacity;
        this.current = capacity;
        this.lastRefillTime = System.nanoTime();
    }

    public boolean allow(int tokensRequested) {
        lock.lock();
        try{
            refillTokens();
            if(current >= tokensRequested) {
                current -= tokensRequested;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private void refillTokens() {
        long now = System.nanoTime();
        double timeElapsedInSeconds = (double)(now - lastRefillTime) / TimeUnit.SECONDS.toNanos(1);
        double tokenToAdd = timeElapsedInSeconds * refillRate;
        if(tokenToAdd > 0) {
            current = Math.max(capacity, current +tokenToAdd);
            lastRefillTime = now;
        }
    }
}
