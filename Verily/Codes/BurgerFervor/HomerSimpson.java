package Verily.Codes.BurgerFervor;

import java.util.PriorityQueue;


/*
    Homer Simpson, a very smart guy, likes eating Krusty-burgers. It takes
    Homer m minutes to eat a Krusty-burger. However, there’s a new type
    of burger in Apu’s Kwik-e-Mart. Homer likes those too. It takes him n
    minutes to eat one of these burgers. Given t minutes, you have to find out
    the maximum number of burgers Homer can eat without wasting any time.
    If he must waste time, he can have beer.
    Input
    Input consists of several test cases. Each test case consists of three integers
    m, n, t (0 < m, n, t < 10000). Input is terminated by EOF.
    Output
    For each test case, print in a single line the maximum number of burgers
    Homer can eat without having beer. If homer must have beer, then also
    print the time he gets for drinking, separated by a single space. It is preferable that Homer drinks as
    little beer as possible.
    Sample Input
    3 5 54
    3 5 55
    Sample Output
    18
    17
 */
public class HomerSimpson {
    public static void main(String[] args) {
        int m = 4;
        int n = 9;
        int t = 15;
        int[] res = solve(m,n,t);
        System.out.println("["+res[0] + "," + res[1]+ "]");

        m = 3;
        n = 5;
        t = 54;
        res = solve(m,n,t);
        System.out.println("["+res[0] + "," + res[1]+ "]");

        m = 3;
        n = 5;
        t = 55;
        res = solve(m,n,t);
        System.out.println("["+res[0] + "," + res[1]+ "]");

        m = 4;
        n = 9;
        t = 54;
        res = solve(m,n,t);
        System.out.println("["+res[0] + "," + res[1]+ "]");

        m = 4;
        n = 9;
        t = 22;
        res = solve(m,n,t);
        System.out.println("["+res[0] + "," + res[1]+ "]");
    }

    // O(t/m log(t/m))
    static int[] solve(int m, int n, int t) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((a,b) -> a[1] == b[1] ?
                Integer.compare(b[0], a[0]) : Integer.compare(a[1], b[1]));

        int mcount = t/m;
        for(int i=mcount; i>=0; i--) {
            int timeLeft = (t - i*m);
            int ncount = timeLeft/n;
            int wasted = t - (i*m + ncount*n);
            pq.offer(new int[]{i+ncount, wasted});
        }

        return pq.poll();
    }

//    // O(t/m)
//    static int[] solveOptimized(int m, int n, int t) {
//        int maxBurgers = 0;
//
//    }
//    // O(t)
//    static int[] solveUsingDP(int m, int n, int t) {
//
//    }
}