package OOP.JigsawPuzzle3;

/**
 * A single edge of a piece. The value is modelled as an integer "code" so that
 * a real puzzle (which has many distinct shapes/colours) can be represented,
 * but the solver never inspects the code directly — it only ever asks an
 * {@link EdgeMatcher} whether two edges connect.
 *
 * Convention used by {@link ComplementaryEdgeMatcher}:
 *   - code 0 is a FLAT border edge that connects to nothing.
 *   - a tab with code k connects to the blank with code -k.
 */
public class Edge {
    public static final int FLAT = 0;

    private final int code;

    public Edge(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public boolean isFlat() {
        return code == FLAT;
    }

    @Override
    public String toString() {
        return Integer.toString(code);
    }
}
