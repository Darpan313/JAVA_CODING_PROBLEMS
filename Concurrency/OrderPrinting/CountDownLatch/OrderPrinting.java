package Concurrency.OrderPrinting.CountDownLatch;

import java.util.concurrent.CountDownLatch;

public class OrderPrinting {
    CountDownLatch latch1;
    CountDownLatch latch2;

    public OrderPrinting() {
        this.latch1 = new CountDownLatch(1);
        this.latch2 = new CountDownLatch(1);
    }

    public void printFirst() {
        latch1.countDown();
        System.out.println("First");
    }

    public void printSecond() throws InterruptedException {
        latch1.await();
        System.out.println("Second");
        latch2.countDown();
    }

    public void printThird() throws InterruptedException {
        latch2.await();
        System.out.println("Third");
    }
}
