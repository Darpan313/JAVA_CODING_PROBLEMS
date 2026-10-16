package ImplementProblems.MaxScorePath;

import java.util.Arrays;

/*
    Given an m x n grid where each cell contains one of the values 0, 1, or 2. You are also given an integer k.
    You start from the top-left corner (0, 0) and want to reach the bottom-right corner (m - 1, n - 1) by moving only right or down.
    Each cell contributes a specific score and incurs an associated cost, according to their cell values:

    0: adds 0 to your score and costs 0.
    1: adds 1 to your score and costs 1.
    2: adds 2 to your score and costs 1.
    Return the maximum score achievable without exceeding a total cost of k, or -1 if no valid path exists.

    Note: If you reach the last cell but the total cost exceeds k, the path is invalid.
 */
public class MaxScorePathWithConstraint {
    public static void main(String[] args) {
        int[][] grid = {
                {0,1,2,2},
                {0,2,2,1},
                {0,0,2,1},
                {2,1,1,2}
        };
        int k = 4;
        System.out.println(solve(grid, k));
    }

    private static int solve(int[][] grid, int k) {
        int m = grid.length;
        int n = grid[0].length;
        int[][][] memo = new int[m][n][k+1];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                Arrays.fill(memo[i][j], Integer.MIN_VALUE);
            }
        }

        dfs(grid, m-1, n-1, k, memo);
        return memo[m-1][n-1][k];
    }

    private static int dfs(int[][] grid, int r, int c, int remainK, int[][][] memo) {
        if(r<0 || c<0 || remainK<0) {
            return Integer.MIN_VALUE;
        }
        if(r==0 && c==0) {
            return 0;
        }

        if (memo[r][c][remainK] != Integer.MIN_VALUE) {
            return memo[r][c][remainK];
        }

        int score = grid[r][c];
        int cost = (grid[r][c]>0) ? 1 : 0;
        int nextK = remainK - cost;
//        if(nextK<0) {
//            memo[r][c][remainK] = Integer.MIN_VALUE;
//            return memo[r][c][remainK];
//        }
        memo[r][c][remainK] = score + Math.max(dfs(grid, r-1, c, nextK, memo), dfs(grid, r, c-1, nextK, memo));
        return memo[r][c][remainK];
    }
}
