package Amazon.DroneRoute;

public class Main {
    public static void main(String[] args) {
        int[] corridor = {1, 2, 3, 4};
        int[] route = {0, 2, 3};
        System.out.println(totalDistance(corridor, route));

        corridor = new int[]{1, 2, 3, 4};
        route = new int[]{1, 3};
        System.out.println(totalDistance(corridor, route));

        corridor = new int[]{4, 4, 4};
        route = new int[]{0, 1, 1, 0};
        System.out.println(totalDistance(corridor, route));
    }

    private static long totalDistance(int[] corridor, int[] route) {
        int n = corridor.length+1;
        int[] prefixSum = new int[n];

        for(int i=1; i<n; i++) {
            prefixSum[i] = prefixSum[i-1] + corridor[i-1];
        }

        int total = prefixSum[n-1];
        int dist = 0;

        for(int i=1; i<route.length; i++) {
            int start = route[i-1];
            int end = route[i];
            int directDistance = Math.abs(prefixSum[start] - prefixSum[end]);
            dist += Math.min(directDistance, Math.abs(total - directDistance));
        }
        return dist;
    }
}
