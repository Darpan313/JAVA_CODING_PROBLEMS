package OOP.Chess.entities;

import OOP.Chess.enums.Color;
import OOP.Chess.enums.PieceType;

public class Pawn extends Piece {
    public Pawn(Color color) {
        super(color, PieceType.PAWN);
    }

    @Override
    public boolean canMove(Board board, Position from, Position to) {
        // Forward one square
        // Forward two squares from starting position
        // Diagonal Capture (including en passant)
        return false;
    }
}
