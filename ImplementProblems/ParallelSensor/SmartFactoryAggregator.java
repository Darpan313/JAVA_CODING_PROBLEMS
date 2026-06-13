package ImplementProblems.ParallelSensor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SmartFactoryAggregator {
    public static void main(String[] args) {
        SensorService sensorService = new SensorService();
        List<String> sensors = List.of("Temperature", "Humidity", "Air Quality");

        ExecutorService executor = Executors.newFixedThreadPool(3);

        System.out.println("Starting parallel sensor fetch..\n");
        long startTime = System.currentTimeMillis();

        List<CompletableFuture<SensorService.SensorReading>> futuresList = sensors.stream()
                .map(s -> CompletableFuture.supplyAsync(() -> sensorService.fetchReading(s), executor))
                .toList();

        CompletableFuture<?>[] futuresArray = futuresList.toArray(new CompletableFuture[0]);

        CompletableFuture<SensorService.FactoryHealthReport> reportFuture = CompletableFuture.allOf(futuresArray)
                .thenApply(v -> {
                   List<SensorService.SensorReading> readings = futuresList.stream()
                           .map(CompletableFuture::join)
                           .toList();
                   return new SensorService.FactoryHealthReport(readings);
                });

        SensorService.FactoryHealthReport finalReport = reportFuture.join();

        long duration = System.currentTimeMillis() - startTime;

        System.out.println("\n --- Final Factory Health Report ---");
        finalReport.readings().forEach(r ->
                System.out.println(r.sensorName() + ": " + r.value()));
        System.out.println("--------------------------------------");
        System.out.println("Total time taken: " + duration + "ms");

        executor.shutdown();

        try {
            if(!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
        }
    }
}
