package Auvik.TopTalkers;

import java.util.List;
import java.util.Map;

/*
    Problem 2 · Traffic analysis
    Top talkers
    Timer: 30 minutes
    Collectors send NetFlow records: source IP, destination IP, bytes, timestamp. The dashboard asks every few seconds:
    which K source IPs sent the most bytes in the last 5 minutes?

    Build add(FlowRecord) and top(int k).

    Ask before you code
    Does the window need to be exact, or is minute-level granularity fine? (Granularity makes it much cheaper.)
    Can records arrive late or out of order? How late?
    Roughly how many distinct source IPs, and how many records per second?
    Is the flow data sampled (1 in N packets)?
*/
public class Main {
    public static void main(String[] args) {
        TopTalkers topTalkers = new TopTalkers();
        long now = System.currentTimeMillis();
        long ONE_MINUTE = 60_000L;

        System.out.println("Ingesting NetFlow records...");

        // 1. Current traffic for IP_A
        topTalkers.add(new TopTalkers.FlowRecord("192.168.1.10", "10.0.0.1", 2000, now));

        // 2. Current traffic for IP_B
        topTalkers.add(new TopTalkers.FlowRecord("192.168.1.20", "10.0.0.2", 5000, now));

        // 3. Out-of-order traffic for IP_A (arriving late, timestamped 2 minutes ago)
        // IP_A total should become 2000 + 7000 = 9000
        topTalkers.add(new TopTalkers.FlowRecord("192.168.1.10", "10.0.0.3", 7000, now - (2 * ONE_MINUTE)));

        // 4. Traffic for IP_C (timestamped 4 minutes ago, still barely in the 5-min window)
        topTalkers.add(new TopTalkers.FlowRecord("192.168.1.30", "10.0.0.4", 4000, now - (4 * ONE_MINUTE)));

        // 5. Expired traffic for IP_D (timestamped 6 minutes ago)
        // This is strictly older than the 5-minute window and should be dropped entirely.
        topTalkers.add(new TopTalkers.FlowRecord("192.168.1.99", "10.0.0.5", 999999, now - (6 * ONE_MINUTE)));

        System.out.println("Retrieving Top 3 Talkers...");
        List<Map.Entry<String, Long>> top3 = topTalkers.topK(3);

        int rank = 1;
        for (Map.Entry<String, Long> entry : top3) {
            System.out.printf("%d. IP: %-15s | Total Bytes: %d%n", rank, entry.getKey(), entry.getValue());
            rank++;
        }

        // Clean up background threads if you used the ScheduledExecutorService implementation
        // topTalkers.shutdown();
    }
}
