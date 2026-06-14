package Concurrency.DesignProblem.RateLimiter.TimeBasedRateLimiter;

public class Main {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========================================");
        System.out.println("  Rate Limiter Tests: 5 requests/minute");
        System.out.println("========================================\n");

        testHappyPath_exactLimit();
        testUnhappyPath_exceedLimit();
        testMultipleClients_independentBuckets();
        testSlidingWindow_requestsAllowedAfterWindowPasses();
        testConcurrentRequests_threadSafety();

        System.out.println("========================================");
        System.out.printf("  Results: %d PASSED, %d FAILED%n", passed, failed);
        System.out.println("========================================");
    }

    // Happy path: exactly 5 requests — all should be allowed
    static void testHappyPath_exactLimit() {
        System.out.println("--- Test 1: Happy Path (exactly 5 requests, all allowed) ---");
        RateLimiter rl = new RateLimiter(5, 60_000);
        String client = "client-happy";

        for (int i = 1; i <= 5; i++) {
            assertRequest(rl, client, i, true);
        }
        System.out.println();
    }

    // Unhappy path: 8 requests — 6th, 7th, 8th must be rejected
    static void testUnhappyPath_exceedLimit() {
        System.out.println("--- Test 2: Unhappy Path (8 requests, 6-8 must be rejected) ---");
        RateLimiter rl = new RateLimiter(5, 60_000);
        String client = "client-unhappy";

        for (int i = 1; i <= 8; i++) {
            assertRequest(rl, client, i, i <= 5);
        }
        System.out.println();
    }

    // Multiple clients: each has an independent 5-request bucket
    static void testMultipleClients_independentBuckets() {
        System.out.println("--- Test 3: Multiple Clients (independent buckets, 6 requests each) ---");
        RateLimiter rl = new RateLimiter(5, 60_000);
        String[] clients = {"client-A", "client-B", "client-C"};

        for (String client : clients) {
            System.out.println("  >> " + client);
            for (int i = 1; i <= 6; i++) {
                assertRequest(rl, client, i, i <= 5);
            }
        }
        System.out.println();
    }

    // Sliding window: fill limit, wait for window to expire, new requests allowed again
    static void testSlidingWindow_requestsAllowedAfterWindowPasses() throws InterruptedException {
        System.out.println("--- Test 4: Sliding Window (2s window — fill, block, wait, refill) ---");
        // Use 2-second window so the test doesn't take a minute
        RateLimiter rl = new RateLimiter(5, 2_000);
        String client = "client-sliding";

        System.out.println("  Phase 1: Fill up bucket (5 requests)");
        for (int i = 1; i <= 5; i++) {
            assertRequest(rl, client, i, true);
        }

        System.out.println("  Phase 2: 6th request must be rejected");
        assertRequest(rl, client, 6, false);

        System.out.println("  Phase 3: Sleeping 2.1s for window to expire...");
        Thread.sleep(2_100);

        System.out.println("  Phase 4: Window expired — 5 new requests should be allowed");
        for (int i = 1; i <= 5; i++) {
            assertRequest(rl, client, i, true);
        }

        System.out.println("  Phase 5: 6th request after refill must be rejected");
        assertRequest(rl, client, 6, false);
        System.out.println();
    }

    // Concurrent requests: multiple threads hitting the same client — total allowed must be exactly 5
    static void testConcurrentRequests_threadSafety() throws InterruptedException {
        System.out.println("--- Test 5: Concurrent Requests (10 threads, limit 5) ---");
        RateLimiter rl = new RateLimiter(5, 60_000);
        String client = "client-concurrent";

        int[] allowedCount = {0};
        int[] rejectedCount = {0};
        Object lock = new Object();

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                boolean result = rl.allow(client);
                synchronized (lock) {
                    if (result) allowedCount[0]++;
                    else rejectedCount[0]++;
                }
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.printf("  Allowed: %d (expected: 5) | Rejected: %d (expected: 5)%n",
                allowedCount[0], rejectedCount[0]);
        check("Concurrent allowed == 5", allowedCount[0] == 5);
        check("Concurrent rejected == 5", rejectedCount[0] == 5);
        System.out.println();
    }

    // --- helpers ---

    static void assertRequest(RateLimiter rl, String client, int reqNum, boolean expected) {
        boolean actual = rl.allow(client);
        String status = actual == expected ? "PASS" : "FAIL";
        System.out.printf("  Request %-2d | Expected: %-5s | Actual: %-5s | %s%n",
                reqNum, expected, actual, status);
        if (actual == expected) passed++; else failed++;
    }

    static void check(String label, boolean condition) {
        String status = condition ? "PASS" : "FAIL";
        System.out.printf("  %-40s | %s%n", label, status);
        if (condition) passed++; else failed++;
    }
}
