package Concurrency.CallableAndFuture;

import java.util.Random;
import java.util.concurrent.*;
import java.util.logging.Logger;

/*
    In order to shape this scenario, the producer should be able to return a result and throw an exception.
    Since our producer is a Runnable, it can't do either of these. But Java defines an interface
    that is named Callable. This is a functional interface with a method named call().
    In contrast to the run() method of Runnable, the call() method can return a result and even throw
    an exception, V call() throws Exception
 */
public class AssemblyLine {
    private AssemblyLine() {
        throw new AssertionError("There is a single assembly line!");
    }

    private static final int MAX_PROD_TIME_MS = 5*1000;
    private static final int MAX_CONS_TIME_MS = 3*1000;
    private static final int TIMEOUT_MS = MAX_PROD_TIME_MS + MAX_CONS_TIME_MS +1000;

    private static Logger logger = Logger.getLogger(AssemblyLine.class.getName());
    private static final Random rand = new Random();

    private static volatile boolean runningProducer;
    private static volatile boolean runningConsumer;

    private static ExecutorService producerService;
    private static ExecutorService consumerService;

    private static class Producer implements Callable {
        private final String bulb;

        private Producer(String bulb) {
            this.bulb = bulb;
        }
        @Override
        public Object call() throws DefectBulbException, InterruptedException {
            if(runningProducer) {
                Thread.sleep(rand.nextInt(MAX_PROD_TIME_MS));

                if(rand.nextInt(100) < 5) {
                    throw new DefectBulbException("Defect: " + bulb);
                } else {
                    logger.info(() -> "Checked: "+bulb);
                }
                return bulb;
            }
            return "";
        }
    }

    private static class Consumer implements Runnable {
        private final String bulb;

        private Consumer(String bulb) {
            this.bulb = bulb;
        }

        @Override
        public void run() {
            if(runningConsumer) {
                try {
                    Thread.sleep(rand.nextInt(MAX_CONS_TIME_MS));
                    logger.info(() -> "Packed: "+bulb);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    logger.severe(() -> "Exception: "+ ex);
                }
            }
        }
    }

    public static void startAssemblyLine() {
        if(runningProducer || runningConsumer) {
            logger.info("Assembly line is already running ...");
            return;
        }

        logger.info("\nStarting assembly line ...");

        runningProducer = true;
        consumerService = Executors.newSingleThreadExecutor();

        runningConsumer = true;
        producerService = Executors.newSingleThreadExecutor();

        new Thread(() -> {
           automaticSystem();
        }).start();
    }

    public static void stopAssemblyLine() {
        logger.info("Stopping assembly line...");

        boolean isProducerDown = shutDownProducer();
        boolean isConsumerDown = shutDownConsumer();

        if(!isProducerDown || !isConsumerDown) {
            logger.severe("Something abnormal happened during shutting down the assembly line!");
            System.exit(0);
        }
        logger.info("Assembling line was successfuly stopped.");
    }
    private static void automaticSystem() {
        while(runningProducer && runningConsumer) {
            String bulb = "bulb_" + rand.nextInt(1000);
            Producer producer = new Producer(bulb);

            Future<String> bulbFuture = producerService.submit(producer);

            try {
                String checkedBulb = bulbFuture.get(MAX_PROD_TIME_MS, TimeUnit.MILLISECONDS);

                Consumer consumer = new Consumer(checkedBulb);
                if(runningConsumer) {
                    consumerService.execute(consumer);
                }
            } catch(ExecutionException ex) {
                logger.severe(() -> "Exception: " + ex.getCause());
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
                logger.severe(() -> "Exception: " + ex);
            } catch (TimeoutException ex) {
                logger.severe("The producer doesn't respect the checking time!");
            }
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
            if(!executor.awaitTermination(TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
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
}
