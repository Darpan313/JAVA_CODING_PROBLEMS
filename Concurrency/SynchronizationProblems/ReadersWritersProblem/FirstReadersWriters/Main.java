package Concurrency.SynchronizationProblems.ReadersWritersProblem.FirstReadersWriters;

public class Main {
    // This solution gives priority to readers. Writers wait for all readers to finish, and new readers can join even
    // if a writer is waiting. This maximizes read throughput at the cost of potentially starving writers.
    public static void main(String[] args) throws InterruptedException {
        ReadersPreferenceRW rw = new ReadersPreferenceRW();

        // Start a writer first so it acquires writeLock immediately.
        Thread w1 = new Thread(rw.new Writers(), "Writer-1");
        w1.start();

        // Small pause so Writer-1 gets the lock before readers arrive.
        Thread.sleep(100);

        // Three readers arrive while Writer-1 holds the lock.
        // Once Writer-1 finishes, all three readers run concurrently.
        Thread r1 = new Thread(rw.new Readers(), "Reader-1");
        Thread r2 = new Thread(rw.new Readers(), "Reader-2");
        Thread r3 = new Thread(rw.new Readers(), "Reader-3");
        r1.start();
        r2.start();
        r3.start();

        // Writer-2 arrives while readers are active.
        // It must wait until ALL readers finish before it can write.
        Thread.sleep(200);
        Thread w2 = new Thread(rw.new Writers(), "Writer-2");
        w2.start();

        // Reader-4 arrives after Writer-2 is already waiting.
        // Because readers are preferred, Reader-4 is allowed in and Writer-2 keeps waiting.
        Thread.sleep(100);
        Thread r4 = new Thread(rw.new Readers(), "Reader-4");
        r4.start();
    }
}
