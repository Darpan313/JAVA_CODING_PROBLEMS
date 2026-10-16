package OOP.JigsawPuzzle3;

/**
 * A sample black-box matcher.
 *
 * Rules:
 *  - A FLAT edge (code 0) is a border edge and connects to nothing, so any
 *    pair involving a FLAT never matches.
 *  - Otherwise a tab with code k connects only to the blank with code -k.
 *
 * A useful consequence: because FLAT matches nothing, the solver needs no
 * special "flat edges must sit on the border" rule. A flat edge can only ever
 * survive where it has no neighbour — i.e. on the grid boundary — so the
 * border constraint emerges for free from the adjacency check.
 */
public class ComplementaryEdgeMatcher implements EdgeMatcher {
    @Override
    public boolean match(Edge a, Edge b) {
        if (a.isFlat() || b.isFlat()) {
            return false;
        }
        return a.getCode() == -b.getCode();
    }
}
