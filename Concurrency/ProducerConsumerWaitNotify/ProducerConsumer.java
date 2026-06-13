package Concurrency.ProducerConsumerWaitNotify;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ProducerConsumer {
    private static final int MAX_PROD_TIME_MS = 1000;
    private static final int MAX_CONS_TIME_MS = 1000;
    private static final int TIMEOUT_MS = 5000;
    private static final Logger logger = Logger.getLogger(ProducerConsumer.class.getName());
    private static final Random rand = new Random();
    private static final List<String> queue = new LinkedList<>();
    private static final Producer producer = new Producer();
    private static final Consumer consumer = new Consumer();
    private static volatile boolean runningProducer;
    private static volatile boolean runningConsumer;
    private static ExecutorService producerService;
    private static ExecutorService consumerService;

    private ProducerConsumer() {
        throw new AssertionError("Cannot be Instantiated");
    }

    public static void startProducerConsumer() {
        if (runningProducer || runningConsumer) {
            logger.info("Producer-consumer already running...");
            return;
        }

        logger.info("\n\nStrating Producer-Consumer ...");
        queue.clear();

        runningProducer = true;
        producerService = Executors.newSingleThreadExecutor();
        producerService.execute(producer);

        runningConsumer = true;
        consumerService = Executors.newSingleThreadExecutor();
        consumerService.execute(consumer);
    }

    public static void stopProducerConsumer() {
        logger.info("Stopping Producer-Consumer...");

        boolean isProducerDown = shutDownProducer();
        boolean isConsumerDown = shutDownConsumer();

        if (!isProducerDown || !isConsumerDown) {
            logger.severe("Something abnormal happened during shutdown the Producer-COnsumer");
            System.exit(0);
        }
    }

    private static boolean shutDownProducer() {
        runningProducer = false;
        return shutDownExecutor(producerService);
    }

    private static boolean shutDownConsumer() {
        runningConsumer = false;
        return shutDownExecutor(consumerService);
    }

    private static boolean shutDownExecutor(ExecutorService executor) {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();

                return executor.awaitTermination(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            }
            return true;
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            logger.severe(() -> "Exception: " + ex);
        }
        return false;
    }

    private static class Producer implements Runnable {

        @Override
        public void run() {
            while (runningProducer) {
                synchronized (queue) {
                    while (!queue.isEmpty()) {
                        logger.info("Queue is not empty ...");

                        try {
                            queue.wait();
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                            logger.severe(() -> "Exception: " + ex);
                            break;
                        }
                    }
                }

                synchronized (queue) {
                    try {
                        String product = "product-" + rand.nextInt(1000);
                        Thread.sleep(rand.nextInt(MAX_PROD_TIME_MS));

                        queue.add(product);
                        logger.info(() -> "Produced: " + product);

                        queue.notify();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        logger.severe(() -> "Exception: " + ex);
                    }
                }
            }
        }
    }

    private static class Consumer implements Runnable {

        @Override
        public void run() {
            while (runningConsumer) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        logger.info("Queue is Empty ...");

                        try {
                            queue.wait();
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                            logger.severe(() -> "Exception: " + ex);
                            break;
                        }
                    }
                }

                synchronized (queue) {
                    try {
                        String product = queue.remove(0);

                        if (product != null) {
                            Thread.sleep(rand.nextInt(MAX_CONS_TIME_MS));
                            logger.info(() -> "Consumed:  " + product);

                            queue.notify();
                        }
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        logger.severe(() -> "Exception: " + ex);
                        break;
                    }
                }
            }
        }
    }
}
