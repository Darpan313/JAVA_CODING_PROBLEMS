package OOP.Chess.entities;

import OOP.Chess.enums.Color;
import OOP.Chess.enums.PieceType;

public class Knight extends Piece {
    public Knight(Color color) {
        super(color, PieceType.KNIGHT);
    }

    @Override
    public boolean canMove(Board board, Position from, Position to) {
        int rowDiff = Math.abs(to.getRow() - from.getRow());
        int colDiff = Math.abs(to.getCol() - from.getCol());

        // L-Shape: (2,1) or (1,2)
        boolean isLShape = (rowDiff == 2 && colDiff ==1)
                || (colDiff == 2 && rowDiff == 1);

        if(!isLShape) {
            return false;
        }

        Piece target = board.getPiece(to);
        return target == null || target.getColor() != this.color;
    }
}
