package Concurrency.ThreadsCoordination;

//state modification inside lambda?
//why to define private static final
//why synchronized block needed?
public class Main {
    private static final Object lock = new Object();
    private static int state = 0;

    public static void main(String[] args) {

        Thread threadA = new Thread(() -> {
            synchronized (lock) {
                while (state != 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("A");
                state = 1;
                lock.notifyAll();
            }
        });

        Thread threadB = new Thread(() -> {
            synchronized (lock) {
                while (state != 1) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("B");
                state = 2;
                lock.notifyAll();
            }
        });

        Thread threadC = new Thread(() -> {
            synchronized (lock) {
                while (state != 2) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("C");
                state = 0;
                lock.notifyAll();
            }
        });

        threadA.start();
        threadB.start();
        threadC.start();
    }
}
