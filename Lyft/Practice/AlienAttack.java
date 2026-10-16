package Lyft.Practice;

import java.util.Comparator;
import java.util.TreeSet;

public class AlienAttack {
    private final int n;
    private final TreeSet<Interval> intactIntervals;
    private int remainingLength;

    public AlienAttack(int n) {
        this.n = n;
        this.remainingLength = n;
        intactIntervals = new TreeSet<>(Comparator.comparingInt(a -> a.start));

        // if n>0 the entire base [0,n) is one large intact interval.
        if(n>0) {
            intactIntervals.add(new Interval(0, n));
        }
    }

    // Time-complexity: O(logK)
    public boolean attack(int x) {
        if(x<0 || x>=n) {
            return false;
        }

        Interval matchedInterval = intactIntervals.floor(new Interval(x,x+1));
        if(matchedInterval == null || matchedInterval.end <= x) {
            return false;
        }

        intactIntervals.remove(matchedInterval);
        remainingLength--;

        if(x>matchedInterval.start) {
            intactIntervals.add(new Interval(matchedInterval.start, x));
        }
        if(x+1<matchedInterval.end) {
            intactIntervals.add(new Interval(x+1, matchedInterval.end));
        }
        return true;
    }

    // Time-Complexity: O(logK)
    public boolean isSegmentDestroyed(int x) {
        if(x<0 || x>=n) return true;

        Interval matchedInterval = intactIntervals.floor(new Interval(x, x+1));

        return (matchedInterval == null || matchedInterval.end <= x);
    }

    // Time-Complexity: O(1)
    public int getRemainingLength() {
        return this.remainingLength;
    }

    // Time-Complexity: O(1)
    public int getIntactSegmentedCount() {
        return intactIntervals.size();
    }

    public record Interval(int start, int end) {};

    public static void main(String[] args) {
        AlienBaseAttack alienBaseAttack = new AlienBaseAttack(5);
        System.out.println(alienBaseAttack.attack(2));
        System.out.println(alienBaseAttack.getIntactSegmentedCount());
        System.out.println(alienBaseAttack.attack(0));
        System.out.println(alienBaseAttack.getIntactSegmentedCount());
        System.out.println(alienBaseAttack.attack(4));
        System.out.println(alienBaseAttack.getIntactSegmentedCount());
    }
}
