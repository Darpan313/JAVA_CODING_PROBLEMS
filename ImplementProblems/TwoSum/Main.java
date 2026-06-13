package ImplementProblems.TwoSum;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        //return all pair of indices which sums to target value;
        int target = 12;
        int[] arr = {1,3,9,8,7,5};

        int[] indices = twoSum(arr, target);
        System.out.println(indices[0] +","+indices[1]);
    }

    private static int[] twoSum(int[] arr, int target) {
        Map<Integer, Integer> seen = new HashMap<>();

        for(int i=0; i<arr.length; i++) {
            int need = target - arr[i];
            if (seen.containsKey(need)) {
                return new int[] {seen.get(need), i};
            }
            seen.put(arr[i], i);
        }
        return new int[]{-1,-1};
    }
}
