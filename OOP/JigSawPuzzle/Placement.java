package OOP.JigSawPuzzle;

/*
    The immutable result returned by the solver:
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("R=").append(rows).append(", C=").append(cols).append("\n");
        for(int r=0; r<rows; r++) {
            for(int c=0; c<cols; c++) {
                sb.append(String.format("%4d@%d", pieceIds[r][c], rotations[r][c]));
                if(c<cols-1) sb.append("|");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
