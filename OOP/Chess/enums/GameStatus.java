package OOP.Chess.enums;

public enum GameStatus {
    ACTIVE,         // Normal Play
    CHECK,          // Current player's king is under attack
    STALEMATE,      // Current player has no legal moves but isn't in check
    CHECKMATE,      // Current player has no legal moves to escape check
    RESIGNED        // A player voluntarily surrendered.
}
