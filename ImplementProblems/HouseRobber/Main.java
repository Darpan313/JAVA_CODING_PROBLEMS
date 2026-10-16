package ImplementProblems.HouseRobber;

public class Main {
    /*
        You are a professional robber planning to rob houses along a street. Each house has a certain amount of money
        stashed, the only constraint stopping you from robbing each of them is that adjacent houses have security systems
        connected and it will automatically contact the police if two adjacent houses were broken into on the same night.

        Given an integer array nums representing the amount of money of each house, return the maximum amount of money
        you can rob tonight without alerting the police.

        Input: nums = [1,2,3,1]
        Output: 4
        Explanation: Rob house 1 (money = 1) and then rob house 3 (money = 3).
        Total amount you can rob = 1 + 3 = 4.
     */
    public static void main(String[] args) {

    }

    public int rob(int[] nums) {
        //dp[i] = Math.max(nums[i-1]+dp[i-2], dp[i-1]);
        int n = nums.length;
        int[] dp = new int[n+1];
        dp[0] = 0;
        dp[1] = nums[0];

        for(int i=2; i<=n; i++) {
            dp[i] = Math.max(nums[i-1]+dp[i-2], dp[i-1]);
        }
        return dp[n];
    }
}
