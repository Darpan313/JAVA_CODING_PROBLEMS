package OOP.JigSawPuzzle;

import java.util.Map;

public class Piece {
    private int id;
    private Map<Direction, Edge> edges;
    private int rotation;

    public Piece(int id, int left, int right, int up, int down) {
        this.id = id;
        this.rotation = 0;
        edges.put(Direction.LEFT, new Edge(left));
        edges.put(Direction.RIGHT, new Edge(right));
        edges.put(Direction.UP, new Edge(up));
        edges.put(Direction.DOWN, new Edge(down));
    }

    public Edge getEdge(Direction direction) {
        return edges.get(direction);
    }

    public int getId() {
        return id;
    }

    public int getRotation() {
        return rotation;
    }

    public  void rotate() {
        Edge up = edges.get(Direction.UP);
        edges.put(Direction.UP, edges.get(Direction.LEFT));
        edges.put(Direction.LEFT, edges.get(Direction.DOWN));
        edges.put(Direction.DOWN, edges.get(Direction.RIGHT));
        edges.put(Direction.RIGHT, up);
        rotation = (rotation+1)%4;
    }
}
