package OOP.JigSawPuzzle;

import java.util.List;
import java.util.Optional;

public class JigSawSolver {

    private List<Piece> pieces;
    private EdgeMatcher matcher;

    public JigSawSolver(List<Piece> pieces, EdgeMatcher matcher) {
        this.pieces = pieces;
        this.matcher = matcher;
    }

    public Optional<Placement> solve() {
        int n = pieces.size();
        if(n==0) return Optional.empty();

        for(int rows = 1; rows<n; rows++) {
            if(n%rows != 0) continue;

            int cols = n/rows;

            Board board = new Board(rows, cols);
            boolean[] used = new boolean[n];
            if(fill(board, new Position(0,0), used)) {
                return Optional.of(snaphsot(board));
            }
        }
        return Optional.empty();
    }

    /** Backtracking fill of the board in row-major order. */
    private boolean fill(Board board, Position pos, boolean[] used) {
        if(pos.row() == board.getRows()) {
            return true;
        }

        Position nextPos = (pos.col() == board.getCols()-1) ? pos.nextRowStart() : pos.right();

        for(int i=0; i<pieces.size(); i++) {
            if(used[i]) continue;
            Piece piece = pieces.get(i);

            for(int r=0; r<4; r++) {
                if(fits(board, piece, pos)) {
                    board.placePiece(pos, piece);
                    used[i] = true;

                    if(fill(board, nextPos, used)) {
                        return true;
                    }
                    used[i] = false;
                    board.removePiece(pos);
                }
                piece.rotate();
            }
        }
        return false;
    }

    private boolean fits(Board board, Piece piece, Position position) {
        if(position.row()>0) {
            Piece above = board.getPiece(position.up());
            if(!matcher.match(above.getEdge(Direction.DOWN), piece.getEdge(Direction.UP))) {
                return false;
            }
        }
        if(position.col()>0) {
            Piece left = board.getPiece(position.left());
            if(!matcher.match(left.getEdge(Direction.RIGHT), piece.getEdge(Direction.LEFT))) {
                return false;
            }
        }
        return true;
    }

    private Placement snaphsot(Board board) {
        int rows = board.getRows();
        int cols = board.getCols();

        int[][] ids = new int[rows][cols];
        int[][] rotations = new int[rows][cols];

        for(int r=0; r<rows; r++) {
            for(int c=0; c<cols; c++) {
                Piece p = board.getPiece(new Position(r,c));
                ids[r][c] = p.getId();
                rotations[r][c] = p.getRotation();
            }
        }
        return new Placement(rows, cols, ids, rotations);
    }
}
