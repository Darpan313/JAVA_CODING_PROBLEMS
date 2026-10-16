package ImplementProblems.LargeIPProcessor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

public class LargeIpProcessor {
    private static final int NUM_OF_SHARDS = 100; // Adjust this based on memory limits
    private static final int TOP_K = 10;
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    public static void main(String[] args) {
        Path inputLogFile = Paths.get("gateway_traffic.log");

        // For testing purposes, you would create a dummy file here.
         createDummyData(inputLogFile);

        System.out.println("Starting processing...");
        long startTime = System.currentTimeMillis();

        try {
            processLargeLogFile(inputLogFile);
        } catch (IOException ex) {
            System.err.println("Fatal error processing file: " + ex.getMessage());
        }

        System.out.println("Completed in " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public static void processLargeLogFile(Path logFile) throws IOException {
        //PHASE-1 - Stream, Normalize and Shard
        System.out.println("Sharding large input file...");
        shardInputFile(logFile);

        //PHASE-2 & 3 - Aggregate buckets and maintain Global Top-K
        System.out.println("Phase 2 & 3: Processing shards and merging results...");
        long totalUniqueIps = 0;

        // Min-Heap to keep track of the Top K elements
        // We use Min-Heap so we can easily drop smallest of Top K in O(1) time.
        PriorityQueue<Map.Entry<String, Integer>> globalTopK = new PriorityQueue<>(
                Comparator.comparingInt(Map.Entry::getValue));

        for(int i=0; i<NUM_OF_SHARDS; i++) {
            Path shardFile = Paths.get(TEMP_DIR, "shard_" + i + ".log");
            if(!Files.exists(shardFile)) continue;

            //Processing individual shard
            Map<String, Integer> localCounts = countIpsInShard(shardFile);
            totalUniqueIps += localCounts.size();

            // Update Global Top-K
            for(Map.Entry<String, Integer> entry : localCounts.entrySet()) {
                globalTopK.offer(entry);
                if(globalTopK.size() > TOP_K) {
                    globalTopK.poll();
                }
            }

            Files.delete(shardFile);
        }

        printResults(totalUniqueIps, globalTopK);
    }

    private static void shardInputFile(Path file) throws IOException {
        BufferedWriter[] writers = new BufferedWriter[NUM_OF_SHARDS];

        try {
            for(int i=0; i<NUM_OF_SHARDS; i++) {
                Path shardFile = Paths.get(TEMP_DIR, "shard_" + i + ".log");
                writers[i] = Files.newBufferedWriter(shardFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }

            // Read the massive file lazily using Streams
            try(Stream<String> lines = Files.lines(file)) {
                lines.forEach(line -> {
                    String normalizedIp = normalizeIp(line);
                    if(normalizedIp != null) {
                        // Use bitwise AND to avoid negative numbers from Math.abs(Integer.MIN_VALUE)
                        int shardIndex = (normalizedIp.hashCode() & 0x7FFFFFFF) % NUM_OF_SHARDS;

                        try {
                            writers[shardIndex].write(normalizedIp);
                            writers[shardIndex].newLine();
                        } catch (IOException ex) {
                            throw new UncheckedIOException(ex);
                        }
                    }
                });
            }
        } finally {
            for(BufferedWriter writer : writers) {
                if(writer != null) {
                    writer.close();
                }
            }
        }

    }

    /**
     * Reads a single shard into memory and counts frequencies.
     */
    private static Map<String, Integer> countIpsInShard(Path shardFile) throws IOException {
        Map<String, Integer> counts = new HashMap<>();
        try(Stream<String> lines = Files.lines(shardFile)) {
            lines.forEach(ip -> counts.put(ip, counts.getOrDefault(ip, 0)+1));
        }
        return counts;
    }

    private static String normalizeIp(String rawLine) {
        if(rawLine == null || rawLine.isEmpty()) {
            return null;
        }
        String cleanLine = rawLine.strip();

        try {
            InetAddress address = InetAddress.getByName(cleanLine);
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    private static void printResults(long totalUnique, PriorityQueue<Map.Entry<String, Integer>> globalTopK) {
        System.out.println("\n=== PROCESSING RESULTS ===");
        System.out.println("Total Unique IPs: " + totalUnique);
        System.out.println("Top " + TOP_K + " Most Frequent IPs:");

        List<Map.Entry<String, Integer>> sortedTopK = globalTopK.stream().
                sorted(Comparator.comparingInt((Map.Entry<String, Integer> e) -> e.getValue()).reversed()).toList();

        for (Map.Entry<String, Integer> entry : sortedTopK) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
        System.out.println("=========================\n");
    }

    /**
     * Generates a realistic, large dummy log file to test the processor.
     * Injects predictable Top-K IPs, formatting edge cases, and random noise.
     */
    private static void createDummyData(Path inputLogFile) {
        System.out.println("Generating 5,000,000 lines of dummy data... (This may take a few seconds)");
        long start = System.currentTimeMillis();
        int totalLines = 5_000_000;

        try (BufferedWriter writer = Files.newBufferedWriter(
                inputLogFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            Random random = new Random();

            // Expected Top 3 (after normalization)
            String top1 = "192.168.1.100";
            String top2 = "10.0.0.5";
            String top3 = "2001:db8::1";

            for (int i = 0; i < totalLines; i++) {
                int chance = random.nextInt(100);
                String lineToWrite;

                if (chance < 20) {
                    lineToWrite = top1; // ~20% of the file
                } else if (chance < 35) {
                    lineToWrite = top2; // ~15% of the file
                } else if (chance < 45) {
                    lineToWrite = top3; // ~10% of the file
                } else if (chance < 50) {
                    // Normalizes to top1! Tests leading zeros and whitespace.
                    // This proves our parser accurately groups formatting differences.
                    lineToWrite = "   192.168.001.100  ";
                } else if (chance < 52) {
                    lineToWrite = "invalid_ip_format_here"; // Edge Case: Invalid text
                } else if (chance < 53) {
                    lineToWrite = ""; // Edge Case: Empty line
                } else {
                    // Random noise IPs to fill the rest of the file
                    lineToWrite = random.nextInt(256) + "." +
                            random.nextInt(256) + "." +
                            random.nextInt(256) + "." +
                            random.nextInt(256);
                }

                writer.write(lineToWrite);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to create dummy data: " + e.getMessage());
        }

        long fileSizeMB = inputLogFile.toFile().length() / (1024 * 1024);
        System.out.println("Dummy data generated in " + (System.currentTimeMillis() - start) + "ms. File size: ~" + fileSizeMB + " MB\n");
    }
}
