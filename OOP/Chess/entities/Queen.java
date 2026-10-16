package OOP.Chess.entities;

import OOP.Chess.enums.Color;
import OOP.Chess.enums.PieceType;

public class Queen extends Piece {
    public Queen(Color color) {
        super(color, PieceType.QUEEN);
    }

    @Override
    public boolean canMove(Board board, Position from, Position to) {
        int rowDiff = to.getRow() - from.getRow();
        int colDiff = to.getCol() - from.getCol();

        boolean isStraight = rowDiff == 0 || colDiff == 0;
        boolean isDiagonal = Math.abs(rowDiff) == Math.abs(colDiff);

        if(!isStraight || !isDiagonal) {
            return false;
        }

        if(!isPathClear(board, from, to)) {
            return false;
        }

        Piece target = board.getPiece(to);
        return target == null || target.getColor() != this.color;
    }

    private boolean isPathClear(Board board, Position from, Position to) {
        int rowDir = Integer.signum(to.getRow() - from.getRow());
        int colDir = Integer.signum(to.getCol() - from.getCol());

        int currRow = from.getRow() + rowDir;
        int currCol = from.getCol() + colDir;

        while (currRow != to.getRow() || currCol != to.getCol()) {
            if(board.getPiece(new Position(currRow, currCol)) != null) {
                return false;
            }
            currRow += rowDir;
            currCol += colDir;
        }
        return true;
    }
}
