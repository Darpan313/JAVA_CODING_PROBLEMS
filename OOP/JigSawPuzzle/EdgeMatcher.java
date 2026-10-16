package OOP.JigSawPuzzle;

@FunctionalInterface
public interface EdgeMatcher {
    boolean match(Edge a, Edge b);
}
