package OOP.Chess.entities;

import OOP.Chess.enums.Color;
import OOP.Chess.enums.GameStatus;
import OOP.Chess.exceptions.ChessException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private final Board board;
    private final Player[] players;
    private Color currentTurn;
    private final List<Move> moveHistory;
    private GameStatus status;
    private Color winner;

    public Game(Player whitePlayer, Player blackPlayer) {
        this.board = new Board();
        this.players = new Player[]{whitePlayer, blackPlayer};
        this.moveHistory = new ArrayList<>();
        this.currentTurn = Color.WHITE;
        this.status = GameStatus.ACTIVE;
        this.winner = null;
    }

    public boolean makeMove(Position from, Position to) {
        // Check game is not over
        if(status == GameStatus.CHECKMATE || status == GameStatus.STALEMATE || status == GameStatus.RESIGNED) {
            throw new ChessException("Game is already over: " + status);
        }

        Piece piece = board.getPiece(from);

        if(piece == null) {
            throw new ChessException("No piece at " + from);
        }
        if(piece.getColor() != currentTurn) {
            throw new ChessException("Not your turn. Current turn: " + currentTurn);
        }

        if(!piece.canMove(board, from, to)) {
            throw new ChessException("Illegal move for " + piece.getPieceType() + " from " + from + " to " + to);
        }

        // Castling: the king may not be in check, and may not pass through an attacked square. (Landing in check is
        // rejected by king-safety test that every move goes through below. We test each square the king crosses by
        // stepping it there and reusing isInCheck
        return false;
    }

    public boolean isInCheck(Color color) {
        return false;
    }

    public boolean isCheckmate(Color color) {
        return false;
    }

    public boolean isStalemate(Color color) {
        return false;
    }

    public void resign(Color color) {
        return;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Color getWinner() {
        return winner;
    }

    public Board getBoard() {
        return board;
    }

    public Color getCurrentTurn() {
        return currentTurn;
    }

    public List<Move> getMoveHistory() {
        return Collections.unmodifiableList(moveHistory);
    }
}
