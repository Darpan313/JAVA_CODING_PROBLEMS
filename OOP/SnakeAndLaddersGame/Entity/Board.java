package OOP.SnakeAndLaddersGame.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    int size;
    Map<Integer, Integer> snakesAndLadders;

    public Board(int size, List<BoardEntity> entities) {
        this.size = size;
        this.snakesAndLadders = new HashMap<>();

        for(BoardEntity entity : entities) {
            int start = entity.getStart();
            int end = entity.getEnd();

            if(start<1 || start>size || end<1 || end>size) {
                throw new IllegalArgumentException("Entry positions must be within 1 and "+ size+".");
            }
            if(snakesAndLadders.containsKey(start)) {
                throw new IllegalArgumentException("Two entities cannot share same start cell: "+start+".");
            }
            snakesAndLadders.put(start, end);
        }
    }

    public int getSize() {
        return this.size;
    }

    public int getFinalPosition(int position) {
        return snakesAndLadders.getOrDefault(position, position);
    }
}
