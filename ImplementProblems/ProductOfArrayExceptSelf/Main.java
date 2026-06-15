package ImplementProblems.ProductOfArrayExceptSelf;

public class Main {
    /*
        Given an integer array nums, return an array answer such that answer[i] is equal to the product of all the
        elements of nums except nums[i].

        The product of any prefix or suffix of nums is guaranteed to fit in a 32-bit integer.

        You must write an algorithm that runs in O(n) time and without using the division operation.

        Example 1:
            Input: nums = [1,2,3,4]
            Output: [24,12,8,6]
     */
    public static void main(String[] args) {
        int[] nums = new int[] {1,2,3,4};
        int[] res = productExceptionSelf(nums);
        for(int i=0; i<nums.length; i++) {
            System.out.printf(res[i] + " ");
        }
        System.out.println();
    }

    private static int[] productExceptionSelf(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        res[0] = 1;

        for(int i=1; i<n; i++) {
            res[i] = res[i-1] * nums[i-1];
        }

        int suffix = 1;

        for(int i=n-1; i>=0; i--) {
            res[i] *= suffix;
            suffix *= nums[i];
        }
        return res;
    }
}
