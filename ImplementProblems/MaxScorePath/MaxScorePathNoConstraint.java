package ImplementProblems.MaxScorePath;

/*
    Given grid with values 0,1,2 find max score path sum from (0,0) to (m-1,n-1)
 */
public class MaxScorePathNoConstraint {
    public static void main(String[] args) {
        int[][] grid = {
                {0,1,2,0},
                {1,2,2,1},
                {0,0,1,1},
                {2,1,1,2}
        };

        System.out.println(solve(grid));
    }

    /*
        dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1])+grid[i][j]
     */
    private static int solve(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int[][] dp = new int[m][n];
        for(int i=0; i<m; i++) {
            for(int j=0; j<n; j++) {
                if(i==0 || j==0) {
                    dp[i][j] = grid[i][j];
                }
                else {
                    dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]) + grid[i][j];
                }
            }
        }
        return dp[m-1][n-1];
    }
}
