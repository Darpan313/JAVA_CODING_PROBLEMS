package ImplementProblems.Graphs.Cycles.DetectCycleInUndirectedGraph;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Main {
    /*
        Given an undirected graph with V vertices labeled from 0 to V-1. The graph is represented using an adjacency
        list where adj[i] lists all nodes connected to node. Determine if the graph contains any cycles.

        Note: The graph does not contain any self-edges (edges where a vertex is connected to itself).
     */
    public static void main(String[] args) {
        int V = 6;
        List<Integer>[] adj = new List[]{List.of(1,3), List.of(0,2,4), List.of(1,5), List.of(0,4), List.of(1,3,5), List.of(2,4)};
        System.out.println(isCycleBFS(V, adj));
        System.out.println(isCycleDFS(V, adj));
    }

    private static boolean isCycleBFS(int V, List<Integer>[] adj) {
        boolean ans = false;
        boolean[] visited = new boolean[V];
        for(int i=0; i<V; i++) {
            if(!visited[i]) {
                ans = bfs(i, adj, visited);
                if(ans) break;
            }
        }
        return ans;
    }

    private static boolean bfs(int node, List<Integer>[] adj, boolean[] visited) {
        Queue<int[]> queue = new LinkedList<>();

        queue.offer(new int[]{node, -1});
        visited[node] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int currNode = current[0];
            int parentNode = current[1];

            for(int neighbor : adj[currNode]) {
                if(!visited[neighbor]) {
                    queue.offer(new int[]{neighbor, currNode});
                    visited[neighbor] = true;
                }
                else if(neighbor != parentNode) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isCycleDFS(int V, List<Integer>[] adj) {
        boolean[] visited = new boolean[V];

        for(int i=0; i<V; i++) {
            if(!visited[i]) {
                if(dfs(i, adj, visited, -1)) {
                    return true;
                }
            }
        }
        return false;
    }
    private static boolean dfs(int node, List<Integer>[] adj, boolean[] visited, int prev) {
        visited[node] = true;

        for(int neighbor : adj[node]) {
            if(!visited[neighbor]) {
                if(dfs(neighbor, adj, visited, node)) {
                    return true;
                }
            }
            else if(neighbor != prev) {
                return true;
            }
        }
        return false;
    }
}
