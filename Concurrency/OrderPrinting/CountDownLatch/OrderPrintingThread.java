package Concurrency.OrderPrinting.CountDownLatch;

public class OrderPrintingThread extends Thread {
    private OrderPrinting obj;
    private String method;

    public OrderPrintingThread(OrderPrinting obj, String method) {
        this.obj = obj;
        this.method = method;
    }

    public void run() {
        if ("first".equals(method)) {
            obj.printFirst();
        }
        else if("second".equals(method)) {
            try {
                obj.printSecond();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        else if("third".equals(method)) {
            try {
                obj.printThird();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
