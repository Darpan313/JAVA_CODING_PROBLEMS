package OOP.JigSawPuzzle;

public class ComplementaryEdgeMatcher implements EdgeMatcher {
    @Override
    public boolean match(Edge a, Edge b) {
        if(a.isFlat() || b.isFlat()) {
            return false;
        }
        return a.getCode() == -b.getCode();
    }
}
