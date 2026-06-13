package ImplementProblems.DijkstraAlgo.SmallestNumberOfNeighborsAtK;

import java.util.*;

public class Main {
    /*
        There are n cities numbered from 0 to n-1. Given the array edges where edges[i] = [fromi, toi, weighti]
        represents a bidirectional and weighted edge between cities fromi and toi, and given the integer distanceThreshold.

        Return the city with the smallest number of cities that are reachable through some path and whose distance
        is at most distanceThreshold, If there are multiple such cities, return the city with the greatest number.

        Notice that the distance of a path connecting cities i and j is equal to the sum of the edges' weights along
        that path.

        Input: n = 4, edges = [[0,1,3],[1,2,1],[1,3,4],[2,3,1]], distanceThreshold = 4
        Output: 3
        Explanation: The figure above describes the graph.
        The neighboring cities at a distanceThreshold = 4 for each city are:
        City 0 -> [City 1, City 2]
        City 1 -> [City 0, City 2, City 3]
        City 2 -> [City 0, City 1, City 3]
        City 3 -> [City 1, City 2]
        Cities 0 and 3 have 2 neighboring cities at a distanceThreshold = 4, but we have to return city 3 since it has
        the greatest number.
     */
    public static void main(String[] args) {
        int n = 5;
        int[][] edges = new int[][]{{0,1,2}, {0,4,8}, {1,2,3}, {1,4,2}, {2,3,1}, {3,4,1}};
        int distThreshold = 2;
        System.out.println(findTheCity(n, edges, distThreshold));

        n = 4;
        edges = new int[][]{{0,1,3}, {1,2,1}, {1,3,4}, {2,3,1}};
        distThreshold = 4;
        System.out.println(findTheCity(n, edges, distThreshold));
    }

    public static int findTheCity(int n, int[][] edges, int distanceThreshold) {
        // array of lists
        List<int[]>[] adjacencyList = new List[n];
        int[][] shortestPathMatrix = new int[n][n];

        for(int i=0; i<n; i++) {
            Arrays.fill(shortestPathMatrix[i], Integer.MAX_VALUE);
            shortestPathMatrix[i][i] = 0;
            adjacencyList[i] = new ArrayList<>();
        }

        for(int[] edge : edges) {
            int start = edge[0];
            int end = edge[1];
            int weight = edge[2];

            adjacencyList[start].add(new int[]{end, weight});
            adjacencyList[end].add(new int[]{start, weight});
        }

        // Compute the shortest path from each city
        for(int i=0; i<n; i++) {
            djikstra(n, adjacencyList, shortestPathMatrix[i], i);
        }

        // Find the city with the fewest number of reachable city within the distance threshold
        return getCityWithFewestReachable(n, shortestPathMatrix, distanceThreshold);
    }

    private static void djikstra(int n, List<int[]>[] adj, int[] shortestPathDistances, int source) {
        // Priority Queue to process nodes with shortest distance first
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[] {source, 0});
        Arrays.fill(shortestPathDistances, Integer.MAX_VALUE);
        shortestPathDistances[source] = 0;

        while(!pq.isEmpty()) {
            int[] curr = pq.poll();
            int currCity = curr[0];
            int currDist = curr[1];

            // we found other path which gives shortest distance
            if(currDist>shortestPathDistances[currCity]) {
                continue;
            }

            for(int[] neighbor : adj[currCity]) {
                int neighborCity = neighbor[0];
                int edgeWeight = neighbor[1];
                if(shortestPathDistances[neighborCity] > currDist + edgeWeight) {
                    shortestPathDistances[neighborCity] = currDist + edgeWeight;
                    pq.add(new int[]{neighborCity , shortestPathDistances[neighborCity]});
                }
            }
        }
    }

    private static int getCityWithFewestReachable(int n, int[][] shortestPathMatrix, int distanceThreshold) {
        int fewestReachableCount = n;
        int cityWithFewestReachable = -1;
        for(int i=0; i<n; i++) {
            int reachableCount = 0;
            for(int j=0; j<n; j++) {
                if(i == j) {
                    continue;
                }
                if(shortestPathMatrix[i][j] <= distanceThreshold) {
                    reachableCount++;
                }
            }
            if(reachableCount <=  fewestReachableCount) {
                fewestReachableCount = reachableCount;
                cityWithFewestReachable = i;
            }
        }
        return cityWithFewestReachable;
    }
}
