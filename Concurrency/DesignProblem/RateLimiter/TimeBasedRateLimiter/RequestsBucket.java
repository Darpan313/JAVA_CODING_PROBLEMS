package Concurrency.DesignProblem.RateLimiter.TimeBasedRateLimiter;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public class RequestsBucket {
    private final ReentrantLock lock = new ReentrantLock();
    private final int capacity;
    private final long timeRangeInMs;
    private Deque<Long> requestsQueue;

    public RequestsBucket(int capacity, long timeRangeInMs) {
        this.requestsQueue = new LinkedList<>();
        this.capacity = capacity;
        this.timeRangeInMs = timeRangeInMs;
    }

    public boolean tryAcquire() {
        lock.lock();
        try {
            long currTimeInMillis = System.currentTimeMillis();
            while (!requestsQueue.isEmpty() && (currTimeInMillis - requestsQueue.peekFirst() > timeRangeInMs)) {
                requestsQueue.removeFirst();
            }

            if (requestsQueue.size() < capacity) {
                requestsQueue.addLast(currTimeInMillis);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}
