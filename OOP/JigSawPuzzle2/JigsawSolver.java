package OOP.JigSawPuzzle2;

import java.util.ArrayList;
import java.util.List;

public class JigsawSolver {
    private final Board board;
    private final List<Piece> remainingPieces;

    public JigsawSolver(Board board, List<Piece> pieces) {
        this.board = board;
        this.remainingPieces = new ArrayList<>(pieces);
    }

    public boolean solve(int row, int col) {
        if(row == board.getSize()) {
            return true;
        }

        int nextRow = (col == board.getSize() - 1) ? row + 1 : row;
        int nextCol = (col == board.getSize() - 1) ? 0 : col + 1;

        for(int i=0; i<remainingPieces.size(); i++) {
            Piece piece = remainingPieces.get(i);
            for(int r=0; r<4; r++) {
                if(isValidPlacement(piece, row, col)) {
                    board.placePiece(row, col, piece);
                    remainingPieces.remove(piece);

                    if(solve(nextRow, nextCol)) {
                        return true;
                    }

                    remainingPieces.add(piece);
                    board.removePiece(row, col);
                }
                piece.rotate();
            }
        }
        return false;
    }

    private boolean isValidPlacement(Piece piece, int row, int col) {
        if(row == 0 && piece.getEdge(Direction.UP).getType() != EdgeType.FLAT) return false;
        if(col == 0 && piece.getEdge(Direction.LEFT).getType() != EdgeType.FLAT) return false;
        if(row == board.getSize() && piece.getEdge(Direction.DOWN).getType() != EdgeType.FLAT) return false;
        if(col == board.getSize() && piece.getEdge(Direction.RIGHT).getType() != EdgeType.FLAT) return false;

        // check fit with top neighbor
        if(row > 0) {
            Piece topNeighbor = board.getPiece(row-1, col);
            if(topNeighbor != null && !topNeighbor.getEdge(Direction.DOWN).fitsWith(piece.getEdge(Direction.UP))) {
                return false;
            }
        }
        // check fit with left neighbor
        if(col > 0) {
            Piece leftNeighbor = board.getPiece(row, col - 1);
            if(leftNeighbor != null && !leftNeighbor.getEdge(Direction.RIGHT).fitsWith(piece.getEdge(Direction.LEFT))) {
                return false;
            }
        }
        return true;
    }
}
