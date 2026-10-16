package OOP.Chess.entities;

import OOP.Chess.exceptions.ChessException;

/*
    It is a value object representing a board co-ordinate. The constructor validates bounds, and equals/hashcode are
    implemented so positions can be compared and used in collections.
 */
public class Position {
    private final int row;
    private final int col;

    public Position(int row, int col) {
        if(row<0 || col<0 || row>7 || col>7) {
            throw new ChessException("Invalid position: (" + row +", "+ col);
        }
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object other) {
        if(this == other) return true;
        if(!(other instanceof Position)) return false;
        Position p = (Position) other;
        return row == p.row && col == p.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    @Override
    public String toString() {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }
}
