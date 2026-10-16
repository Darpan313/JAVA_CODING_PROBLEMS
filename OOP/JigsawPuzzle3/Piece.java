package OOP.JigsawPuzzle3;

import java.util.EnumMap;
import java.util.Map;

/**
 * A puzzle piece with four edges. A piece tracks its current rotation so the
 * orientation that made it fit can be reported back in the final placement.
 *
 * Rotation is measured in 90-degree clockwise steps (0..3).
 */
public class Piece {
    private final int id;
    private final Map<Direction, Edge> edges = new EnumMap<>(Direction.class);
    private int rotation = 0;

    public Piece(int id, int up, int right, int down, int left) {
        this.id = id;
        edges.put(Direction.UP, new Edge(up));
        edges.put(Direction.RIGHT, new Edge(right));
        edges.put(Direction.DOWN, new Edge(down));
        edges.put(Direction.LEFT, new Edge(left));
    }

    public int getId() {
        return id;
    }

    public int getRotation() {
        return rotation;
    }

    public Edge getEdge(Direction direction) {
        return edges.get(direction);
    }

    /**
     * Rotate the piece 90 degrees clockwise. The edge that was on the LEFT
     * moves to the top, etc. Calling this four times restores the original
     * orientation, which is exactly what the solver relies on when it tries
     * all rotations and backtracks.
     */
    public void rotate() {
        Edge up = edges.get(Direction.UP);
        edges.put(Direction.UP, edges.get(Direction.LEFT));
        edges.put(Direction.LEFT, edges.get(Direction.DOWN));
        edges.put(Direction.DOWN, edges.get(Direction.RIGHT));
        edges.put(Direction.RIGHT, up);
        rotation = (rotation + 1) % 4;
    }

    public long flatCount() {
        return edges.values().stream().filter(Edge::isFlat).count();
    }

    public boolean isCorner() {
        return flatCount() == 2;
    }

    public boolean isEdge() {
        return flatCount() == 1;
    }

    @Override
    public String toString() {
        return "Piece#" + id;
    }
}
