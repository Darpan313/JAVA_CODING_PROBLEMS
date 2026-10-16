package OOP.JigSawPuzzle2;

public class Board {
    private final int size;
    private final Piece[][] grid;

    public Board(int size) {
        this.size = size;
        this.grid = new Piece[size][size];
    }

    public int getSize() {
        return size;
    }

    public Piece getPiece(int row, int column) {
        return grid[row][column];
    }

    public void placePiece(int row, int column, Piece piece) {
        grid[row][column] = piece;
    }

    public void removePiece(int row, int column) {
        grid[row][column] = null;
    }
}
