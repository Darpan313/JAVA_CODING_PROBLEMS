package Amazon.TicTacToe.entities;

import Amazon.TicTacToe.enums.GameStatus;
import Amazon.TicTacToe.enums.Symbol;
import Amazon.TicTacToe.exceptions.InvalidMoveException;

public class Game {
    private final Board board;
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;
    private GameStatus currentStatus;

    public Game(Player player1, Player player2, int boardSize) {
        this.board = new Board(boardSize);
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
        this.currentStatus = GameStatus.IN_PROGRESS;
    }

    public synchronized void makeMove(int row, int col) {
        if(currentStatus != GameStatus.IN_PROGRESS) {
            throw new InvalidMoveException("Game is already over!");
        }

        if(!board.isCellEmpty(row, col)) {
            throw new InvalidMoveException("Cell (" + row + ", "+ col + ") is already occupied");
        }

        board.placeSymbol(row, col, currentPlayer.getSymbol());

        if(checkWin(row, col, currentPlayer.getSymbol())) {
            currentStatus = (currentPlayer.getSymbol() == Symbol.X) ? GameStatus.WINNER_X : GameStatus.WINNER_O;
            return;
        }

        if(board.isFull()) {
            currentStatus = GameStatus.DRAW;
            return;
        }
        if(currentPlayer.equals(player1)) {
            currentPlayer = player2;
        }
        else {
            currentPlayer = player1;
        }
    }

    private boolean checkWin(int row, int col, Symbol symbol) {
        int size = board.getSize();
        boolean win = true;
        // check curr row
        for(int c=0; c<size; c++) {
            if(board.getCell(row, c).getSymbol() != symbol) {
                win = false;
                break;
            }
        }
        if(win) return true;

        // check curr col
        for(int r=0; r<size; r++) {
            if(board.getCell(r, col).getSymbol() != symbol) {
                win = false;
                break;
            }
        }
        if(win) return true;

        // check diagonal
        if(row == col) {
            for(int i=0; i<size; i++) {
                if(board.getCell(i,i).getSymbol() != symbol) {
                    win = false;
                    break;
                }
            }
            if(win) return true;
        }

        // check anti diagonal
        if(row+col == size-1) {
            win = true;
            for(int i=0; i<size; i++) {
                if(board.getCell(i, size-i-1).getSymbol() != symbol) {
                    win = false;
                    break;
                }
            }
            if(win) return true;
        }
        return false;
    }

    public GameStatus getStatus() {
        return currentStatus;
    }

    public Player getWinner() {
        if(currentStatus == GameStatus.WINNER_X) {
            return player1.getSymbol() == Symbol.X ? player1 : player2;
        }
        else if(currentStatus == GameStatus.WINNER_O) {
            return player2.getSymbol() == Symbol.O ? player2 : player1;
        }
        return null;
    }

    public void printBoard() {
        board.printBoard();
    }

}
