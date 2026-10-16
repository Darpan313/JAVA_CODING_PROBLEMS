package Lyft.Practice;

import java.util.TreeSet;

public class AlienBaseAttackSimulation {
    // Nested class to represent a continuous interval [start, end)
    private static class Interval implements Comparable<Interval> {
        int start;
        int end; // exclusive boundary

        Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        // Sort intervals strictly by their starting coordinate
        @Override
        public int compareTo(Interval fire) {
            return Integer.compare(this.start, fire.start);
        }
    }

    private final int n;
    private final TreeSet<Interval> intactIntervals;
    private int remainingLength;

    public AlienBaseAttackSimulation(int n) {
        this.n = n;
        this.intactIntervals = new TreeSet<>();
        this.remainingLength = n;

        // Initially, the entire base [0, n) is one large intact interval
        if (n > 0) {
            intactIntervals.add(new Interval(0, n));
        }
    }

    /**
     * Time Complexity: O(log K)
     */
    public boolean attack(int x) {
        if (x < 0 || x >= n) return false;

        // Find the interval that could potentially contain coordinate x
        // We look for the interval with the highest start point <= x
        Interval key = new Interval(x, x + 1);
        Interval match = intactIntervals.floor(key);

        // If no interval contains x, it means [x, x+1] was already destroyed
        if (match == null || match.end <= x) {
            return false;
        }

        // x is guaranteed to be inside 'match' now. Remove it to split/modify it.
        intactIntervals.remove(match);
        remainingLength--;

        // Case 1: Left piece remains intact
        if (x > match.start) {
            intactIntervals.add(new Interval(match.start, x));
        }

        // Case 2: Right piece remains intact
        if (x + 1 < match.end) {
            intactIntervals.add(new Interval(x + 1, match.end));
        }

        return true;
    }

    /**
     * Time Complexity: O(log K)
     */
    public boolean isSegmentDestroyed(int x) {
        if (x < 0 || x >= n) return true;

        Interval match = intactIntervals.floor(new Interval(x, x + 1));
        // If an intact interval covers x, then it is NOT destroyed
        return match == null || match.end <= x;
    }

    /**
     * Time Complexity: O(1)
     */
    public int getRemainingLength() {
        return this.remainingLength;
    }

    /**
     * Time Complexity: O(1) - Instant lookup!
     */
    public int getIntactSegmentsCount() {
        return intactIntervals.size();
    }
}

