package Concurrency.WaterMolecule;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class H2OMachine {
    private final Semaphore hSemaphore = new Semaphore(2);
    private final Semaphore oSemaphore = new Semaphore(1);

    private final CyclicBarrier barrier = new CyclicBarrier(3, () -> {
        System.out.println("*** Molecule completed: H2O exits the machine. ***\n");
    });

    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
        hSemaphore.acquire();
        try {
            releaseHydrogen.run();
            barrier.await();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        } finally {
            hSemaphore.release();
        }
    }

    public void oxygen(Runnable releaseOxygen) throws InterruptedException {
        oSemaphore.acquire();
        try {
            releaseOxygen.run();
            barrier.await();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        } finally {
            oSemaphore.release();
        }
    }
}
