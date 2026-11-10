package LLD.IdempotencyKeyManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class IdempotencyManager<T> {
    private final ConcurrentHashMap<String, IdempotencyRecord<T>> cache = new ConcurrentHashMap<>();
    private final long ttlNanos;

    public IdempotencyManager(long ttlDuration, TimeUnit timeUnit) {
        this.ttlNanos = timeUnit.toNanos(ttlDuration);
    }

    /*
        Executes the payment supplier idempotently based on the client-generated key.

        @param idempotencyKey The unique key provided by client.
        @param paymentAction the business logic to execute if this is new request.
        @param The result of paymentAction either freshly computed or cached.
        @param throw IllegalStateException If the transaction is already in-flight by another thread.
     */
    public T executeIdempotent(String idempotencyKey, Supplier<T> paymentAction) {
        cleanUpIfExpired(idempotencyKey);
        IdempotencyRecord<T> newRecord = new IdempotencyRecord<>();
        IdempotencyRecord<T> existingRecord = cache.putIfAbsent(idempotencyKey, newRecord);
        IdempotencyRecord<T> recordToUse = (existingRecord != null) ? existingRecord : newRecord;

        // If the record is already completed successfully, return cached response.
        if(recordToUse.isCompleted()) {
            return recordToUse.getResponse();
        }

        // Native primitive to ensure strict consistency during in-flight processing.
        if(recordToUse.getLock().tryLock()) {
            try {
                if(recordToUse.isCompleted()) {
                    return recordToUse.getResponse();
                }

                T result = paymentAction.get();
                recordToUse.complete(result, System.nanoTime());
                return result;
            } catch(Exception e) {
                // On failure, remove the key so client can safely retry
                cache.remove(idempotencyKey);
                throw e;
            }
            finally {
                recordToUse.getLock().unlock();
            }
        }
        else {
            throw new IllegalStateException("Payment request is currently in progress. Please try again later!");
        }
    }

    private void cleanUpIfExpired(String key) {
        IdempotencyRecord<T> record = cache.get(key);
        if(record != null && record.isCompleted()) {
            long elapsedTime = System.nanoTime() - record.getCompletionTimestamp();
            if(elapsedTime > ttlNanos) {
                cache.remove(key, record); // Atomic removal if it matches our record.
            }
        }
    }

    private static class IdempotencyRecord<T> {
        private final ReentrantLock lock = new ReentrantLock();
        private T response;
        private long completionTimestamp;
        private volatile boolean completed = false;

        public boolean isCompleted() {
            return completed;
        }

        public T getResponse() {
            return response;
        }

        public ReentrantLock getLock() {
            return lock;
        }

        public long getCompletionTimestamp() {
            return this.completionTimestamp;
        }

        public void complete(T response, long completionTimestamp) {
            this.response = response;
            this.completionTimestamp = completionTimestamp;
            this.completed = true;
        }
    }
}
