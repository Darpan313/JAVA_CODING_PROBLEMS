package OOP.SnakeAndLaddersGame.Entity;

public class Ladder extends BoardEntity {
    public Ladder(int start, int end) {
        super(start, end);
        if(end<=start) {
            throw new IllegalArgumentException("Ladders bottom should be lower position than its top");
        }
    }
}
