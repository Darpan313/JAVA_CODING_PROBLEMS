package OOP.JigsawPuzzle3;

/**
 * The black-box {@code match(edgeA, edgeB) -> bool} function from the problem
 * statement. The solver depends ONLY on this interface, so the real matching
 * rule (shape, colour, image continuity, ...) can be swapped without touching
 * the search algorithm.
 *
 * Contract: {@code match(a, b)} is true iff edge {@code a} can physically
 * connect to edge {@code b} when they are placed against each other.
 */
@FunctionalInterface
public interface EdgeMatcher {
    boolean match(Edge a, Edge b);
}
