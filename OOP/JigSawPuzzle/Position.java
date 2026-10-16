package OOP.JigSawPuzzle;

public record Position(int row, int col) {
    public Position right() {
        return new Position(row, col + 1);
    }
    public Position left() {
        return new Position(row, col - 1);
    }
    public Position up() {
        return new Position(row-1, col);
    }
    public Position down() {
        return new Position(row+1, col);
    }

    public Position nextRowStart() {
        return new Position(row+1, 0);
    }
}
