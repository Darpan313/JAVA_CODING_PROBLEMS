package ImplementProblems.KMostFrequentElements;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Integer> input = List.of(1, 3, 4, 2, 1, 3, 3, 5, 2);
        int k = 2;
        List<Integer> res = topK(input, k);
        System.out.println(res);
    }

    //non-stream
    private static List<Integer> topK(List<Integer> list, int k) {
        List<Integer> res = new ArrayList<>();
        Map<Integer, Integer> map = new HashMap<>();

        for (int n : list) {
            map.put(n, map.getOrDefault(n, 0) + 1);
        }
        // return min count element if two element has same count return higher one.
        PriorityQueue<Map.Entry<Integer, Integer>> pq = new PriorityQueue<>((a, b) ->
                a.getValue().equals(b.getValue()) ? Integer.compare(b.getKey(), a.getKey()) :
                        Integer.compare(a.getValue(), b.getValue()));

        //O(NlogN) - optimize by actively keeping data-structure short
//        pq.addAll(map.entrySet());

        // since we discard here, we should discard element with low count and if two element has same count discard higher
        // element
        for(Map.Entry<Integer, Integer> entry : map.entrySet()) {
            pq.add(entry);
            if(pq.size() > k) {
                pq.poll();
            }
        }

        for(int i=0; i<k; i++) {
            res.add(pq.poll().getKey());
        }
        return res;
    }
}
