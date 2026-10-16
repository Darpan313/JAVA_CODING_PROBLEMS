package OOP.JigSawPuzzle;

public class Board {
    private final int rows;
    private final int cols;
    private Piece[][] grid;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Piece[rows][cols];
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Piece getPiece(Position pos) {
        return grid[pos.row()][pos.col()];
    }

    public void placePiece(Position position, Piece piece) {
        grid[position.row()][position.col()] = piece;
    }

    public void removePiece(Position position) {
        grid[position.row()][position.col()] = null;
    }
}
