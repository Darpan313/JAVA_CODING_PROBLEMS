package Concurrency.CompletableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class CustomerAsyncs {
    private static final Logger logger = Logger.getLogger(CustomerAsyncs.class.getName());

    public static void printOrder() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> cfPrintOrder = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info(() -> "Order is printed by: " + Thread.currentThread().getName());
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        cfPrintOrder.get(); // wait for order to be printed, this is blocking
        logger.info("Customer order was printed...\n");
    }

    /*
        This time, the async task must return result, and so runAsync() is not useful. This is a job for supplyAsync()
     */
    public static void fetchOrderSummary() throws ExecutionException, InterruptedException {

        CompletableFuture<String> cfOrderSummary = CompletableFuture.supplyAsync(() -> {
            logger.info(() -> "Fetch order summary by: " + Thread.currentThread().getName());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return "Order summary #8989";
        });

        // wait for summary to be available, this is blocking
        String summary = cfOrderSummary.get();
        logger.info("Order summary: " + summary + "\n");
    }

    /*
        By default, as in preceding examples, the synchronous tasks are executed in threads obtained from the global
        ForkJoinPool.commonPool(). But we can also use an explicit Executor custom thread pool.
     */
    public static void fetchOrderSummaryExecutor() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        CompletableFuture<String> cfOrderSummary = CompletableFuture.supplyAsync(() -> {
           try {
               logger.info(() -> "Fetch order summary by: " + Thread.currentThread().getName());
               Thread.sleep(500);
           } catch(InterruptedException e) {
               Thread.currentThread().interrupt();
           }
           return "Order summary #8989";
        }, executor);

        String summary = cfOrderSummary.get(); // wait for summary to be available, this is blocking
        logger.info(() -> "Order summary: " + summary + "\n");

        executor.shutdownNow();
    }
    // thenApply() access to previous result and returns CompletableFuture<T>
    public static void fetchInvoiceTotalSign() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cfFetchInvoice = CompletableFuture.supplyAsync(() -> {
           try {
               logger.info(() -> "Fetch invoice by: " + Thread.currentThread().getName());
               Thread.sleep(500);
           } catch (InterruptedException ex) {

           }
           return "Invoice #3344";
        });

        CompletableFuture<String> cTotalSign = cfFetchInvoice
                .thenApply(o -> o + " Total: $145")
                .thenApply(o -> o + " Signed");

        String result = cTotalSign.get();
        logger.info(() -> "Invoice: " + result + "\n");
    }

    // thenAccept() access to previous result and returns CompletableFuture<Void>
    public static void fetchAndPrintOrder() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> cFetchAndPrintOrder = CompletableFuture.supplyAsync(() -> {
           logger.info(() -> "Fetch order by: " + Thread.currentThread().getName());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "Order #1024";
        }).thenAccept(
                o-> logger.info(() -> "Printing order " + o + " by: " + Thread.currentThread().getName()));

        cFetchAndPrintOrder.get();
        logger.info("Order was fetched and printed \n");
    }

    // thenRun() no access to previous result and returns CompletableFuture<Void>
    public static void deliverOrderNotifyCustomer() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> cfDeliverOrder = CompletableFuture.runAsync(() -> {
           logger.info(() -> "Order was delivered by: " + Thread.currentThread().getName());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<Void> cNotifyCustomer = cfDeliverOrder.thenRun(() -> logger.info(
                () -> "Dear customer, your order has been delivered today by: "
                + Thread.currentThread().getName()
        ));

        cNotifyCustomer.get();
        logger.info(() -> "Order was delivered and customer was notified\n");
    }
}
