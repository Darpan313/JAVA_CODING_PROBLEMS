package OOP.JigSawPuzzle;

public class Edge {
    private static final int FLAT = 0;

    int code;

    public Edge(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public boolean isFlat() {
        return FLAT == code;
    }
}
