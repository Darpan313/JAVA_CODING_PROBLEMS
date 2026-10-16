package OOP.JigsawPuzzle3;

/**
 * An immutable (row, col) coordinate on the {@link Board}. Grouping the two
 * values keeps the solver's traversal and neighbour arithmetic behind readable
 * names instead of naked index math.
 */
public record Position(int row, int col) {

    /** The cell immediately to the right (same row, next column). */
    public Position right() {
        return new Position(row, col + 1);
    }

    /** The first cell of the next row, used when wrapping in row-major order. */
    public Position nextRowStart() {
        return new Position(row + 1, 0);
    }

    /** The neighbour above (previous row, same column). */
    public Position up() {
        return new Position(row - 1, col);
    }

    /** The neighbour to the left (same row, previous column). */
    public Position left() {
        return new Position(row, col - 1);
    }
}
