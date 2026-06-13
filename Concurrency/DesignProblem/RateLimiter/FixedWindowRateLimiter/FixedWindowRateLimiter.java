package Concurrency.DesignProblem.RateLimiter.FixedWindowRateLimiter;

import java.util.concurrent.atomic.AtomicLong;

public class FixedWindowRateLimiter {
    static final int THREADS = 8;
    static final int REQUESTS = 100000;
    static final int LIMIT = 1000;

    public static void main(String[] args) throws InterruptedException {
        RateLimiter rateLimiter = new RateLimiter(LIMIT);
        AtomicLong allowed = new AtomicLong(0);

        Thread[] threads = new Thread[THREADS];
        for(int t=0; t<THREADS; t++) {
            threads[t] = new Thread(() -> {
               long local = 0;
               for(int i=0; i<REQUESTS; i++) {
                   if(rateLimiter.tryAcquire()) {
                       local++;
                   }
               }
               allowed.addAndGet(local);
            });
        }

        for(Thread t : threads) {
            t.start();
        }

        for(Thread t : threads) {
            t.join();
        }
        long total = (long) THREADS * REQUESTS;
        System.out.println(allowed.get() + " " + (total - allowed.get()));
    }
}
