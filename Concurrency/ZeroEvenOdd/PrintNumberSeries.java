package Concurrency.ZeroEvenOdd;

import java.util.concurrent.Semaphore;

public class PrintNumberSeries {
    private int n;
    private Semaphore zeroSem, oddSem, evenSem;

    public PrintNumberSeries(int n) {
        this.n = n;
        zeroSem = new Semaphore(1);
        oddSem = new Semaphore(0);
        evenSem = new Semaphore(0);
    }

    public void printZero() {
        for(int i=0; i<n; i++) {
            try {
                zeroSem.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("0");
            (i%2 == 0 ? oddSem : evenSem).release();
        }
    }
    public void printOdd() {
        for(int i=1; i<n; i+=2) {
            try {
                oddSem.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(i);
            zeroSem.release();
        }
    }

    public void printEven() {
        for(int i=2; i<n; i+=2) {
            try {
                evenSem.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(i);
            zeroSem.release();
        }
    }
}
