package Amazon.TicTacToe.entities;

import Amazon.TicTacToe.enums.Symbol;
import Amazon.TicTacToe.exceptions.InvalidMoveException;

public class Board {
    private final Cell[][] board;
    private final int size;

    public Board(int size) {
        this.size = size;
        this.board = new Cell[size][size];
        initializeBoard();
    }

    private void initializeBoard() {
        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++) {
                board[i][j] = new Cell();
            }
        }
    }

    public int getSize() {
        return this.size;
    }

    public Cell getCell(int row, int col) {
        validatePosition(row, col);
        return board[row][col];
    }

    public void placeSymbol(int row, int col, Symbol symbol) {
        validatePosition(row, col);
        board[row][col].setSymbol(symbol);
    }

    public boolean isCellEmpty(int row, int col) {
        validatePosition(row, col);
        return board[row][col].getSymbol() == Symbol.EMPTY;
    }

    public boolean isFull() {
        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++) {
                if(board[i][j].isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void printBoard() {
        System.out.println();
        for (int i=0; i<size; i++) {
            for(int j=0; j<size; j++) {
                System.out.print(" " + board[i][j].getSymbol().getDisplayChar() + " ");
                if(j<size-1) System.out.print("|");
            }
            System.out.println();
            if(i<size-1) {
                System.out.println("-".repeat(size*4-1));
            }
        }
        System.out.println();
    }

    private void validatePosition(int row, int col) {
        if(row<0 || row>=size || col<0 || col>=size) {
            throw new InvalidMoveException("Position (" + row +", " + col + ") is out of bounds");
        }
    }
}
