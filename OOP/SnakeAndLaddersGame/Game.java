package OOP.SnakeAndLaddersGame;

import OOP.SnakeAndLaddersGame.Entity.Board;
import OOP.SnakeAndLaddersGame.Entity.BoardEntity;
import OOP.SnakeAndLaddersGame.Entity.Dice;
import OOP.SnakeAndLaddersGame.Entity.Player;
import OOP.SnakeAndLaddersGame.Enums.GameStatus;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Game {
    private final Board board;
    private final Queue<Player> players;
    private final Dice dice;
    private GameStatus status;
    private Player winner;

    private Game(Builder builder) {
        this.board = builder.board;
        this.players = builder.players;
        this.dice = builder.dice;
        this.status = GameStatus.NOT_STARTED;
    }

    public void play() {
        if(players.size()<2) {
            System.out.println("Cannot start game with < 2 players.");
            return;
        }

        this.status = GameStatus.RUNNING;
        System.out.println("Game Started!");

        while(status == GameStatus.RUNNING) {
            Player currentPlayer = players.poll();
            takeTurn(currentPlayer);

            if(status == GameStatus.RUNNING) {
                players.add(currentPlayer);
            }
        }

        System.out.println("Game finished!");
        if(winner!=null) {
            System.out.println("The winner is "+ winner.getName()+"!");
        }
    }

    public void  takeTurn(Player player) {
        takeTurn(player, 0, player.getPosition());
    }

    public void takeTurn(Player player, int consecutiveSixes, int turnStartPosition) {
        int roll = dice.roll();
        System.out.println(player.getName() + "'s turn. Rolled a "+roll+".");

        if(roll == 6 && consecutiveSixes == 2) {
            player.setPosition(turnStartPosition);
            System.out.printf(
                    "%s rolled three 6s in a row. Turn forfeited, back to %d.\n",
                    player.getName(), turnStartPosition
            );
            return;
        }

        int currentPosition = player.getPosition();
        int nextPosition = currentPosition+roll;

        if(nextPosition>board.getSize()) {
            System.out.printf(
                    "Oops, %s needs to land exactly on %d. Turn skipped.%n",
                    player.getName(), board.getSize()
            );
            return;
        }

        if(nextPosition == board.getSize()) {
            player.setPosition(nextPosition);
            this.winner = player;
            this.status = GameStatus.FINISHED;
            System.out.printf(
                    "Hooray! %s reached the final square %d and won!%n",
                    player.getName(), board.getSize()
            );
            return;
        }

        int finalPosition = board.getFinalPosition(nextPosition);
        if (finalPosition > nextPosition) {
            System.out.printf(
                    "Wow! %s found a ladder at %d and climbed to %d.%n",
                    player.getName(), nextPosition, finalPosition
            );
        } else if (finalPosition < nextPosition) {
            System.out.printf(
                    "Oh no! %s was bitten by a snake at %d and slid down to %d.%n",
                    player.getName(), nextPosition, finalPosition
            );
        } else {
            System.out.printf(
                    "%s moved from %d to %d.%n",
                    player.getName(), currentPosition, finalPosition
            );
        }

        player.setPosition(finalPosition);
        if(roll == 6) {
            System.out.printf("%s rolled a 6 and gets another turn!%n", player.getName());
            takeTurn(player, consecutiveSixes+1, turnStartPosition);
        }
    }

    public static class Builder {
        private Board board;
        private Queue<Player> players;
        private Dice dice;

        public Builder setBoard(int boardSize, List<BoardEntity> boardEntityList) {
            this.board = new Board(boardSize, boardEntityList);
            return this;
        }

        public Builder setPlayers(List<String> playerNames) {
            this.players = new LinkedList<>();
            for(String playerName : playerNames) {
                players.add(new Player(playerName));
            }
            return this;
        }

        public Builder setDice(Dice dice) {
            this.dice = dice;
            return this;
        }

        public Game build() {
            if(board == null || dice == null || players == null) {
                throw new IllegalStateException("Board, Players, and Dice must be set.");
            }
            return new Game(this);
        }
    }
}
