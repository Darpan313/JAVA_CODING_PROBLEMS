package Codeforces.problem12D;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
    /*
        5
        3 0 0 2 0
        7 10 7 4 0
        9 1 6 1 9
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int N = sc.nextInt();
        int[][] strength = new int[N][3];
        for(int i=0; i<N; i++) {
            strength[i][0] = sc.nextInt();
            strength[i][1] = sc.nextInt();
            strength[i][2] = sc.nextInt();
        }
        System.out.println(getWeakest(strength));
        sc.close();
    }

    private static int getWeakest(int[][] strength) {
        return 1;
    }


}
