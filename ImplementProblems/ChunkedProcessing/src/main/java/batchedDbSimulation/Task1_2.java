package batchedDbSimulation;

import chunkedprocessing.ChunkIteratorImpl;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Random;

public class Task1_2 {
    public static void main(String[] args) {
        long runningSum = 0;
        long runningCount = 0;
        long runningMax = Long.MIN_VALUE;

        List<Integer> list = new Random(42).ints(100_000, 0, Integer.MAX_VALUE).boxed().toList();
        ChunkIteratorImpl<Integer> chunkIterator = new ChunkIteratorImpl<>(list, 10000);

        long chunkNum = 0;
        while (chunkIterator.hasNext()) {
            chunkNum++;
            List<Integer> chunk = chunkIterator.nextChunk();
            IntSummaryStatistics stats = chunk.stream()
                    .mapToInt(Integer::intValue)
                    .summaryStatistics();
            runningSum += stats.getSum();
            runningCount += stats.getCount();
            runningMax = Math.max(stats.getMax(), runningMax);

            System.out.println("chunkNumber: " + chunkNum);
            System.out.println("chunkSum: " + stats.getSum());
            System.out.println("chunkAvg: " + stats.getAverage());
            System.out.println("chunkMax: "+ stats.getMax());
        }
        System.out.println("runningTotal: " + runningSum);
        System.out.println("globalAvg: " + runningSum/runningCount);
        System.out.println("globalMax: " + runningMax);
    }
}
