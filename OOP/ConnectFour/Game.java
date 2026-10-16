package OOP.ConnectFour;

enum GameState {
    IN_PROGRESS,
    WIN,
    DRAW
}
public class Game {
    private final Board board;
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;
    private GameState currentState;
    private Player winner;

    public Game(Player player1, Player player2) {
        this.board = new Board();
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
        this.currentState = GameState.IN_PROGRESS;
        this.winner = null;
    }


}
