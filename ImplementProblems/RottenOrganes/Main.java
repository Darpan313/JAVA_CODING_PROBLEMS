package ImplementProblems.RottenOrganes;

import java.util.LinkedList;
import java.util.Queue;

public class Main {
    /*
        You are given an m x n grid where each cell can have one of three values:
           0 representing an empty cell,
           1 representing a fresh orange, or
           2 representing a rotten orange.
        Every minute, any fresh orange that is 4-directionally adjacent to a rotten orange becomes rotten.
        Return the minimum number of minutes that must elapse until no cell has a fresh orange. If this is impossible, return -1.
     */

    public static void main(String[] args) {
        int[][] grid = new int[][]{{2, 1, 1}, {1, 1, 0}, {0, 1, 1}};
        int minimumTime = orangesRotting(grid);
        System.out.println(minimumTime);

        // ----
        grid = new int[][]{{2, 1, 1}, {0, 1, 1}, {1, 0, 1}};
        minimumTime = orangesRotting(grid);
        System.out.println(minimumTime);
    }

    private static int orangesRotting(int[][] grid) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        int rows = grid.length;
        int cols = grid[0].length;
        int count = 0;
        Queue<Pair> queue = new LinkedList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 2) {
                    queue.add(new Pair(i, j));
                }
            }
        }

        if(queue.isEmpty()) {
            return isFreshLeft(grid, rows, cols) ? -1 : 0;
        }

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Pair currPos = queue.poll();

                for (int[] dir : directions) {
                    int newX = currPos.key + dir[0];
                    int newY = currPos.value + dir[1];

                    if (newX >= 0 && newX < rows && newY >= 0 && newY < cols && grid[newX][newY] == 1) {
                        grid[newX][newY] = 2;
                        queue.add(new Pair(newX, newY));
                    }
                }
            }
            count++;
        }

        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                if(grid[i][j] == 1) {
                    return -1;
                }
            }
        }
        return isFreshLeft(grid, rows, cols) ? -1 : count-1;
    }

    public static boolean isFreshLeft(int[][] grid, int rows, int cols) {
        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                if(grid[i][j] == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public record Pair(int key, int value) {
    }
}
