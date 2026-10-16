package OOP.JigsawPuzzle3;

/**
 * The immutable result returned by the solver: the discovered grid shape plus,
 * for every cell, which piece sits there and in which rotation.
 *
 * rotation is the number of 90-degree clockwise turns applied to the piece.
 */
public class Placement {
    private final int rows;
    private final int cols;
    private final int[][] pieceIds;
    private final int[][] rotations;

    public Placement(int rows, int cols, int[][] pieceIds, int[][] rotations) {
        this.rows = rows;
        this.cols = cols;
        this.pieceIds = pieceIds;
        this.rotations = rotations;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getPieceId(int row, int col) {
        return pieceIds[row][col];
    }

    public int getRotation(int row, int col) {
        return rotations[row][col];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("R=").append(rows).append(", C=").append(cols).append('\n');
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // "id@rot" — rot is the clockwise quarter-turns applied.
                sb.append(String.format("%4d@%d", pieceIds[r][c], rotations[r][c]));
                if (c < cols - 1) sb.append(" | ");
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
