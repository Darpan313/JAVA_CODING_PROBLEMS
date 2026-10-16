package OOP.Chess.entities;

import OOP.Chess.enums.Color;
import OOP.Chess.enums.PieceType;

public class King extends Piece {

    public King(Color color) {
        super(color, PieceType.KING);
    }

    @Override
    public boolean canMove(Board board, Position from, Position to) {
        int rowDiff = Math.abs(to.getRow() - from.getRow());
        int colDiff = Math.abs(to.getCol() - from.getCol());

        // Normal King move: one square in any direction
        if (rowDiff <= 1 && colDiff <= 1 && (rowDiff + colDiff > 0)) {
            Piece target = board.getPiece(to);
            return target == null || target.getColor() != this.getColor();
        }

        // Castling: king moves two squares horizontally
        if (rowDiff == 0 && colDiff == 2 && !hasMoved) {
            return canCastle(board, from, to);
        }
        return false;
    }

    /*
        Castling has 3 conditions:
            - neither king nor rook has moved,
            - all squares between them are empty
            - the king doesn't pass through or land in check. (check detection is handled by the Game class because
              it requires knowledge of all pieces on the board, which is Game's responsibility
    */
    private boolean canCastle(Board board, Position from, Position to) {
        int row = from.getRow();
        int direction = to.getCol()>from.getCol() ? 1 : -1;
        int rookCol = direction == 1 ? 7 : 0;

        Piece rook = board.getPiece(new Position(row, rookCol));
        if(rook == null || rook.getPieceType() != PieceType.ROOK || rook.hasMoved) {
            return false;
        }

        // Check all squares between king and rook is empty
        int startCol = Math.min(from.getCol(), rookCol) + 1;
        int endCol = Math.max(from.getCol(), rookCol);
        for(int col = startCol; col<endCol; col++) {
            if(board.getPiece(new Position(row, col)) != null) {
                return false;
            }
        }

        // Note: if king doesn't pass through or land in check is handled by Game class during move validation.
        return true;
    }
}
