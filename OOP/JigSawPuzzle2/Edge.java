package OOP.JigSawPuzzle2;

public class Edge {
    private final EdgeType type;
    private final Piece parentPiece;

    public Edge(EdgeType type, Piece parentPiece) {
        this.type = type;
        this.parentPiece = parentPiece;
    }

    public EdgeType getType() {
        return type;
    }

    public Piece getParentPiece() {
        return parentPiece;
    }

    public boolean fitsWith(Edge other) {
        if(this.type == EdgeType.FLAT || other.type == EdgeType.FLAT) {
            return false;
        }
        return this.type != other.type;
    }
}
