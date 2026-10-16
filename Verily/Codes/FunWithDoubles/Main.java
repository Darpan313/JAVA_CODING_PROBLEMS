package Verily.Codes.FunWithDoubles;

import java.util.Arrays;

/*
    Fun with Doubles
    Part 1
        Given an array arr of integers, double each element and concatenate.
        For example, [1, 2, 4] --> [1, 2, 4, 2, 4, 8]
        notice the pattern in the output?

    Part 2
        Given an output that may or may not come from part 1, identify the inverse.

        [1, 2, 4, 2, 4, 8] --> [1, 2, 4]
        [4, 2, 8, 16] --> [2, 8]
        [1, 4] --> NO INVERSE
 */
public class Main {
    public static void main(String[] args) {
        int[] arr = new int[]{1, 2, 4};
        int[] res = funWithDoubles(arr);
        print(res);
        //-----//
        arr = new int[]{1, 2, 4, 2, 4, 8};
        res = funWithDoubles(arr);
        print(res);
        //-----//
        arr = new int[]{4, 2, 8, 16};
        Arrays.sort(arr);
        res = funWithDoubles(arr);
        print(res);
        //-----//
        arr = new int[]{1, 2, 2, 4, 4, 8, 5};
        res = funWithDoubles(arr);
        print(res);
        //-----//
        arr = new int[]{1, 2, 2, 4, 4, 8, 5, 2, 4, 4, 8, 8, 16, 10};
        res = funWithDoubles(arr);
        print(res);
    }

    private static void print(int[] res){
        System.out.print("[");
        for(int i=0; i<res.length; i++) {
            System.out.print(res[i]+" ");
        }
        System.out.println("]");
    }

    private static int[] funWithDoubles(int[] arr) {
        int l = arr.length;
        boolean oneSize = checkWinSize(1, arr);
        if(oneSize) {
            return new int[]{arr[0]};
        }
        if(l%2 != 0) {
            return arr;
        }
        for(int win=2; win<=l/2; win++) {
            if(checkWinSize(win, arr)) {
                int[] res = new int[win];
                for(int i=0; i<win; i++) {
                    res[i] = arr[i];
                }
                return res;
            }
        }
        return arr;
    }

    private static boolean checkWinSize(int size, int[] arr) {
        int left = 0;
        for(int right=size; right<arr.length; right++) {
            if(arr[right] != 2*arr[left]) {
                return false;
            }
            left++;
        }
        return true;
    }
}
