package Concurrency.OrderPrinting.CountDownLatch;

public class Main {
    public static void main(String[] args) {
        OrderPrinting obj = new OrderPrinting();
        OrderPrintingThread t1 = new OrderPrintingThread(obj, "first");
        OrderPrintingThread t2 = new OrderPrintingThread(obj, "second");
        OrderPrintingThread t3 = new OrderPrintingThread(obj, "third");

        t1.start();
        t2.start();
        t3.start();
    }
}
