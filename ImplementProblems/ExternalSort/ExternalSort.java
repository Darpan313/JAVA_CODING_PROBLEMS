package ImplementProblems.ExternalSort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ExternalSort {
    public static void main(String[] args) {
        List<List<Integer>> input = List.of(
                List.of(1, 2, 3),
                List.of(4, 5),
                List.of(6, 7, 8, 9)
        );
        System.out.println(kWayMerge(input));
    }

    private static List<Integer> kWayMerge(List<List<Integer>> sortedChunks) {
        List<Integer> result = new ArrayList<>();
        int[] listIndexMap = new int[sortedChunks.size()];
        PriorityQueue<Pair> minHeap = new PriorityQueue<>(Comparator.comparingInt(p -> p.value));
        for (int i = 0; i < sortedChunks.size(); i++) {
            if (!sortedChunks.get(i).isEmpty()) {
                minHeap.add(new Pair(i, sortedChunks.get(i).getFirst()));
                listIndexMap[i] = 0;
            }
        }
        while (!minHeap.isEmpty()) {
            Pair curr = minHeap.poll();
            result.add(curr.value);
            if (listIndexMap[curr.index] + 1 < sortedChunks.get(curr.index).size()) {
                listIndexMap[curr.index]++;
                minHeap.add(new Pair(curr.index, sortedChunks.get(curr.index).get(listIndexMap[curr.index])));
            }
        }
        return result;
    }

    record Pair(int index, int value) {
    }
}
