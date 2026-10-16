package Concurrency.FooBar;

public class FooBarPrint {
    public static Object lock = new Object();
    public static int flag = 0;
    public static void main(String[] args) {
        Thread foo = new Thread(() -> {
            foo(5);
        });
        Thread bar = new Thread(() -> {
            bar(5);
        });
        foo.start();
        bar.start();
    }

    public static void foo(int n) {
        for(int i=0; i<n; i++) {
            synchronized (lock) {
                while(flag == 1) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Foo");
                flag = 1;
                lock.notifyAll();
            }
        }
    }

    public static void bar(int n) {
        for (int i=0; i<n; i++) {
            synchronized (lock) {
                while(flag == 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Bar");
                flag = 0;
                lock.notifyAll();
            }
        }
    }
}
