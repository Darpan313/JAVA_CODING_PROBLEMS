package OOP.InventoryManagement;

import OOP.InventoryManagement.exceptions.InsufficientStockException;
import OOP.InventoryManagement.service.InventoryService;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class InventoryManagementDemo {
    public static void main(String[] args) throws Exception {
        InventoryService svc = new InventoryService(30_000);
        svc.addStock("SKU-1", 10);

        int threads = 200;
        ExecutorService pool = Executors.newFixedThreadPool(32);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger success = new AtomicInteger();

        for(int i=0; i<threads; i++) {
            String orderId = "order-" + i;
            pool.submit(() -> {
                start.await();
                try {
                    svc.reserve(orderId, Map.of("SKU-1", 1));
                    success.incrementAndGet();
                } catch (InsufficientStockException ignored) {}
                    return null;
            });
        }
        start.countDown();
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("successful reservations = " + success.get()); // expect 10
        System.out.println("available now           = " + svc.getAvailable("SKU-1")); // expect 0

        svc.confirm("order-0");
        svc.release("order-1");
        System.out.println("after confirm+release   = " + svc.getAvailable("SKU-1")); // expect 1
    }
}
