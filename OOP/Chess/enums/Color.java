package OOP.Chess.enums;

/*
    opposite() method makes turn switching clean
 */
public enum Color {
    BLACK,
    WHITE;

    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}
