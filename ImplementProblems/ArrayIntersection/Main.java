package ImplementProblems.ArrayIntersection;

import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        int[] arr1 = {1,2,2,1};
        int[] arr2 = {2,2};

        int[] res = find(arr1, arr2);
        for(int i=0; i<res.length; i++) {
            System.out.print(res[i] + ", ");
        }
        System.out.println();
    }

    private static int[] find(int[] arr1, int[] arr2) {
        int[] map = new int[1001];
        Set<Integer> res = new HashSet<>();
        for(int n : arr1) {
            map[n] = 1;
        }

        for(int n : arr2) {
            if(map[n] == 1) {
                res.add(n);
            }
        }

        return res.stream().mapToInt(i->i).toArray();
    }
}
