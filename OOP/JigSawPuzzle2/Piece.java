package OOP.JigSawPuzzle2;

import java.util.HashMap;
import java.util.Map;

public class Piece {
    private final int id;
    private final Map<Direction, Edge> edges = new HashMap<>();

    public Piece(int id, EdgeType left, EdgeType right, EdgeType up, EdgeType down) {
        this.id = id;
        edges.put(Direction.LEFT, new Edge(left, this));
        edges.put(Direction.RIGHT, new Edge(right, this));
        edges.put(Direction.UP, new Edge(up, this));
        edges.put(Direction.DOWN, new Edge(down, this));
    }

    public int getId() {
        return this.id;
    }

    public Edge getEdge(Direction direction) {
        return edges.get(direction);
    }

    public void rotate() {
        Edge up = edges.get(Direction.UP);
        edges.put(Direction.UP, edges.get(Direction.LEFT));
        edges.put(Direction.LEFT, edges.get(Direction.DOWN));
        edges.put(Direction.DOWN, edges.get(Direction.RIGHT));
        edges.put(Direction.RIGHT, up);
    }

    public boolean isCorner() {
        long flatCount = edges.values().stream().filter(e -> e.getType() == EdgeType.FLAT).count();
        return flatCount == 2;
    }

    public boolean isEdge() {
        long flatCount = edges.values().stream().filter(e -> e.getType() == EdgeType.FLAT).count();
        return flatCount == 1;
    }
}
