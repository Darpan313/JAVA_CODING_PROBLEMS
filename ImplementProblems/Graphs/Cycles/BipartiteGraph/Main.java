package ImplementProblems.Graphs.Cycles.BipartiteGraph;

import java.util.*;

public class Main {
    /*
        Given an undirected graph with V vertices labeled from 0 to V-1. The graph is represented using an 2D vector
        edges ,where edges[i] represent an undirected edge between edges[i][0] and edges[i][1].
        Determine if the graph is bipartite or not.
        A graph is bipartite if the nodes can be partitioned into two independent sets A and B such that
        every edge in the graph connects a node in set A and a node in set B.
     */
    public static void main(String[] args) {
        int V = 4;
        List<List<Integer>> edges = List.of(List.of(0,1), List.of(0,3), List.of(1,2), List.of(2,3));
        List<List<Integer>> adj = new ArrayList<>(V);
        for(int i=0; i<V; i++) {
            adj.add(new ArrayList<>());
        }
        for(List<Integer> edge : edges) {
            int u = edge.get(0), v = edge.get(1);
            adj.get(u).add(v);
            adj.get(v).add(u);
        }

        boolean res = isBipartiteBFS(V, adj);
        System.out.println(res);

        res = isBipartiteDFS(V, adj);
        System.out.println(res);
    }

    private static boolean isBipartiteDFS(int V, List<List<Integer>> adj) {

    }

    private static boolean isBipartiteBFS(int V, List<List<Integer>> adj) {
        int[] color = new int[V];
        Arrays.fill(color, -1);

        for(int i=0; i<V; i++) {
            if(color[i] == -1) {
                if(!bfs(i, adj, color)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean bfs(int node, List<List<Integer>> adj, int[] color) {
        color[node] = 0;
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(node);

        while(!queue.isEmpty()) {
            int currNode = queue.poll();

            for(int neighbor : adj.get(currNode)) {
                if(color[neighbor] == -1) {
                    color[neighbor] = 1 - color[currNode];
                    queue.offer(neighbor);
                }
                else if(color[neighbor] == color[currNode]) {
                    return false;
                }
            }
        }
        return true;
    }
}
