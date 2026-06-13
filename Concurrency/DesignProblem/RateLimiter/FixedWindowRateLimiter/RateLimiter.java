package Concurrency.DesignProblem.RateLimiter.FixedWindowRateLimiter;

import java.util.concurrent.locks.ReentrantLock;

public class RateLimiter {
    ReentrantLock lock = new ReentrantLock();
    private int count = 0;
    private final int limit;

    public RateLimiter(int limit) {
        this.limit = limit;
    }

    boolean tryAcquire() {
        // lock; if count < limit, increment and return true; else return false;

        lock.lock();
        try {
            if(count < limit) {
                count++;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}
