package OOP.JigsawPuzzle3;

/**
 * A rectangular R x C grid that the solver fills during the search. Unlike a
 * square board, rows and columns are independent because the final shape is
 * discovered by trying each factorisation of N.
 */
public class Board {
    private final int rows;
    private final int cols;
    private final Piece[][] grid;

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

    public void placePiece(Position pos, Piece piece) {
        grid[pos.row()][pos.col()] = piece;
    }

    public void removePiece(Position pos) {
        grid[pos.row()][pos.col()] = null;
    }
}
