package Concurrency.SynchronizationProblems.ReadersWritersProblem.FirstReadersWriters;

import java.util.concurrent.Semaphore;

public class ReadersPreferenceRW {
    // writeLock controls access to shared resource.
    // It is used by writers, and by the FIRST reader.
    Semaphore writeLock = new Semaphore(1);
    // readLock is mutex that protects the readCount variable.
    Semaphore readLock = new Semaphore(1);
    private int readCount = 0;

    class Readers implements Runnable {

        @Override
        public void run() {
            try {
                readLock.acquire();
                readCount++;

                if(readCount == 1) {
                    writeLock.acquire();
                }
                readLock.release();

                System.out.println(Thread.currentThread().getName() + " is READING.");
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + " has FINISHED reading.");

                readLock.acquire();
                readCount--;

                if(readCount == 0) {
                    writeLock.release();
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

                System.out.println(Thread.currentThread().getName() + " is WRITING.");
                Thread.sleep(2000);
                System.out.println(Thread.currentThread().getName() + " has FINISHED writing.");

                writeLock.release();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.out.println(ex.getMessage());
            }
        }
    }
}
