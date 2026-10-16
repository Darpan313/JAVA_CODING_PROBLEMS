package OOP.JigSawPuzzle2;

public enum Direction {
    UP, RIGHT, DOWN, LEFT;

    public Direction getOpposite() {
        return values()[(this.ordinal() + 2)%4];
    }
}
