package LLD.CircuitBreaker;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/*
    A lock-free, thread-safe Circuit Breaker
 */
public class CircuitBreaker {
    public enum State {
        CLOSED,
        OPEN,
        HALF_OPEN
    }

    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
    private final AtomicInteger consecutiveFailure = new AtomicInteger(0);

    private final int failureThreshold;
    private final long retryTimeoutNanos;

    // volatile ensures visibility across threads for the timeout timestamp
    private volatile long nextAttemptNanos;

    /*
        @param failureThreshold Consecutive failures before tripping the circuit breaker
        @param retryTimeout Duration to wait before allowing a test request.
        @param timeunit The unit for retry timeout.
     */
    public CircuitBreaker(int failureThreshold, long retryTimeout, TimeUnit timeUnit) {
        this.failureThreshold = failureThreshold;
        this.retryTimeoutNanos = timeUnit.toNanos(retryTimeout);
    }

    /*
        Wraps a downstream call with circuit breaker protection.

        @param action The downstream service call to execute
        @return The result of the action if successful
        @throws CircuitBreakerOpenException if the circuit is OPEN
        @throws RuntimeException if the underlying action throws and exception.
     */
    public <T> T execute(Supplier<T> action) {
        if(!tryAcquirePermission()) {
            throw new CircuitBreakerOpenException("Circuit Breaker is OPEN. Request rejected.");
        }

        try {
            T result = action.get();
            recordSuccess();
            return result;
        } catch(Exception e) {
            recordFailure();
            throw e;
        }
    }

    /*
        Determines if a request should be allowed through based on current state.
     */
    private boolean tryAcquirePermission() {
        State currentState = state.get();

        if(currentState == State.CLOSED) {
            return true;
        }

        if(currentState == State.OPEN) {
            long now = System.nanoTime();
            if(now >= nextAttemptNanos) {
                // Compare-And-Swap (CAS) prevents the Thundering Herd.
                // Only ONE thread will successfully change OPEN->HALF_OPEN and return true.
                if(state.compareAndSet(State.OPEN, State.HALF_OPEN)) {
                    return true;
                }
            }
            return false;
        }
        // If a state is HALF_OPEN, another thread is currently testing the downstream service.
        return false;
    }

    private void recordSuccess() {
        // Reset failures on a successful call
        consecutiveFailure.set(0);

        // If we just had a successful test request, close the circuit
        if(state.get() == State.HALF_OPEN) {
            state.set(State.CLOSED);
        }
    }

    private void recordFailure() {
        int currentFailure = consecutiveFailure.incrementAndGet();

        // If we are in HALF_OPEN, the test request failed. Immediately trip back to OPEN.
        if(state.get() == State.HALF_OPEN) {
            tripToOpen();
            return;
        }

        // If we are CLOSED and exceeded the threshold, trip to OPEN
        if(currentFailure >= failureThreshold && state.get() == State.CLOSED) {
            // CAS ensures we only trip it once and set the timeout timestamp once.
            if(state.compareAndSet(State.CLOSED, State.OPEN)) {
                nextAttemptNanos = System.nanoTime() + retryTimeoutNanos;
            }
        }
    }

    /*
        Helper to force the circuit open and reset the timeout.
     */
    private void tripToOpen() {
        state.set(State.OPEN);
        nextAttemptNanos = System.nanoTime() + retryTimeoutNanos;
    }

    public static class CircuitBreakerOpenException extends RuntimeException {
        public CircuitBreakerOpenException(String message) {
            super(message);
        }
    }
}
