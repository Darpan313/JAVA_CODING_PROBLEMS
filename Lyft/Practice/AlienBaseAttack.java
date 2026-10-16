package Lyft.Practice;

import java.util.TreeSet;

public class AlienBaseAttack {
    private final int n;
    private final TreeSet<Integer> destroyedSegments;
    private int remainingLength;

    public AlienBaseAttack(int n) {
        this.n = n;
        this.destroyedSegments = new TreeSet<>();
        this.remainingLength = n;
    }

    // Destroys [x,x+1]
    // Time-complexity: O(logK)
    public boolean attack(int x) {
        if(x<0 || x>=n) {
            return false;
        }

        if(destroyedSegments.add(x)) {
            remainingLength--;
            return true;
        }

        return false;
    }

    // Check if [x, x+1] is destroyed
    // Time-complexity: O(logK)
    public boolean isSegmentDestroyed(int x) {
        return destroyedSegments.contains(x);
    }

    // Returns total remaining length of base
    public int getRemainingLength() {
        return this.remainingLength;
    }

    // Returns total number of intact base pieces
    // Time-complexity: O(n)
    public int getIntactSegmentedCount() {
        if(destroyedSegments.isEmpty()) {
            return n>0 ? 1 : 0;
        }

        int prevEnd = 0;
        int count = 0;

        for(int x : destroyedSegments) {
            if(x > prevEnd) {
                count++;
            }
            prevEnd = x+1;
        }

        if(prevEnd < n) {
            count++;
        }
        return count;
    }

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
