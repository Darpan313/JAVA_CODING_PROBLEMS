package Concurrency.DesignProblem.RateLimiter.LockFreeCASRateLimiter;

import java.util.concurrent.atomic.AtomicLong;

public class LockFreeBucket {
    // Store tokens * PRECISION to avoid floating point
    private static final long PRECISION = 1_000_000;

    private final AtomicLong tokens;
    private final AtomicLong lastRefillTime;
    private final long capacity;
    private final double tokensPerSecond;

    public LockFreeBucket(long capacity, double tokensPerSecond) {
        this.capacity = capacity * PRECISION;
        this.tokensPerSecond = tokensPerSecond;
        this.tokens = new AtomicLong(this.capacity);
        this.lastRefillTime = new AtomicLong(System.nanoTime());
    }

    public boolean tryAcquire() {
        while(true) {
            long now = System.nanoTime();
            long currentTokens = tokens.get();
            long lastRefill = lastRefillTime.get();

            double elapsed = (now - lastRefill) / 1_000_000_000.0;
            long tokensToAdd = (long)(elapsed * tokensPerSecond * PRECISION);
            long newTokens = Math.min(capacity, currentTokens + tokensToAdd);

            if(newTokens < PRECISION) {
                return false;
            }

            long afterConsume = newTokens - PRECISION; // Subtract 1 token

            if(tokens.compareAndSet(currentTokens, afterConsume)) {
                lastRefillTime.compareAndSet(lastRefill, now);
                return true;
            }
            // CAS failed - another thread won the race. Loop and retry with fresh value.
        }
    }
}
