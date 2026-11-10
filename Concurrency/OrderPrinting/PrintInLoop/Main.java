package Concurrency.OrderPrinting.PrintInLoop;

import java.util.concurrent.Semaphore;

public class Main {
    private static final int N = 5;
    private static final Semaphore semA = new Semaphore(1);
    private static final Semaphore semB = new Semaphore(0);
    private static final Semaphore semC = new Semaphore(0);

    public static void main(String[] args) {
        Thread threadA = new Thread(() -> {
            try {
                for (int i = 0; i < N; i++) {
                    semA.acquire();
                    System.out.print(" A");
                    semB.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread threadB = new Thread(() -> {
            try {
                for (int i = 0; i < N; i++) {
                    semB.acquire();
                    System.out.print(" B");
                    semC.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread threadC = new Thread(() -> {
            try {
                for (int i = 0; i < N; i++) {
                    semC.acquire();
                    System.out.println(" C");
                    semA.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        threadA.start();
        threadB.start();
        threadC.start();
    }
}
