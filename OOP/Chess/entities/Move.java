package OOP.Chess.entities;

import OOP.Chess.enums.PieceType;

/*
    Captures the full state of a move for history tracking and en passant detection.

    The Move is mostly immutable. The captured piece and promotion fields are set after construction because they depend
    on move execution logic that happens in the Game class;
 */
public class Move {
    private final Position source;
    private final Position destination;
    private final Piece piece;
    private Piece capturedPiece;
    private boolean isPromotion;
    private PieceType promotedTo;


    public Move(Position source, Position destination, Piece piece) {
        this.source = source;
        this.destination = destination;
        this.piece = piece;
    }

    public Position getSource() {
        return source;
    }

    public Position getDestination() {
        return destination;
    }

    public Piece getPiece() {
        return piece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(Piece piece) {
        this.capturedPiece = piece;
    }

    public boolean isPromotion() {
        return isPromotion;
    }

    public void setPromotion(boolean promotion, PieceType promotedTo) {
        this.isPromotion = promotion;
        this.promotedTo = promotedTo;
    }

    public PieceType getPromotedTo() {
        return promotedTo;
    }

    @Override
    public String toString() {
        String pieceStr = piece.getColor() + " " + piece.getPieceType();
        String moveStr = source + "->" + destination;
        String extra = "";
        if(capturedPiece != null) {
            extra += " captures " + capturedPiece.getPieceType();
        }

        if(isPromotion) {
            extra += " (promted to " + promotedTo + ")";
        }
        return pieceStr + " " + moveStr + extra;
    }
}
