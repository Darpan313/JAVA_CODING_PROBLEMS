package OOP.JigsawPuzzle3;

/**
 * The four edges of a piece, in clockwise order starting from the top.
 * Clockwise ordering is what makes {@link Piece#rotate()} a simple shift.
 */
public enum Direction {
    UP, RIGHT, DOWN, LEFT;

    public Direction getOpposite() {
        return values()[(this.ordinal() + 2) % 4];
    }
}
