package Amazon.TicTacToe;

import Amazon.TicTacToe.entities.Game;
import Amazon.TicTacToe.entities.Player;
import Amazon.TicTacToe.enums.Symbol;

public class TicTacToe {
    public static void main(String[] args) {
        Player alice = new Player("alice", Symbol.X);
        Player bob = new Player("bob", Symbol.O);

        Game game = new Game(alice, bob, 3);

        System.out.println("=========TIC-TAC-TOE=========");

        game.makeMove(0,0);
        game.makeMove(1,0);
        game.makeMove(0,1);
        game.makeMove(1,1);
        game.makeMove(0,2);

        game.printBoard();

        System.out.println("Result: " + game.getStatus() );
        Player winner = game.getWinner();
        if(winner != null) {
            System.out.println("Winner: " + winner.getName());
        }
    }
}
