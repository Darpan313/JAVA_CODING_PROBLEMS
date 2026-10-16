package OOP.Chess.entities;

import OOP.Chess.enums.Color;
import OOP.Chess.enums.PieceType;
import OOP.Chess.exceptions.ChessException;

public class Board {
    private final Piece[][] grid;
    public Board() {
        grid = new Piece[8][8];
        initialize();
    }

    private void initialize() {
        // Black pieces
        grid[0][0] = new Rook(Color.BLACK);
        grid[0][1] = new Knight(Color.BLACK);
        grid[0][2] = new Bishop(Color.BLACK);
        grid[0][3] = new Queen(Color.BLACK);
        grid[0][4] = new King(Color.BLACK);
        grid[0][5] = new Bishop(Color.BLACK);
        grid[0][6] = new Knight(Color.BLACK);
        grid[0][7] = new Rook(Color.BLACK);

        for(int col=0; col<8; col++) {
            grid[1][col] = new Pawn(Color.BLACK);
        }

        for(int col=0; col<8; col++) {
            grid[6][col] = new Pawn(Color.WHITE);
        }

        // White Pieces
        grid[7][0] = new Rook(Color.WHITE);
        grid[7][1] = new Knight(Color.WHITE);
        grid[7][2] = new Bishop(Color.WHITE);
        grid[7][3] = new Queen(Color.WHITE);
        grid[7][4] = new King(Color.WHITE);
        grid[7][5] = new Bishop(Color.WHITE);
        grid[7][6] = new Knight(Color.WHITE);
        grid[7][7] = new Rook(Color.WHITE);
    }

    public Piece getPiece(Position position) {
        return grid[position.getRow()][position.getCol()];
    }

    public void setPiece(Position position, Piece piece) {
        grid[position.getRow()][position.getCol()] = piece;
    }

    public void movePiece(Position from, Position to) {
        grid[to.getRow()][to.getCol()] = grid[from.getRow()][from.getCol()];
        grid[from.getRow()][from.getCol()] = null;
    }

    public boolean isValidPosition(int row, int col) {
        return row>=0 && row<8 && col>=0 && col<8;
    }

    public Position findKing(Color color) {
        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 8; col++) {
                Piece piece = grid[row][col];
                if(piece != null && piece.getPieceType() == PieceType.KING
                && piece.getColor() == color) {
                    return new Position(row, col);
                }
            }
        }
        throw new ChessException("King not found for " + color);
    }
}
