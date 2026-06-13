package ImplementProblems.DijkstraAlgo.CheapestFlightsWithInKStops;

import java.util.*;

public class Main {
    /*
        There are n cities connected by some number of flights. You are given an array flights where
        flights[i] = [fromi, toi, pricei] indicates that there is a flight from city fromi to city toi with cost pricei.

        You are also given three integers src, dst, and k, return the cheapest price from src to dst with at most
        k stops. If there is no such route, return -1.
     */
    public static void main(String[] args) {
        int[][] flights = new int[][] {{0,1,100}, {1,2,100}, {2,0,100}, {1,3,600}, {2,3,200}};
        int n = 4;
        int src = 0;
        int dst = 3;
        int k = 1;
        System.out.println(findCheapestPrice(n, flights, src, dst, k));
    }

    public static int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        // create an adjacency list, to answer neighbors of node.
        Map<Integer, List<int[]>> adj = new HashMap<>();
        for(int[] flight : flights) {
            adj.computeIfAbsent(flight[0], value -> new ArrayList<>()).add(new int[] {flight[1], flight[2]});
        }

        int[] minStops = new int[n];
        Arrays.fill(minStops, Integer.MAX_VALUE);

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        // {node, distance_from_source_node, number_of_stops_from_source_node}
        pq.offer(new int[]{src, 0, 0});

        while(!pq.isEmpty()) {
            int[] temp = pq.poll();
            int node = temp[0];
            int cost = temp[1];
            int steps = temp[2];

            // If we reach destination, return the cost.
            // Because we use MIN_HEAP, the FIRST time we reach dst, it is guaranteed to be the cheapest path
            if(node == dst) {
                return cost;
            }

            // Pruning-1: If we have reached out stop limit, we can't take any more flights
            if(steps>k+1) {
                continue;
            }

            // Pruning-2: If we have already reached this node with fewer or equal stops,
            // we can discard this path. (We have already found cheaper route with <= stops)
            // The only reason to go further is if we can reach this node in fewer steps even though expensive.
            if(steps >= minStops[node]) {
                continue;
            }
            // Update the minimum stops to reach this node.
            minStops[node] = steps;

            if(!adj.containsKey(node)) {
                continue;
            }

            // Explore the neighbors
            for(int[] connectedCity : adj.get(node)) {
                pq.offer(new int[]{connectedCity[0], cost + connectedCity[1], steps+1});
            }
        }
        return -1;
    }
}
