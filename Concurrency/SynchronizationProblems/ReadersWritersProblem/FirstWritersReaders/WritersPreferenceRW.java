package Concurrency.SynchronizationProblems.ReadersWritersProblem.FirstWritersReaders;

import java.util.concurrent.Semaphore;

public class WritersPreferenceRW {

    Semaphore resourceAccess = new Semaphore(1);
    Semaphore writeLock = new Semaphore(1);
    Semaphore readLock = new Semaphore(1);
    Semaphore turnStile = new Semaphore(1);
    private int writeCount = 0;
    private int readCount = 0;

    class Readers implements Runnable {

        @Override
        public void run() {
            try {
                // 1. Enter the turnstile
                // If a writer is waiting, the turnstile is locked, and readers wait here!
                turnStile.acquire();
                turnStile.release(); // Step through and immediately unlock for the next person.

                // Safely increment readCount
                readLock.acquire();
                readCount++;
                if(readCount == 1) {
                    // First reader locks the resource from writers
                    resourceAccess.acquire();
                }
                readLock.release();

                System.out.println(Thread.currentThread().getName() + " is READING.");
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + " has FINISHED reading.");

                readLock.acquire();
                readCount--;
                if(readCount == 0) {
                    resourceAccess.release();
                }
                readLock.release();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.out.println(ex.getMessage());
            }
        }
    }

    class Writers implements Runnable {

        @Override
        public void run() {
            try {
                writeLock.acquire();
                writeCount++;
                if(writeCount == 1) {
                    // FIRST writer locks the Turnstile!
                    // No new readers can pass this point.
                    turnStile.acquire();
                }
                writeLock.release();

                // Request exclusive access to the resource
                // The writer waits here until active readers/writers finish.
                resourceAccess.acquire();
                System.out.println(Thread.currentThread().getName() + " is WRITING.");
                Thread.sleep(2000);
                System.out.println(Thread.currentThread().getName() + " has FINISHED writing.");
                resourceAccess.release();

                writeLock.acquire();
                writeCount--;
                if(writeCount == 0) {
                    turnStile.release();
                }
                writeLock.release();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.out.println(ex.getMessage());
            }
        }
    }
}
